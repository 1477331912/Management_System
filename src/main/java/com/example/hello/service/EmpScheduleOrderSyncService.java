package com.example.hello.service;

import java.time.LocalDateTime;

/**
 * 订单写入后同步员工排班类型：与 {@code order} 关联的 {@code emp_schedule} 行由「可预约」变为「已被预约」，
 * 取消或改期释放旧时段时再改回「可预约」。
 * <p>
 * 业务约定：订单区间 {@code [rangeStart, rangeEnd]} 须落在某条排班的 {@code [start_time, end_time]} 内（含边界），
 * 多行同时包含时更新其中时间跨度最短的一行。
 */
public interface EmpScheduleOrderSyncService {

    /**
     * 将满足条件的排班由可预约(1)更新为已被预约(5)。
     *
     * @param empId      服务师（员工）ID，对应 {@code emp_schedule.emp_id}
     * @param rangeStart 订单服务开始时间
     * @param rangeEnd   订单服务结束时间
     */
    void markBooked(Integer empId, LocalDateTime rangeStart, LocalDateTime rangeEnd);

    /**
     * 将满足条件的排班由已被预约(5)恢复为可预约(1)，用于用户/管理端取消或改期前释放原时段。
     *
     * @param empId      服务师（员工）ID
     * @param rangeStart 原订单服务开始时间
     * @param rangeEnd   原订单服务结束时间
     */
    void releaseBooked(Integer empId, LocalDateTime rangeStart, LocalDateTime rangeEnd);
}
