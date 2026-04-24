package com.example.hello.mapper;

import com.example.hello.entity.EmpSchedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工排班表 Mapper。
 */
public interface EmpScheduleMapper {
    List<EmpSchedule> pageQuery(@Param("workDateBegin") LocalDate workDateBegin,
                                @Param("workDateEnd") LocalDate workDateEnd,
                                @Param("deptId") Integer deptId,
                                @Param("empId") Integer empId,
                                @Param("scheduleType") Integer scheduleType);

    EmpSchedule findById(@Param("id") Integer id);

    int insert(EmpSchedule schedule);

    int update(EmpSchedule schedule);

    int deleteById(@Param("id") Integer id);

    int countTimeOverlap(@Param("empId") Integer empId,
                         @Param("workDate") LocalDate workDate,
                         @Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime,
                         @Param("excludeId") Integer excludeId);

    int countActiveOrderInRange(@Param("empId") Integer empId,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd);

    List<EmpSchedule> calendar(@Param("workDate") LocalDate workDate,
                               @Param("deptId") Integer deptId,
                               @Param("empId") Integer empId);

    List<EmpSchedule> listAvailableSchedules(@Param("workDate") LocalDate workDate, @Param("empId") Integer empId);

    /**
     * 按员工ID集合查询某天可用排班。
     *
     * @param workDate 查询日期
     * @param empIds   员工ID集合
     * @return 可用排班列表
     */
    List<EmpSchedule> listAvailableSchedulesByEmpIds(@Param("workDate") LocalDate workDate,
                                                     @Param("empIds") List<Integer> empIds);

    /**
     * 当订单区间 {@code [rangeStart, rangeEnd]} 落在某条排班 {@code [start_time, end_time]} 内（含边界）时，
     * 将该条可预约(1)改为已被预约(5)。若多行均包含，则更新时间跨度最短的一行。
     *
     * @return 受影响的行数（0 表示无匹配排班）
     */
    int updateToBookedContained(@Param("empId") Integer empId,
                                @Param("workDate") LocalDate workDate,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd);

    /**
     * 在订单区间被排班区间包含的前提下，将已被预约(5)改回可预约(1)，用于取消或改期释放原时段。
     * 与 {@link #updateToBookedContained} 使用相同的匹配规则，优先命中时间跨度最短的一行。
     *
     * @return 受影响的行数
     */
    int updateToBookableContained(@Param("empId") Integer empId,
                                  @Param("workDate") LocalDate workDate,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd);
}
