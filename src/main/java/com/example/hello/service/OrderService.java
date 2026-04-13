package com.example.hello.service;

import com.example.hello.dto.OrderCreateDTO;
import com.example.hello.dto.OrderRatingDTO;
import com.example.hello.dto.OrderStatusDTO;
import com.example.hello.dto.OrderUpdateDTO;
import com.example.hello.entity.BizOrder;
import com.example.hello.vo.OrderCreateResultVO;
import com.example.hello.vo.OrderDetailVO;
import com.example.hello.vo.OrderRatingResultVO;
import com.example.hello.vo.OrderStatusPatchVO;
import com.example.hello.vo.PageResultVO;

import java.time.LocalDate;

/**
 * 订单业务接口层。
 * <p>
 * 用途：对 Controller 暴露用例级方法（分页、详情、创建、修改、改状态、评价、删除），
 * 隐藏事务、冲突检测、状态机与多表校验细节，便于单元测试与替换实现。
 */
public interface OrderService {

    PageResultVO<BizOrder> pageQuery(String orderNo, String customerName, String petNickname, Integer status,
                                     LocalDate beginTime, LocalDate endTime, Integer page, Integer pageSize);

    OrderDetailVO findDetailById(Integer id);

    OrderCreateResultVO create(OrderCreateDTO req);

    BizOrder update(OrderUpdateDTO req);

    OrderStatusPatchVO updateStatus(Integer id, OrderStatusDTO dto);

    OrderRatingResultVO rate(Integer id, OrderRatingDTO dto);

    boolean delete(Integer id);
}
