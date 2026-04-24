package com.example.hello.service.impl;

import com.example.hello.dto.EmpScheduleCreateDTO;
import com.example.hello.dto.EmpScheduleUpdateDTO;
import com.example.hello.entity.Emp;
import com.example.hello.entity.EmpSchedule;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.mapper.EmpScheduleMapper;
import com.example.hello.service.EmpScheduleService;
import com.example.hello.vo.EmpOptionVO;
import com.example.hello.vo.PageResultVO;
import com.example.hello.vo.ScheduleTypeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 管理端员工排班业务实现。
 */
@Service
public class EmpScheduleServiceImpl implements EmpScheduleService {

    private final EmpScheduleMapper empScheduleMapper;
    private final EmpMapper empMapper;

    public EmpScheduleServiceImpl(EmpScheduleMapper empScheduleMapper, EmpMapper empMapper) {
        this.empScheduleMapper = empScheduleMapper;
        this.empMapper = empMapper;
    }

    /**
     * 分页条件查询排班记录。
     *
     * @param workDateBegin 排班起始日期
     * @param workDateEnd 排班结束日期
     * @param deptId 部门ID
     * @param empId 员工ID
     * @param scheduleType 排班类型
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public PageResultVO<EmpSchedule> pageQuery(LocalDate workDateBegin, LocalDate workDateEnd, Integer deptId, Integer empId,
                                               Integer scheduleType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<EmpSchedule> rows = (Page<EmpSchedule>) empScheduleMapper.pageQuery(workDateBegin, workDateEnd, deptId, empId, scheduleType);
        PageResultVO<EmpSchedule> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    /**
     * 按ID查询排班详情。
     *
     * @param id 排班ID
     * @return 排班详情
     */
    @Override
    public EmpSchedule findById(Integer id) {
        return empScheduleMapper.findById(id);
    }

    /**
     * 新增排班，并进行员工存在、时间合法性及重叠冲突校验。
     *
     * @param dto 新增参数
     * @return 新增后的排班
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmpSchedule create(EmpScheduleCreateDTO dto) {
        validateEmpExists(dto.getEmpId());
        validateRange(dto.getWorkDate(), dto.getStartTime(), dto.getEndTime());
        assertNoScheduleOverlap(dto.getEmpId(), dto.getWorkDate(), dto.getStartTime(), dto.getEndTime(), null);

        EmpSchedule schedule = new EmpSchedule();
        schedule.setEmpId(dto.getEmpId());
        schedule.setWorkDate(dto.getWorkDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setScheduleType(dto.getScheduleType());
        schedule.setMaxAppointments(dto.getMaxAppointments() == null ? 0 : dto.getMaxAppointments());
        schedule.setRemark(dto.getRemark());
        empScheduleMapper.insert(schedule);
        return empScheduleMapper.findById(schedule.getId());
    }

    /**
     * 修改排班，并校验是否影响已有有效预约订单。
     *
     * @param dto 修改参数
     * @return 修改后的排班
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmpSchedule update(EmpScheduleUpdateDTO dto) {
        EmpSchedule existing = empScheduleMapper.findById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("排班不存在");
        }
        validateEmpExists(dto.getEmpId());
        validateRange(dto.getWorkDate(), dto.getStartTime(), dto.getEndTime());
        assertNoScheduleOverlap(dto.getEmpId(), dto.getWorkDate(), dto.getStartTime(), dto.getEndTime(), dto.getId());

        int activeOrders = empScheduleMapper.countActiveOrderInRange(existing.getEmpId(), existing.getStartTime(), existing.getEndTime());
        if (activeOrders > 0) {
            boolean sameEmp = existing.getEmpId().equals(dto.getEmpId());
            boolean fullCover = !dto.getStartTime().isAfter(existing.getStartTime())
                    && !dto.getEndTime().isBefore(existing.getEndTime());
            if (!sameEmp || !fullCover) {
                throw new IllegalStateException("该排班调整后将影响已预约订单");
            }
        }

        EmpSchedule schedule = new EmpSchedule();
        schedule.setId(dto.getId());
        schedule.setEmpId(dto.getEmpId());
        schedule.setWorkDate(dto.getWorkDate());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setScheduleType(dto.getScheduleType());
        schedule.setMaxAppointments(dto.getMaxAppointments() == null ? 0 : dto.getMaxAppointments());
        schedule.setRemark(dto.getRemark());
        empScheduleMapper.update(schedule);
        return empScheduleMapper.findById(dto.getId());
    }

    /**
     * 删除排班。若排班时段内存在未取消订单则禁止删除。
     *
     * @param id 排班ID
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Integer id) {
        EmpSchedule existing = empScheduleMapper.findById(id);
        if (existing == null) {
            return false;
        }
        int activeOrders = empScheduleMapper.countActiveOrderInRange(existing.getEmpId(), existing.getStartTime(), existing.getEndTime());
        if (activeOrders > 0) {
            throw new IllegalStateException("该排班已有关联预约，不可删除");
        }
        return empScheduleMapper.deleteById(id) > 0;
    }

    /**
     * 查询日历视图排班数据。
     *
     * @param workDate 指定日期
     * @param deptId 部门ID（可选）
     * @param empId 员工ID（可选）
     * @return 排班列表
     */
    @Override
    public List<EmpSchedule> calendar(LocalDate workDate, Integer deptId, Integer empId) {
        return empScheduleMapper.calendar(workDate, deptId, empId);
    }

    /**
     * 查询员工下拉数据。传 deptId 则按部门筛选，不传则返回全部员工。
     *
     * @param deptId 部门ID（可选）
     * @return 员工下拉列表
     */
    @Override
    public List<EmpOptionVO> empOptions(Integer deptId) {
        if (deptId == null) {
            return empMapper.listAll().stream()
                    .map(emp -> {
                        EmpOptionVO vo = new EmpOptionVO();
                        vo.setEmpId(emp.getId());
                        vo.setEmpName(emp.getName());
                        vo.setDeptId(emp.getDeptId());
                        vo.setDeptName(emp.getDeptName());
                        return vo;
                    })
                    .toList();
        }
        return empMapper.listOptionsByDeptId(deptId);
    }

    /**
     * 返回排班类型字典。
     *
     * @return 排班类型列表
     */
    @Override
    public List<ScheduleTypeVO> listTypes() {
        return List.of(
                new ScheduleTypeVO(1, "可预约"),
                new ScheduleTypeVO(2, "休息"),
                new ScheduleTypeVO(3, "请假"),
                new ScheduleTypeVO(4, "锁定"),
                new ScheduleTypeVO(5, "已被预约")
        );
    }

    /**
     * 校验员工是否存在。
     *
     * @param empId 员工ID
     */
    private void validateEmpExists(Integer empId) {
        Emp emp = empMapper.findById(empId);
        if (emp == null) {
            throw new IllegalArgumentException("员工不存在");
        }
    }

    /**
     * 校验排班日期与时间区间是否合法。
     *
     * @param workDate 排班日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateRange(LocalDate workDate, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        if (!startTime.toLocalDate().equals(workDate) || !endTime.toLocalDate().equals(workDate)) {
            throw new IllegalArgumentException("开始/结束时间必须与排班日期一致");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("排班开始时间必须早于结束时间");
        }
    }

    /**
     * 校验同员工同日排班时间是否重叠。
     *
     * @param empId 员工ID
     * @param workDate 排班日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 修改时排除的排班ID
     */
    private void assertNoScheduleOverlap(Integer empId, LocalDate workDate, java.time.LocalDateTime startTime,
                                         java.time.LocalDateTime endTime, Integer excludeId) {
        if (empScheduleMapper.countTimeOverlap(empId, workDate, startTime, endTime, excludeId) > 0) {
            throw new IllegalArgumentException("排班时间与现有班次冲突");
        }
    }
}
