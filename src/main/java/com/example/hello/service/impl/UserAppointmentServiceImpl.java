package com.example.hello.service.impl;

import com.example.hello.dto.UserAppointmentCancelDTO;
import com.example.hello.dto.UserAppointmentCreateDTO;
import com.example.hello.dto.UserAppointmentRescheduleDTO;
import com.example.hello.dto.UserPetCreateDTO;
import com.example.hello.entity.BizOrder;
import com.example.hello.entity.Customer;
import com.example.hello.entity.Emp;
import com.example.hello.entity.EmpSchedule;
import com.example.hello.entity.Pet;
import com.example.hello.entity.ServiceItem;
import com.example.hello.mapper.CustomerMapper;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.mapper.EmpScheduleMapper;
import com.example.hello.mapper.OrderMapper;
import com.example.hello.mapper.PetMapper;
import com.example.hello.mapper.ServiceItemMapper;
import com.example.hello.service.EmpScheduleOrderSyncService;
import com.example.hello.service.UserAppointmentService;
import com.example.hello.vo.AvailableSlotVO;
import com.example.hello.vo.EmpOptionVO;
import com.example.hello.vo.OrderCreateResultVO;
import com.example.hello.vo.OrderCustomerMini;
import com.example.hello.vo.OrderDetailVO;
import com.example.hello.vo.OrderEmpMini;
import com.example.hello.vo.OrderPetMini;
import com.example.hello.vo.OrderServiceItemMini;
import com.example.hello.vo.PageResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户端预约业务实现。
 */
@Service
public class UserAppointmentServiceImpl implements UserAppointmentService {

    private static final String CONFLICT_MSG = "该服务师在所选时间段已有预约，请选择其他时间";

    private final CustomerMapper customerMapper;
    private final PetMapper petMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final EmpMapper empMapper;
    private final EmpScheduleMapper empScheduleMapper;
    private final OrderMapper orderMapper;
    /** 订单与 {@code emp_schedule} 类型联动（预约成功标「已被预约」，取消/改期释放）。 */
    private final EmpScheduleOrderSyncService empScheduleOrderSyncService;

    public UserAppointmentServiceImpl(CustomerMapper customerMapper,
                                      PetMapper petMapper,
                                      ServiceItemMapper serviceItemMapper,
                                      EmpMapper empMapper,
                                      EmpScheduleMapper empScheduleMapper,
                                      OrderMapper orderMapper,
                                      EmpScheduleOrderSyncService empScheduleOrderSyncService) {
        this.customerMapper = customerMapper;
        this.petMapper = petMapper;
        this.serviceItemMapper = serviceItemMapper;
        this.empMapper = empMapper;
        this.empScheduleMapper = empScheduleMapper;
        this.orderMapper = orderMapper;
        this.empScheduleOrderSyncService = empScheduleOrderSyncService;
    }

    @Override
    public List<Pet> myPets(Integer userAccountId) {
        Customer customer = requireCustomer(userAccountId);
        return petMapper.pageQuery(null, null, null, customer.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pet createMyPet(Integer userAccountId, UserPetCreateDTO dto) {
        Customer customer = requireCustomer(userAccountId);
        Pet p = new Pet();
        p.setNickname(dto.getNickname());
        p.setBreed(dto.getBreed());
        p.setGender(dto.getGender());
        p.setBirthday(dto.getBirthday());
        p.setWeight(dto.getWeight());
        p.setVaccineStatus(dto.getVaccineStatus() != null ? dto.getVaccineStatus() : 0);
        p.setAllergyHistory(dto.getAllergyHistory());
        p.setImage(dto.getImage());
        p.setCustomerId(customer.getId());
        petMapper.insert(p);
        return petMapper.findById(p.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMyPet(Integer userAccountId, Integer petId) {
        Customer customer = requireCustomer(userAccountId);
        Pet p = petMapper.findById(petId);
        if (p == null) {
            return false;
        }
        if (!customer.getId().equals(p.getCustomerId())) {
            throw new IllegalArgumentException("无权删除该宠物");
        }
        return petMapper.deleteById(petId) > 0;
    }

    @Override
    public List<EmpOptionVO> empOptions(Integer serviceItemId, LocalDate date) {
        ServiceItem item = requireServiceItem(serviceItemId);
        if (item.getDeptId() == null) {
            throw new IllegalArgumentException("服务项目未配置所属部门，无法查询服务师");
        }
        if (date != null) {
            return empMapper.listOptionsByDeptIdAndDate(item.getDeptId(), date);
        }
        return empMapper.listOptionsByDeptId(item.getDeptId());
    }

    @Override
    public List<AvailableSlotVO> availableSlots(LocalDate date, Integer serviceItemId, Integer empId) {
        ServiceItem item = requireServiceItem(serviceItemId);
        int durationMinutes = item.getDurationMinutes();

        List<EmpSchedule> schedules;
        if (empId != null) {
            // 指定服务师时，按员工+日期直接查询排班。
            requireEmp(empId);
            schedules = empScheduleMapper.listAvailableSchedules(date, empId);
        } else {
            // 未指定服务师时，先按服务项目所属部门筛选员工，再按员工集合+日期查询排班。
            if (item.getDeptId() == null) {
                throw new IllegalArgumentException("服务项目未配置所属部门，无法计算可预约服务师");
            }
            List<Emp> emps = empMapper.listByDeptId(item.getDeptId());
            if (emps == null || emps.isEmpty()) {
                return new ArrayList<>();
            }
            List<Integer> empIds = emps.stream().map(Emp::getId).toList();
            schedules = empScheduleMapper.listAvailableSchedulesByEmpIds(date, empIds);
        }

        List<AvailableSlotVO> slots = new ArrayList<>();
        for (EmpSchedule s : schedules) {
            AvailableSlotVO vo = new AvailableSlotVO();
            vo.setEmpId(s.getEmpId());
            vo.setEmpName(s.getEmpName());
            vo.setStartTime(s.getStartTime());
            vo.setEndTime(s.getEndTime());
            vo.setDurationMinutes(durationMinutes);
            slots.add(vo);
        }
        return slots;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateResultVO create(Integer userAccountId, UserAppointmentCreateDTO dto) {
        Customer customer = requireCustomer(userAccountId);
        Pet pet = requirePet(dto.getPetId());
        if (!customer.getId().equals(pet.getCustomerId())) {
            throw new IllegalArgumentException("只能为当前账号名下宠物预约");
        }
        ServiceItem item = requireServiceItem(dto.getServiceItemId());
        requireEmp(dto.getEmpId());

        int duration = dto.getDurationMinutes() != null ? dto.getDurationMinutes() : item.getDurationMinutes();
        assertNoConflict(dto.getEmpId(), dto.getServiceTime(), duration, null);

        BizOrder order = new BizOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserAccountId(userAccountId);
        order.setCustomerId(customer.getId());
        order.setPetId(dto.getPetId());
        order.setServiceItemId(dto.getServiceItemId());
        order.setEmpId(dto.getEmpId());
        order.setServiceTime(dto.getServiceTime());
        order.setServiceEndTime(dto.getServiceTime().plusMinutes(duration));
        order.setDurationMinutes(duration);
        order.setStatus(1);
        insertWithRetry(order);
        // 订单时段落在某条可预约排班区间内时，将该行 schedule_type 1→5
        empScheduleOrderSyncService.markBooked(order.getEmpId(), order.getServiceTime(), order.getServiceEndTime());

        BizOrder saved = orderMapper.findById(order.getId());
        OrderCreateResultVO vo = new OrderCreateResultVO();
        vo.setId(saved.getId());
        vo.setOrderNo(saved.getOrderNo());
        vo.setStatus(saved.getStatus());
        vo.setServiceTime(saved.getServiceTime());
        vo.setDurationMinutes(saved.getDurationMinutes());
        return vo;
    }

    @Override
    public PageResultVO<BizOrder> myOrders(Integer userAccountId, String orderNo, Integer status, LocalDate beginTime,
                                           LocalDate endTime, Integer page, Integer pageSize) {
        LocalDateTime begin = beginTime != null ? beginTime.atStartOfDay() : null;
        LocalDateTime end = endTime != null ? endTime.atTime(23, 59, 59) : null;
        PageHelper.startPage(page, pageSize);
        Page<BizOrder> rows = (Page<BizOrder>) orderMapper.pageQueryByUser(userAccountId, orderNo, status, begin, end);
        for (BizOrder row : rows.getResult()) {
            row.setStatusDesc(statusToDesc(row.getStatus()));
        }
        PageResultVO<BizOrder> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    @Override
    public OrderDetailVO myOrderDetail(Integer userAccountId, Integer orderId) {
        BizOrder order = orderMapper.findByIdAndUser(orderId, userAccountId);
        if (order == null) {
            return null;
        }
        Customer c = customerMapper.findById(order.getCustomerId());
        Pet p = petMapper.findById(order.getPetId());
        ServiceItem si = serviceItemMapper.findById(order.getServiceItemId());
        Emp e = empMapper.findById(order.getEmpId());

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setServiceTime(order.getServiceTime());
        vo.setDurationMinutes(order.getDurationMinutes());
        vo.setStatus(order.getStatus());
        vo.setRating(order.getRating());
        vo.setComment(order.getComment());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        if (c != null) {
            OrderCustomerMini cm = new OrderCustomerMini();
            cm.setId(c.getId());
            cm.setName(c.getName());
            cm.setPhone(c.getPhone());
            vo.setCustomer(cm);
        }
        if (p != null) {
            OrderPetMini pm = new OrderPetMini();
            pm.setId(p.getId());
            pm.setNickname(p.getNickname());
            pm.setBreed(p.getBreed());
            vo.setPet(pm);
        }
        if (si != null) {
            OrderServiceItemMini sm = new OrderServiceItemMini();
            sm.setId(si.getId());
            sm.setName(si.getName());
            sm.setPrice(si.getPrice());
            sm.setDurationMinutes(si.getDurationMinutes());
            vo.setServiceItem(sm);
        }
        if (e != null) {
            OrderEmpMini em = new OrderEmpMini();
            em.setId(e.getId());
            em.setName(e.getName());
            em.setPhone(e.getPhone());
            vo.setEmp(em);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizOrder cancel(Integer userAccountId, Integer orderId, UserAppointmentCancelDTO dto) {
        BizOrder existing = orderMapper.findByIdAndUser(orderId, userAccountId);
        if (existing == null) {
            return null;
        }
        if (existing.getStatus() != 1 && existing.getStatus() != 2) {
            throw new IllegalArgumentException("当前订单状态不可取消");
        }
        String reason = dto != null && StringUtils.hasText(dto.getCancelReason()) ? dto.getCancelReason() : "用户取消";
        // 先释放排班占用，再将订单置为已取消
        empScheduleOrderSyncService.releaseBooked(existing.getEmpId(), existing.getServiceTime(), orderServiceEnd(existing));
        orderMapper.updateCancel(orderId, reason);
        BizOrder out = orderMapper.findByIdAndUser(orderId, userAccountId);
        out.setStatusDesc(statusToDesc(out.getStatus()));
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizOrder reschedule(Integer userAccountId, Integer orderId, UserAppointmentRescheduleDTO dto) {
        BizOrder existing = orderMapper.findByIdAndUser(orderId, userAccountId);
        if (existing == null) {
            return null;
        }
        if (existing.getStatus() != 1) {
            throw new IllegalArgumentException("仅已预约订单可改期");
        }
        LocalDateTime newTime = dto.getServiceTime() != null ? dto.getServiceTime() : existing.getServiceTime();
        Integer newEmpId = dto.getEmpId() != null ? dto.getEmpId() : existing.getEmpId();
        requireEmp(newEmpId);
        assertNoConflict(newEmpId, newTime, existing.getDurationMinutes(), existing.getId());

        // 释放原预约时段对应的排班类型 5→1
        empScheduleOrderSyncService.releaseBooked(existing.getEmpId(), existing.getServiceTime(), orderServiceEnd(existing));

        BizOrder patch = new BizOrder();
        patch.setId(existing.getId());
        patch.setServiceTime(newTime);
        patch.setServiceEndTime(newTime.plusMinutes(existing.getDurationMinutes()));
        patch.setEmpId(newEmpId);
        patch.setPetId(existing.getPetId());
        patch.setServiceItemId(existing.getServiceItemId());
        patch.setDurationMinutes(existing.getDurationMinutes());
        orderMapper.update(patch);
        // 新订单时段落在可预约排班区间内时 1→5
        empScheduleOrderSyncService.markBooked(newEmpId, newTime, newTime.plusMinutes(existing.getDurationMinutes()));

        BizOrder out = orderMapper.findByIdAndUser(orderId, userAccountId);
        out.setStatusDesc(statusToDesc(out.getStatus()));
        return out;
    }

    /**
     * 解析订单结束时刻，与排班释放/同步时使用的 {@code rangeEnd} 一致；无落库字段时用开始时间+时长。
     *
     * @param o 当前订单实体
     * @return 服务结束时间
     */
    private LocalDateTime orderServiceEnd(BizOrder o) {
        if (o.getServiceEndTime() != null) {
            return o.getServiceEndTime();
        }
        return o.getServiceTime().plusMinutes(o.getDurationMinutes() != null ? o.getDurationMinutes() : 0);
    }

    private void insertWithRetry(BizOrder order) {
        for (int i = 0; i < 8; i++) {
            try {
                orderMapper.insert(order);
                return;
            } catch (DuplicateKeyException ex) {
                order.setOrderNo(generateOrderNo());
            }
        }
        throw new IllegalStateException("生成订单号失败，请重试");
    }

    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + ts + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private String statusToDesc(Integer status) {
        return switch (status) {
            case 1 -> "已预约";
            case 2 -> "进行中";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }

    private void assertNoConflict(Integer empId, LocalDateTime start, Integer durationMinutes, Integer excludeOrderId) {
        LocalDateTime end = start.plusMinutes(durationMinutes);
        if (orderMapper.countEmpTimeConflict(empId, start, end, excludeOrderId) > 0) {
            throw new IllegalArgumentException(CONFLICT_MSG);
        }
    }

    private Customer requireCustomer(Integer userAccountId) {
        Customer c = customerMapper.findByUserAccountId(userAccountId);
        if (c == null) {
            throw new IllegalArgumentException("当前账号未绑定客户档案");
        }
        return c;
    }

    private Pet requirePet(Integer petId) {
        Pet p = petMapper.findById(petId);
        if (p == null) {
            throw new IllegalArgumentException("宠物不存在");
        }
        return p;
    }

    private ServiceItem requireServiceItem(Integer serviceItemId) {
        ServiceItem item = serviceItemMapper.findById(serviceItemId);
        if (item == null) {
            throw new IllegalArgumentException("服务项目不存在");
        }
        return item;
    }

    private void requireEmp(Integer empId) {
        Emp emp = empMapper.findById(empId);
        if (emp == null) {
            throw new IllegalArgumentException("服务师不存在");
        }
    }
}
