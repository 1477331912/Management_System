package com.example.hello.service.impl;

import com.example.hello.mapper.EmpScheduleMapper;
import com.example.hello.service.EmpScheduleOrderSyncService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * {@link EmpScheduleOrderSyncService} 的实现：仅调用 Mapper 做单行 UPDATE，不拆分排班时间段。
 */
@Service
public class EmpScheduleOrderSyncServiceImpl implements EmpScheduleOrderSyncService {

    private final EmpScheduleMapper empScheduleMapper;

    /**
     * @param empScheduleMapper 员工排班表数据访问
     */
    public EmpScheduleOrderSyncServiceImpl(EmpScheduleMapper empScheduleMapper) {
        this.empScheduleMapper = empScheduleMapper;
    }

    /**
     * 在 {@code work_date = rangeStart 的日期} 下，若订单区间被某行排班区间包含，则将该「可预约」行标为「已被预约」。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markBooked(Integer empId, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (empId == null || rangeStart == null || rangeEnd == null || !rangeStart.isBefore(rangeEnd)) {
            return;
        }
        empScheduleMapper.updateToBookedContained(empId, rangeStart.toLocalDate(), rangeStart, rangeEnd);
    }

    /**
     * 在包含订单区间的「已被预约」行中，将匹配到的一行恢复为「可预约」。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseBooked(Integer empId, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (empId == null || rangeStart == null || rangeEnd == null || !rangeStart.isBefore(rangeEnd)) {
            return;
        }
        empScheduleMapper.updateToBookableContained(empId, rangeStart.toLocalDate(), rangeStart, rangeEnd);
    }
}
