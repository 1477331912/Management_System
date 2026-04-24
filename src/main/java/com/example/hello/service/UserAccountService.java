package com.example.hello.service;

import com.example.hello.dto.UserLoginDTO;
import com.example.hello.dto.UserRegisterDTO;
import com.example.hello.vo.UserLoginVO;

/**
 * 用户端账号业务。
 */
public interface UserAccountService {
    UserLoginVO register(UserRegisterDTO dto);
    UserLoginVO login(UserLoginDTO dto);
}
