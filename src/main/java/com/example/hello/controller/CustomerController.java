package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.CustomerCreateDTO;
import com.example.hello.dto.CustomerUpdateDTO;
import com.example.hello.entity.Customer;
import com.example.hello.service.CustomerService;
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
 * 客户管理接口（仅 {@code customer} 表，路径与文档 5.x 一致）。
 */
@Validated
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 5.1 客户分页条件查询：name 模糊、phone 精确、memberLevel 筛选；默认 page=1、pageSize=10。
     */
    @GetMapping
    public Result<PageResultVO<Customer>> pageQuery(@RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String phone,
                                                      @RequestParam(required = false) Integer memberLevel,
                                                      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") Integer page,
                                                      @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize最小为1") Integer pageSize) {
        return Result.success(customerService.pageQuery(name, phone, memberLevel, page, pageSize));
    }

    /**
     * 5.2 根据 ID 查询客户详情（仅客户表字段，不含其他业务扩展数据）。
     */
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        Customer customer = customerService.findById(id);
        return customer != null ? Result.success(customer) : Result.error("客户不存在");
    }

    /** 5.3 新增客户 */
    @PostMapping
    public Result<Customer> create(@RequestBody @Valid CustomerCreateDTO req) {
        return Result.success(customerService.create(req));
    }

    /** 5.4 修改客户 */
    @PutMapping
    public Result<Customer> update(@RequestBody @Valid CustomerUpdateDTO req) {
        Customer updated = customerService.update(req);
        return updated != null ? Result.success(updated) : Result.error("客户不存在");
    }

    /** 5.5 删除客户 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        boolean ok = customerService.delete(id);
        return ok ? Result.success() : Result.error("客户不存在");
    }
}
