package com.example.hello.controller.User;

import com.example.hello.common.Result;
import com.example.hello.entity.ServiceItem;
import com.example.hello.service.ServiceItemService;
import com.example.hello.vo.PageResultVO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端服务项目查询接口。
 */
@Validated
@RestController
@RequestMapping("/app/service-items")
public class UserServiceItemController {

    private final ServiceItemService serviceItemService;

    public UserServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    /**
     * 用户端分页条件查询服务项目。
     */
    @GetMapping
    public Result<PageResultVO<ServiceItem>> pageQuery(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer deptId,
                                                       @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") Integer page,
                                                       @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize最小为1") Integer pageSize) {
        return Result.success(serviceItemService.pageQuery(name, deptId, page, pageSize));
    }

    /**
     * 用户端根据ID查询服务项目详情。
     */
    @GetMapping("/{id}")
    public Result<ServiceItem> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        ServiceItem item = serviceItemService.findById(id);
        return item != null ? Result.success(item) : Result.error("服务项目不存在");
    }

    /**
     * 用户端查询全部服务项目（用于下拉框）。
     */
    @GetMapping("/list")
    public Result<List<ServiceItem>> listAll() {
        return Result.success(serviceItemService.listAll());
    }
}
