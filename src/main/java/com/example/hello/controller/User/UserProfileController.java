package com.example.hello.controller.User;

import com.example.hello.common.Result;
import com.example.hello.dto.UserAccountProfileUpdateDTO;
import com.example.hello.dto.UserCustomerProfileDTO;
import com.example.hello.entity.Customer;
import com.example.hello.service.UserProfileService;
import com.example.hello.util.UserTokenUtils;
import com.example.hello.vo.UserAccountProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端个人中心客户档案接口。
 */
@RestController
@RequestMapping("/app/profile/customer")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserTokenUtils userTokenUtils;

    public UserProfileController(UserProfileService userProfileService, UserTokenUtils userTokenUtils) {
        this.userProfileService = userProfileService;
        this.userTokenUtils = userTokenUtils;
    }

    /**
     * 查询当前登录用户账号资料（用户名、昵称、手机号等）。
     */
    @GetMapping("/account")
    public Result<UserAccountProfileVO> getMyAccount(HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        UserAccountProfileVO vo = userProfileService.getMyAccountProfile(userAccountId);
        return vo != null ? Result.success(vo) : Result.error("当前用户不存在");
    }

    /**
     * 修改当前登录用户账号资料（用户名、昵称）。
     */
    @PutMapping("/account")
    public Result<UserAccountProfileVO> updateMyAccount(@RequestBody @Valid UserAccountProfileUpdateDTO dto, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userProfileService.updateMyAccountProfile(userAccountId, dto));
    }

    /**
     * 查询当前登录用户绑定的客户档案。
     */
    @GetMapping
    public Result<Customer> getMyProfile(HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userProfileService.getMyCustomerProfile(userAccountId));
    }

    /**
     * 创建并绑定客户档案。
     */
    @PostMapping
    public Result<Customer> createMyProfile(@RequestBody @Valid UserCustomerProfileDTO dto, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userProfileService.createMyCustomerProfile(userAccountId, dto));
    }

    /**
     * 修改当前用户已绑定的客户档案。
     */
    @PutMapping
    public Result<Customer> updateMyProfile(@RequestBody @Valid UserCustomerProfileDTO dto, HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        return Result.success(userProfileService.updateMyCustomerProfile(userAccountId, dto));
    }

    /**
     * 解绑当前用户与客户档案关系。
     */
    @DeleteMapping
    public Result<Void> unbindMyProfile(HttpServletRequest request) {
        Integer userAccountId = userTokenUtils.currentUserId(request);
        boolean ok = userProfileService.unbindMyCustomerProfile(userAccountId);
        return ok ? Result.success() : Result.error("当前账号未绑定客户档案");
    }
}
