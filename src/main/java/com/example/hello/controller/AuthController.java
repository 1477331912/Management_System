package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.LoginDTO;
import com.example.hello.service.AuthService;
import com.example.hello.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证接口。
 */
@RestController
@RequestMapping
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 员工登录。
     * <p>
     * POST /login
     *
     * @param loginDTO 登录参数
     * @return 成功返回员工信息及token，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return loginVO != null ? Result.success(loginVO) : Result.error("用户名或密码错误");
    }
}

