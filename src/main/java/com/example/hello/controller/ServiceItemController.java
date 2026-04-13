package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.ServiceItemCreateDTO;
import com.example.hello.dto.ServiceItemUpdateDTO;
import com.example.hello.entity.ServiceItem;
import com.example.hello.service.ServiceItemService;
import com.example.hello.vo.PageResultVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

/**
 * 服务项目管理接口（路径与文档 7.x 一致）。
 */
@Validated
@RestController
@RequestMapping("/service-items")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    /** 7.1 分页条件查询：名称模糊、部门筛选；按创建时间倒序 */
    @GetMapping
    public Result<PageResultVO<ServiceItem>> pageQuery(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Integer deptId,
                                                       @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") Integer page,
                                                       @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize最小为1") Integer pageSize) {
        return Result.success(serviceItemService.pageQuery(name, deptId, page, pageSize));
    }

    /** 7.2 根据 ID 查询 */
    @GetMapping("/{id}")
    public Result<ServiceItem> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        ServiceItem item = serviceItemService.findById(id);
        return item != null ? Result.success(item) : Result.error("服务项目不存在");
    }

    /** 7.3 新增 */
    @PostMapping
    public Result<ServiceItem> create(@RequestBody @Valid ServiceItemCreateDTO req) {
        return Result.success(serviceItemService.create(req));
    }

    /** 7.4 修改 */
    @PutMapping
    public Result<ServiceItem> update(@RequestBody @Valid ServiceItemUpdateDTO req) {
        ServiceItem updated = serviceItemService.update(req);
        return updated != null ? Result.success(updated) : Result.error("服务项目不存在");
    }

    /** 7.5 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        boolean ok = serviceItemService.delete(id);
        return ok ? Result.success() : Result.error("服务项目不存在");
    }
}
