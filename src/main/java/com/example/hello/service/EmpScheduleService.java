package com.example.hello.service;

import com.example.hello.dto.EmpScheduleCreateDTO;
import com.example.hello.dto.EmpScheduleUpdateDTO;
import com.example.hello.entity.EmpSchedule;
import com.example.hello.vo.EmpOptionVO;
import com.example.hello.vo.PageResultVO;
import com.example.hello.vo.ScheduleTypeVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 管理端员工排班业务。
 */
public interface EmpScheduleService {
    /**
     * 分页条件查询排班记录。
     *
     * @param workDateBegin 排班起始日期
     * @param workDateEnd 排班结束日期
     * @param deptId 部门ID（可选）
     * @param empId 员工ID（可选）
     * @param scheduleType 排班类型（可选）：1可预约 2休息 3请假 4锁定 5已被预约
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页排班结果
     */
    PageResultVO<EmpSchedule> pageQuery(LocalDate workDateBegin, LocalDate workDateEnd, Integer deptId, Integer empId,
                                        Integer scheduleType, Integer page, Integer pageSize);

    /**
     * 根据排班ID查询详情。
     *
     * @param id 排班ID
     * @return 排班详情，不存在返回 null
     */
    EmpSchedule findById(Integer id);

    /**
     * 新增排班。
     *
     * @param dto 新增参数
     * @return 新增后的排班记录
     */
    EmpSchedule create(EmpScheduleCreateDTO dto);

    /**
     * 修改排班。
     *
     * @param dto 修改参数
     * @return 修改后的排班记录
     */
    EmpSchedule update(EmpScheduleUpdateDTO dto);

    /**
     * 按ID删除排班。
     *
     * @param id 排班ID
     * @return 是否删除成功
     */
    boolean deleteById(Integer id);

    /**
     * 查询某日排班看板数据。
     *
     * @param workDate 指定日期
     * @param deptId 部门ID（可选）
     * @param empId 员工ID（可选）
     * @return 排班列表
     */
    List<EmpSchedule> calendar(LocalDate workDate, Integer deptId, Integer empId);

    /**
     * 查询员工下拉选项（可按部门筛选）。
     *
     * @param deptId 部门ID（可选）
     * @return 员工下拉数据
     */
    List<EmpOptionVO> empOptions(Integer deptId);

    /**
     * 获取排班类型字典。
     *
     * @return 排班类型列表
     */
    List<ScheduleTypeVO> listTypes();
}
