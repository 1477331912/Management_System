package com.example.hello.controller.User;

import com.example.hello.common.Result;
import com.example.hello.dto.UserAppointmentCancelDTO;
import com.example.hello.dto.UserAppointmentCreateDTO;
import com.example.hello.dto.UserAppointmentRescheduleDTO;
import com.example.hello.dto.UserPetCreateDTO;
import com.example.hello.entity.BizOrder;
import com.example.hello.entity.Pet;
import com.example.hello.service.UserAppointmentService;
import com.example.hello.util.UserTokenUtils;
import com.example.hello.vo.AvailableSlotVO;
import com.example.hello.vo.EmpOptionVO;
import com.example.hello.vo.OrderCreateResultVO;
import com.example.hello.vo.OrderDetailVO;
import com.example.hello.vo.PageResultVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户端预约接口。
 */
@Validated
@RestController
@RequestMapping("/app")
public class UserAppointmentController {

    private final UserAppointmentService userAppointmentService;
    private final UserTokenUtils userTokenUtils;

    public UserAppointmentController(UserAppointmentService userAppointmentService, UserTokenUtils userTokenUtils) {
        this.userAppointmentService = userAppointmentService;
        this.userTokenUtils = userTokenUtils;
    }

    /**
     * 查询当前登录用户名下宠物列表。
     *
     * @param request HTTP 请求（用于从 token 解析用户ID）
     * @return 宠物列表
     */
    @GetMapping("/pets")
    public Result<List<Pet>> myPets(HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userAppointmentService.myPets(userAccountId));
    }

    /**
     * 用户端创建宠物，并自动绑定到当前用户的客户档案。
     *
     * @param dto 宠物创建参数（不含customerId）
     * @param request HTTP 请求（用于从 token 解析用户ID）
     * @return 创建后的宠物
     */
    @PostMapping("/pets")
    public Result<Pet> createPet(@RequestBody @Valid UserPetCreateDTO dto, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userAppointmentService.createMyPet(userAccountId, dto));
    }

    /**
     * 删除当前用户名下宠物。
     *
     * @param id 宠物ID
     * @param request HTTP 请求（用于从 token 解析用户ID）
     * @return 删除结果
     */
    @DeleteMapping("/pets/{id}")
    public Result<Void> deletePet(@PathVariable @NotNull Integer id, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        boolean ok = userAppointmentService.deleteMyPet(userAccountId, id);
        return ok ? Result.success() : Result.error("宠物不存在");
    }

    /**
     * 查询服务师下拉选项（按服务项目所属部门）。
     * <p>
     * 传 date 时仅返回当天有可预约排班的服务师；不传时返回该部门全部服务师。
     *
     * @param serviceItemId 服务项目ID
     * @param date 可选排班日期
     * @return 服务师下拉列表
     */
    @GetMapping("/appointments/emp-options")
    public Result<List<EmpOptionVO>> empOptions(@RequestParam Integer serviceItemId,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(userAppointmentService.empOptions(serviceItemId, date));
    }

    /**
     * 查询可预约时段。
     * <p>
     * 基于排班与订单冲突计算指定日期、服务项目的可预约时间。
     *
     * @param date 查询日期
     * @param serviceItemId 服务项目ID
     * @param empId 可选服务师ID，不传则返回所有服务师可约时段
     * @return 可预约时段列表
     */
    @GetMapping("/appointments/available-slots")
    public Result<List<AvailableSlotVO>> availableSlots(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                        @RequestParam Integer serviceItemId,
                                                        @RequestParam(required = false) Integer empId) {
        return Result.success(userAppointmentService.availableSlots(date, serviceItemId, empId));
    }

    /**
     * 创建用户预约。
     *
     * @param dto 创建预约参数
     * @param request HTTP 请求（用于解析当前用户ID）
     * @return 新建预约结果（含订单号）
     */
    @PostMapping("/appointments")
    public Result<OrderCreateResultVO> create(@RequestBody @Valid UserAppointmentCreateDTO dto, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userAppointmentService.create(userAccountId, dto));
    }

    /**
     * 分页查询当前登录用户自己的预约订单。
     *
     * @param orderNo 订单号（可选）
     * @param status 订单状态（可选）
     * @param beginTime 服务时间起始日期（可选）
     * @param endTime 服务时间结束日期（可选）
     * @param page 页码
     * @param pageSize 每页大小
     * @param request HTTP 请求（用于解析当前用户ID）
     * @return 预约订单分页结果
     */
    @GetMapping("/appointments")
    public Result<PageResultVO<BizOrder>> myOrders(@RequestParam(required = false) String orderNo,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginTime,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
                                                   @RequestParam(defaultValue = "1") @Min(1) Integer page,
                                                   @RequestParam(defaultValue = "10") @Min(1) Integer pageSize,
                                                   HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userAppointmentService.myOrders(userAccountId, orderNo, status, beginTime, endTime, page, pageSize));
    }

    /**
     * 查询当前登录用户的单条预约详情。
     *
     * @param id 订单ID
     * @param request HTTP 请求（用于解析当前用户ID）
     * @return 订单详情
     */
    @GetMapping("/appointments/{id}")
    public Result<OrderDetailVO> myOrderDetail(@PathVariable @NotNull Integer id, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        OrderDetailVO vo = userAppointmentService.myOrderDetail(userAccountId, id);
        return vo != null ? Result.success(vo) : Result.error("订单不存在");
    }

    /**
     * 取消预约订单。
     *
     * @param id 订单ID
     * @param dto 取消请求参数（可选取消原因）
     * @param request HTTP 请求（用于解析当前用户ID）
     * @return 取消后的订单信息
     */
    @PatchMapping("/appointments/{id}/cancel")
    public Result<BizOrder> cancel(@PathVariable @NotNull Integer id,
                                   @RequestBody(required = false) @Valid UserAppointmentCancelDTO dto,
                                   HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        BizOrder order = userAppointmentService.cancel(userAccountId, id, dto);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }

    /**
     * 改期或更换服务师。
     *
     * @param id 订单ID
     * @param dto 改期请求参数
     * @param request HTTP 请求（用于解析当前用户ID）
     * @return 更新后的订单信息
     */
    @PatchMapping("/appointments/{id}/reschedule")
    public Result<BizOrder> reschedule(@PathVariable @NotNull Integer id,
                                       @RequestBody @Valid UserAppointmentRescheduleDTO dto,
                                       HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        BizOrder order = userAppointmentService.reschedule(userAccountId, id, dto);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }
}
