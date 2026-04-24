package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.EmpScheduleCreateDTO;
import com.example.hello.dto.EmpScheduleUpdateDTO;
import com.example.hello.entity.EmpSchedule;
import com.example.hello.service.EmpScheduleService;
import com.example.hello.vo.EmpOptionVO;
import com.example.hello.vo.PageResultVO;
import com.example.hello.vo.ScheduleTypeVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 管理端员工排班接口。
 */
@Validated
@RestController
@RequestMapping("/schedules")
public class EmpScheduleController {

    private final EmpScheduleService empScheduleService;

    public EmpScheduleController(EmpScheduleService empScheduleService) {
        this.empScheduleService = empScheduleService;
    }

    /**
     * 分页条件查询员工排班。
     *
     * @param workDateBegin 排班起始日期（可选）
     * @param workDateEnd 排班结束日期（可选）
     * @param deptId 部门ID（可选）
     * @param empId 员工ID（可选）
     * @param scheduleType 排班类型（可选）：1可预约 2休息 3请假 4锁定 5已被预约
     * @param page 页码
     * @param pageSize 每页大小
     * @return 排班分页数据
     */
    @GetMapping
    public Result<PageResultVO<EmpSchedule>> pageQuery(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate workDateBegin,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate workDateEnd,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) Integer empId,
            @RequestParam(required = false) Integer scheduleType,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        return Result.success(empScheduleService.pageQuery(workDateBegin, workDateEnd, deptId, empId, scheduleType, page, pageSize));
    }

    /**
     * 根据ID查询排班详情。
     *
     * @param id 排班ID
     * @return 排班详情
     */
    @GetMapping("/{id}")
    public Result<EmpSchedule> findById(@PathVariable @NotNull Integer id) {
        EmpSchedule schedule = empScheduleService.findById(id);
        return schedule != null ? Result.success(schedule) : Result.error("排班不存在");
    }

    /**
     * 新增员工排班。
     *
     * @param dto 新增参数
     * @return 新增后的排班数据
     */
    @PostMapping
    public Result<EmpSchedule> create(@RequestBody @Valid EmpScheduleCreateDTO dto) {
        return Result.success(empScheduleService.create(dto));
    }

    /**
     * 修改员工排班。
     *
     * @param dto 修改参数
     * @return 修改后的排班数据
     */
    @PutMapping
    public Result<EmpSchedule> update(@RequestBody @Valid EmpScheduleUpdateDTO dto) {
        return Result.success(empScheduleService.update(dto));
    }

    /**
     * 删除员工排班。
     *
     * @param id 排班ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull Integer id) {
        boolean deleted = empScheduleService.deleteById(id);
        return deleted ? Result.success() : Result.error("排班不存在");
    }

    /**
     * 按日历查询某天排班。
     *
     * @param workDate 查询日期
     * @param deptId 部门ID（可选）
     * @param empId 员工ID（可选）
     * @return 当天排班列表
     */
    @GetMapping("/calendar")
    public Result<List<EmpSchedule>> calendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate workDate,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) Integer empId) {
        return Result.success(empScheduleService.calendar(workDate, deptId, empId));
    }

    /**
     * 查询新增排班所需员工下拉选项。
     *
     * @param deptId 部门ID（可选）
     * @return 员工下拉列表
     */
    @GetMapping("/emp-options")
    public Result<List<EmpOptionVO>> empOptions(@RequestParam(required = false) Integer deptId) {
        return Result.success(empScheduleService.empOptions(deptId));
    }

    /**
     * 查询排班类型字典。
     *
     * @return 排班类型下拉列表
     */
    @GetMapping("/types")
    public Result<List<ScheduleTypeVO>> types() {
        return Result.success(empScheduleService.listTypes());
    }
}
