package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.PetCreateDTO;
import com.example.hello.dto.PetUpdateDTO;
import com.example.hello.entity.Pet;
import com.example.hello.service.PetService;
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
 * 宠物管理接口（路径与文档 6.x 一致）。
 */
@Validated
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    /** 6.1 宠物分页条件查询 */
    @GetMapping
    public Result<PageResultVO<Pet>> pageQuery(@RequestParam(required = false) String nickname,
                                               @RequestParam(required = false) String breed,
                                               @RequestParam(required = false) Integer vaccineStatus,
                                               @RequestParam(required = false) Integer customerId,
                                               @RequestParam(defaultValue = "1") @Min(value = 1, message = "page最小为1") Integer page,
                                               @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize最小为1") Integer pageSize) {
        return Result.success(petService.pageQuery(nickname, breed, vaccineStatus, customerId, page, pageSize));
    }

    /** 6.2 根据 ID 查询宠物详情（含所属客户基本信息） */
    @GetMapping("/{id}")
    public Result<Pet> getById(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        Pet pet = petService.findDetailById(id);
        return pet != null ? Result.success(pet) : Result.error("宠物不存在");
    }

    /** 6.3 新增宠物 */
    @PostMapping
    public Result<Pet> create(@RequestBody @Valid PetCreateDTO req) {
        return Result.success(petService.create(req));
    }

    /** 6.4 修改宠物 */
    @PutMapping
    public Result<Pet> update(@RequestBody @Valid PetUpdateDTO req) {
        Pet updated = petService.update(req);
        return updated != null ? Result.success(updated) : Result.error("宠物不存在");
    }

    /** 6.5 删除宠物 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "id不能为空") Integer id) {
        boolean ok = petService.delete(id);
        return ok ? Result.success() : Result.error("宠物不存在");
    }
}
