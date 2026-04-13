package com.example.hello.service.impl;

import com.example.hello.dto.OrderCreateDTO;
import com.example.hello.dto.OrderRatingDTO;
import com.example.hello.dto.OrderStatusDTO;
import com.example.hello.dto.OrderUpdateDTO;
import com.example.hello.entity.BizOrder;
import com.example.hello.entity.Customer;
import com.example.hello.entity.Emp;
import com.example.hello.entity.Pet;
import com.example.hello.entity.ServiceItem;
import com.example.hello.mapper.CustomerMapper;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.mapper.OrderMapper;
import com.example.hello.mapper.PetMapper;
import com.example.hello.mapper.ServiceItemMapper;
import com.example.hello.service.OrderService;
import com.example.hello.vo.OrderCreateResultVO;
import com.example.hello.vo.OrderCustomerMini;
import com.example.hello.vo.OrderDetailVO;
import com.example.hello.vo.OrderEmpMini;
import com.example.hello.vo.OrderPetMini;
import com.example.hello.vo.OrderRatingResultVO;
import com.example.hello.vo.OrderServiceItemMini;
import com.example.hello.vo.OrderStatusPatchVO;
import com.example.hello.vo.PageResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单业务实现类。
 * <p>
 * 职责概览：
 * <ul>
 *   <li>编排 Mapper 与各实体查询，完成文档 8.x 的用例；</li>
 *   <li>维护服务师时间段冲突规则（已取消订单不占档）；</li>
 *   <li>维护简单状态机与「仅待确认可改单」「仅完成可评价」「仅完成/取消可删」等约束；</li>
 *   <li>订单号生成与唯一键冲突重试。</li>
 * </ul>
 * 说明：若订单被其它业务表引用，删除前校验应在后续迭代中补充；当前按文档做物理删除规则。
 */
@Service
public class OrderServiceImpl implements OrderService {

    /** 与接口文档示例一致的业务提示文案 */
    private static final String CONFLICT_MSG = "该服务师在所选时间段已有预约，请选择其他时间";

    private final OrderMapper orderMapper;
    private final CustomerMapper customerMapper;
    private final PetMapper petMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final EmpMapper empMapper;

    public OrderServiceImpl(OrderMapper orderMapper,
                            CustomerMapper customerMapper,
                            PetMapper petMapper,
                            ServiceItemMapper serviceItemMapper,
                            EmpMapper empMapper) {
        this.orderMapper = orderMapper;
        this.customerMapper = customerMapper;
        this.petMapper = petMapper;
        this.serviceItemMapper = serviceItemMapper;
        this.empMapper = empMapper;
    }

    /**
     * 分页查询：PageHelper 拦截后补充每条记录的 {@link BizOrder#setStatusDesc(String)}。
     * 服务时间范围由 Controller 传入日期，在此转为当天起止时刻。
     */
    @Override
    public PageResultVO<BizOrder> pageQuery(String orderNo, String customerName, String petNickname, Integer status,
                                            LocalDate beginTime, LocalDate endTime, Integer page, Integer pageSize) {
        LocalDateTime serviceTimeBegin = beginTime != null ? beginTime.atStartOfDay() : null;
        LocalDateTime serviceTimeEnd = endTime != null ? endTime.atTime(23, 59, 59) : null;
        PageHelper.startPage(page, pageSize);
        Page<BizOrder> rows = (Page<BizOrder>) orderMapper.pageQuery(orderNo, customerName, petNickname, status,
                serviceTimeBegin, serviceTimeEnd);
        for (BizOrder row : rows.getResult()) {
            row.setStatusDesc(statusToDesc(row.getStatus()));
        }
        PageResultVO<BizOrder> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    /**
     * 详情：先读订单行，再分别读客户/宠物/项目/员工并组装为 {@link OrderDetailVO}，
     * 以满足文档要求的嵌套 JSON，而非扁平字段。
     */
    @Override
    public OrderDetailVO findDetailById(Integer id) {
        BizOrder o = orderMapper.findById(id);
        if (o == null) {
            return null;
        }
        Customer c = customerMapper.findById(o.getCustomerId());
        Pet p = petMapper.findById(o.getPetId());
        ServiceItem si = serviceItemMapper.findById(o.getServiceItemId());
        Emp e = empMapper.findById(o.getEmpId());

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setServiceTime(o.getServiceTime());
        vo.setDurationMinutes(o.getDurationMinutes());
        vo.setStatus(o.getStatus());
        vo.setRating(o.getRating());
        vo.setComment(o.getComment());
        vo.setCreateTime(o.getCreateTime());
        vo.setUpdateTime(o.getUpdateTime());

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
            OrderServiceItemMini sim = new OrderServiceItemMini();
            sim.setId(si.getId());
            sim.setName(si.getName());
            sim.setPrice(si.getPrice());
            sim.setDurationMinutes(si.getDurationMinutes());
            vo.setServiceItem(sim);
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

    /**
     * 创建订单：校验关联存在、宠物归属客户、时段无冲突后插入；订单号唯一冲突则重试生成。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateResultVO create(OrderCreateDTO req) {
        requireCustomerExists(req.getCustomerId());
        Pet pet = requirePet(req.getPetId());
        if (!req.getCustomerId().equals(pet.getCustomerId())) {
            throw new IllegalArgumentException("宠物与所属客户不匹配");
        }
        ServiceItem item = requireServiceItem(req.getServiceItemId());
        requireEmp(req.getEmpId());

        int duration = req.getDurationMinutes() != null ? req.getDurationMinutes() : item.getDurationMinutes();
        assertNoTimeConflict(req.getEmpId(), req.getServiceTime(), duration, null);

        BizOrder order = new BizOrder();
        order.setCustomerId(req.getCustomerId());
        order.setPetId(req.getPetId());
        order.setServiceItemId(req.getServiceItemId());
        order.setEmpId(req.getEmpId());
        order.setServiceTime(req.getServiceTime());
        order.setDurationMinutes(duration);
        order.setStatus(1);

        insertWithUniqueOrderNo(order);

        BizOrder saved = orderMapper.findById(order.getId());
        OrderCreateResultVO vo = new OrderCreateResultVO();
        vo.setId(saved.getId());
        vo.setOrderNo(saved.getOrderNo());
        vo.setStatus(saved.getStatus());
        vo.setServiceTime(saved.getServiceTime());
        vo.setDurationMinutes(saved.getDurationMinutes());
        return vo;
    }

    /** 订单号重复（极低概率）时重试插入 */
    private void insertWithUniqueOrderNo(BizOrder order) {
        for (int i = 0; i < 8; i++) {
            order.setOrderNo(generateOrderNo());
            try {
                orderMapper.insert(order);
                return;
            } catch (DuplicateKeyException ignored) {
                // 继续换号
            }
        }
        throw new IllegalStateException("生成订单号失败，请重试");
    }

    /** 可读订单号：ORD + 时间戳 + 随机数，兼顾可读与唯一性 */
    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + ts + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    /**
     * 改单：仅待确认；合并可选字段；若更换服务项目则时长取新项目默认值；再次做冲突检测（排除自身）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizOrder update(OrderUpdateDTO req) {
        BizOrder existing = orderMapper.findById(req.getId());
        if (existing == null) {
            return null;
        }
        if (existing.getStatus() == null || existing.getStatus() != 1) {
            throw new IllegalArgumentException("仅待确认状态的订单可修改");
        }

        LocalDateTime newTime = req.getServiceTime() != null ? req.getServiceTime() : existing.getServiceTime();
        Integer newEmp = req.getEmpId() != null ? req.getEmpId() : existing.getEmpId();
        Integer newPet = req.getPetId() != null ? req.getPetId() : existing.getPetId();
        Integer newSi = req.getServiceItemId() != null ? req.getServiceItemId() : existing.getServiceItemId();

        requireEmp(newEmp);
        requireServiceItem(newSi);
        Pet pet = requirePet(newPet);
        if (!existing.getCustomerId().equals(pet.getCustomerId())) {
            throw new IllegalArgumentException("宠物与订单客户不匹配");
        }

        int duration;
        if (req.getServiceItemId() != null && !req.getServiceItemId().equals(existing.getServiceItemId())) {
            duration = serviceItemMapper.findById(newSi).getDurationMinutes();
        } else {
            duration = existing.getDurationMinutes();
        }

        assertNoTimeConflict(newEmp, newTime, duration, existing.getId());

        BizOrder u = new BizOrder();
        u.setId(existing.getId());
        u.setServiceTime(newTime);
        u.setEmpId(newEmp);
        u.setPetId(newPet);
        u.setServiceItemId(newSi);
        u.setDurationMinutes(duration);
        orderMapper.update(u);

        BizOrder out = orderMapper.findById(req.getId());
        if (out != null) {
            out.setStatusDesc(statusToDesc(out.getStatus()));
        }
        return out;
    }

    /** 状态流转：1→2/4；2→3/4 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderStatusPatchVO updateStatus(Integer id, OrderStatusDTO dto) {
        BizOrder existing = orderMapper.findById(id);
        if (existing == null) {
            return null;
        }
        int from = existing.getStatus();
        int to = dto.getStatus();
        if (!isValidTransition(from, to)) {
            throw new IllegalArgumentException("订单状态不允许此变更");
        }
        orderMapper.updateStatus(id, to);
        BizOrder fresh = orderMapper.findById(id);
        OrderStatusPatchVO vo = new OrderStatusPatchVO();
        vo.setId(fresh.getId());
        vo.setStatus(fresh.getStatus());
        vo.setStatusDesc(statusToDesc(fresh.getStatus()));
        vo.setUpdateTime(fresh.getUpdateTime());
        return vo;
    }

    /** 评价：仅已完成（3） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderRatingResultVO rate(Integer id, OrderRatingDTO dto) {
        BizOrder existing = orderMapper.findById(id);
        if (existing == null) {
            return null;
        }
        if (existing.getStatus() == null || existing.getStatus() != 3) {
            throw new IllegalArgumentException("仅已完成订单可评价");
        }
        orderMapper.updateRating(id, dto.getRating(), dto.getComment());
        BizOrder fresh = orderMapper.findById(id);
        OrderRatingResultVO vo = new OrderRatingResultVO();
        vo.setId(fresh.getId());
        vo.setRating(fresh.getRating());
        vo.setComment(fresh.getComment());
        vo.setUpdateTime(fresh.getUpdateTime());
        return vo;
    }

    /** 删除：仅已完成或已取消（文档 8.7 业务自控，此处按常见规则收窄） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Integer id) {
        BizOrder existing = orderMapper.findById(id);
        if (existing == null) {
            return false;
        }
        Integer s = existing.getStatus();
        if (s == null || (s != 3 && s != 4)) {
            throw new IllegalArgumentException("仅已完成或已取消的订单可删除");
        }
        return orderMapper.deleteById(id) > 0;
    }

    /**
     * 冲突判定：新区间 [rangeStart, rangeEnd) 与库中未取消订单区间是否相交。
     * rangeEnd = rangeStart + durationMinutes。
     */
    private void assertNoTimeConflict(Integer empId, LocalDateTime rangeStart, int durationMinutes, Integer excludeOrderId) {
        LocalDateTime rangeEnd = rangeStart.plusMinutes(durationMinutes);
        if (orderMapper.countEmpTimeConflict(empId, rangeStart, rangeEnd, excludeOrderId) > 0) {
            throw new IllegalArgumentException(CONFLICT_MSG);
        }
    }

    private static boolean isValidTransition(int from, int to) {
        if (from == 1) {
            return to == 2 || to == 4;
        }
        if (from == 2) {
            return to == 3 || to == 4;
        }
        return false;
    }

    /** 列表与 PATCH 响应共用的状态文案 */
    private static String statusToDesc(Integer status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case 1 -> "待确认";
            case 2 -> "进行中";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }

    private void requireCustomerExists(Integer customerId) {
        if (customerMapper.findById(customerId) == null) {
            throw new IllegalArgumentException("客户不存在");
        }
    }

    private Pet requirePet(Integer petId) {
        Pet pet = petMapper.findById(petId);
        if (pet == null) {
            throw new IllegalArgumentException("宠物不存在");
        }
        return pet;
    }

    private ServiceItem requireServiceItem(Integer serviceItemId) {
        ServiceItem item = serviceItemMapper.findById(serviceItemId);
        if (item == null) {
            throw new IllegalArgumentException("服务项目不存在");
        }
        return item;
    }

    private void requireEmp(Integer empId) {
        if (empMapper.findById(empId) == null) {
            throw new IllegalArgumentException("服务师不存在");
        }
    }
}
