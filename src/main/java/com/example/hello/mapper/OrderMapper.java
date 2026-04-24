package com.example.hello.mapper;

import com.example.hello.entity.BizOrder;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单表持久层接口。
 * <p>
 * 用途：封装对 {@code `order`} 表的 CRUD 及分页查询、冲突统计 SQL；
 * 具体语句在 XML 中编写，便于处理保留字表名与多表连接。
 */
public interface OrderMapper {

    List<BizOrder> pageQuery(@Param("orderNo") String orderNo,
                             @Param("customerName") String customerName,
                             @Param("petNickname") String petNickname,
                             @Param("status") Integer status,
                             @Param("serviceTimeBegin") LocalDateTime serviceTimeBegin,
                             @Param("serviceTimeEnd") LocalDateTime serviceTimeEnd);

    BizOrder findById(@Param("id") Integer id);

    int insert(BizOrder order);

    int update(BizOrder order);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    int updateCancel(@Param("id") Integer id, @Param("cancelReason") String cancelReason);

    int updateRating(@Param("id") Integer id, @Param("rating") Integer rating, @Param("comment") String comment);

    int deleteById(@Param("id") Integer id);
    List<BizOrder> pageQueryByUser(@Param("userAccountId") Integer userAccountId,
                                   @Param("orderNo") String orderNo,
                                   @Param("status") Integer status,
                                   @Param("serviceTimeBegin") LocalDateTime serviceTimeBegin,
                                   @Param("serviceTimeEnd") LocalDateTime serviceTimeEnd);
    BizOrder findByIdAndUser(@Param("id") Integer id, @Param("userAccountId") Integer userAccountId);

    /**
     * 统计同一服务师在时间段上与 [rangeStart, rangeEnd) 重叠且未取消的订单数。
     *
     * @param excludeOrderId 修改订单时排除当前行；新建传 null
     */
    int countEmpTimeConflict(@Param("empId") Integer empId,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd,
                             @Param("excludeOrderId") Integer excludeOrderId);
}
