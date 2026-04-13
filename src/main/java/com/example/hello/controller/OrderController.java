package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.OrderCreateDTO;
import com.example.hello.dto.OrderRatingDTO;
import com.example.hello.dto.OrderStatusDTO;
import com.example.hello.dto.OrderUpdateDTO;
import com.example.hello.entity.BizOrder;
import com.example.hello.service.OrderService;
import com.example.hello.vo.OrderCreateResultVO;
import com.example.hello.vo.OrderDetailVO;
import com.example.hello.vo.OrderRatingResultVO;
import com.example.hello.vo.OrderStatusPatchVO;
import com.example.hello.vo.PageResultVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 订单管理 HTTP 接口（文档第 8 章）。
 * <p>
 * 用途：将 URL、动词、请求参数映射到 {@link OrderService}；
 * 保持与项目其它模块一致，统一返回 {@link com.example.hello.common.Result}，
 * 业务异常（如时段冲突、状态非法）由 {@link com.example.hello.common.GlobalExceptionHandler} 转为 code=0。
 */
@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 8.1 分页条件查询 */
    @GetMapping
    public Result<PageResultVO<BizOrder>> pageQuery(@RequestParam(required = false) String orderNo,
                                                    @RequestParam(required = false) String customerName,
                                                    @RequestParam(required = false) String petNickname,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginTime,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
                                                    @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") Integer page,
                                                    @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize最小为1") Integer pageSize) {
        return Result.success(orderService.pageQuery(orderNo, customerName, petNickname, status, beginTime, endTime, page, pageSize));
    }

    /** 8.2 详情（嵌套 VO） */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        OrderDetailVO vo = orderService.findDetailById(id);
        return vo != null ? Result.success(vo) : Result.error("订单不存在");
    }

    /** 8.3 创建（预约） */
    @PostMapping
    public Result<OrderCreateResultVO> create(@RequestBody @Valid OrderCreateDTO req) {
        return Result.success(orderService.create(req));
    }

    /** 8.4 修改（仅待确认） */
    @PutMapping
    public Result<BizOrder> update(@RequestBody @Valid OrderUpdateDTO req) {
        BizOrder updated = orderService.update(req);
        return updated != null ? Result.success(updated) : Result.error("订单不存在");
    }

    /** 8.5 更新状态 */
    @PatchMapping("/{id}/status")
    public Result<OrderStatusPatchVO> updateStatus(@PathVariable @NotNull(message = "id不能为空") Integer id,
                                                   @RequestBody @Valid OrderStatusDTO dto) {
        OrderStatusPatchVO vo = orderService.updateStatus(id, dto);
        return vo != null ? Result.success(vo) : Result.error("订单不存在");
    }

    /** 8.6 评价（仅已完成） */
    @PutMapping("/{id}/rating")
    public Result<OrderRatingResultVO> rate(@PathVariable @NotNull(message = "id不能为空") Integer id,
                                            @RequestBody @Valid OrderRatingDTO dto) {
        OrderRatingResultVO vo = orderService.rate(id, dto);
        return vo != null ? Result.success(vo) : Result.error("订单不存在");
    }

    /** 8.7 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        boolean ok = orderService.delete(id);
        return ok ? Result.success() : Result.error("订单不存在");
    }
}
