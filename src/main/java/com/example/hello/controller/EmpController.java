package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.EmpCreateDTO;
import com.example.hello.dto.EmpUpdateDTO;
import com.example.hello.entity.Emp;
import com.example.hello.service.EmpService;
import com.example.hello.vo.PageResultVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工管理接口。
 */
@Validated
@RestController
@RequestMapping("/emps")
public class EmpController {

    private final EmpService empService;

    public EmpController(EmpService empService) {
        this.empService = empService;
    }

    /**
     * 分页条件查询员工。
     */
    @GetMapping
    public Result<PageResultVO<Emp>> pageQuery(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) Integer gender,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                             @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") Integer page,
                                             @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize最小为1") Integer pageSize) {
        return Result.success(empService.pageQuery(name, gender, beginDate, endDate, page, pageSize));
    }

    /**
     * 根据ID查询员工详情（含工作经历）。
     */
    @GetMapping("/{id}")
    public Result<Emp> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        return empService.findDetailById(id)
                .map(Result::success)
                .orElseGet(() -> Result.error("员工不存在"));
    }

    /**
     * 新增员工（含工作经历）。
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid EmpCreateDTO req) {
        empService.create(req);
        return Result.success();
    }

    /**
     * 修改员工（含工作经历，全量覆盖）。
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid EmpUpdateDTO req) {
        boolean ok = empService.update(req);
        return ok ? Result.success() : Result.error("员工不存在");
    }

    /**
     * 查询全部员工。
     */
    @GetMapping("/list")
    public Result<PageResultVO<Emp>> listAll() {
        List<Emp> rows = empService.listAll();
        PageResultVO<Emp> result = new PageResultVO<>();
        result.setRows(rows);
        result.setTotal((long) rows.size());
        return Result.success(result);
    }

    /**
     * 批量删除员工（级联删除工作经历）。
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestParam @NotEmpty(message = "ids不能为空") List<Integer> ids) {
        empService.batchDelete(ids);
        return Result.success();
    }
}

