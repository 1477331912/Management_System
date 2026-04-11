package com.example.hello.service;

import com.example.hello.dto.LoginDTO;
import com.example.hello.vo.LoginVO;

/**
 * 认证业务接口。
 */
public interface AuthService {
    /**
     * 登录认证。
     *
     * @param loginDTO 登录参数
     * @return 成功返回登录信息，失败返回空
     */
    LoginVO login(LoginDTO loginDTO);
}

