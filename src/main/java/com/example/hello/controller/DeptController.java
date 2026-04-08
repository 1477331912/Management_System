package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.DeptCreateDTO;
import com.example.hello.dto.DeptUpdateDTO;
import com.example.hello.entity.Dept;
import com.example.hello.service.DeptService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门管理接口。
 * <p>
 * 接口约定：返回 {@link Result}，code=1成功，code=0失败。
 */
@Validated
@RestController
@RequestMapping("/depts")
public class DeptController {

    private final DeptService deptService;

    /**
     * 构造注入。
     *
     * @param deptService 部门业务
     */
    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * 查询所有部门（不分页，按最后操作时间倒序）。
     * <p>
     * GET /depts
     *
     * @return 部门列表
     */
    @GetMapping
    public Result<List<Dept>> listAll() {
        return Result.success(deptService.listAll());
    }

    /**
     * 根据ID查询部门（用于编辑回显）。
     * <p>
     * GET /depts/{id}
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    public Result<Dept> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        return deptService.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.error("部门不存在"));
    }

    /**
     * 新增部门。
     * <p>
     * POST /depts
     *
     * @param req 请求体
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid DeptCreateDTO req) {
        deptService.create(req.getName());
        return Result.success();
    }

    /**
     * 修改部门。
     * <p>
     * PUT /depts
     *
     * @param req 请求体
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid DeptUpdateDTO req) {
        boolean ok = deptService.update(req.getId(), req.getName());
        return ok ? Result.success() : Result.error("部门不存在");
    }

    /**
     * 删除部门。
     * <p>
     * DELETE /depts/{id}
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        boolean ok = deptService.delete(id);
        return ok ? Result.success() : Result.error("部门不存在");
    }
}

