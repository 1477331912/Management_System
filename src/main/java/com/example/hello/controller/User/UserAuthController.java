package com.example.hello.controller.User;

import com.example.hello.common.Result;
import com.example.hello.dto.UserLoginDTO;
import com.example.hello.dto.UserRegisterDTO;
import com.example.hello.service.UserAccountService;
import com.example.hello.vo.UserLoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端认证接口。
 */
@RestController
@RequestMapping("/app/users")
public class UserAuthController {

    private final UserAccountService userAccountService;

    public UserAuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * 用户注册。
     * <p>
     * 创建 user_account 并同步创建客户档案 customer，返回登录态信息。
     *
     * @param dto 注册请求
     * @return 注册成功后的用户信息与 token
     */
    @PostMapping("/register")
    public Result<UserLoginVO> register(@RequestBody @Valid UserRegisterDTO dto) {
        return Result.success(userAccountService.register(dto));
    }

    /**
     * 用户登录。
     * <p>
     * 支持用户名或手机号登录，校验成功后返回 token。
     *
     * @param dto 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody @Valid UserLoginDTO dto) {
        UserLoginVO vo = userAccountService.login(dto);
        return vo != null ? Result.success(vo) : Result.error("账号或密码错误");
    }
}
