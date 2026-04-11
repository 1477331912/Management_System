package com.example.hello.service.impl;

import com.example.hello.dto.LoginDTO;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.service.AuthService;
import com.example.hello.util.JwtUtils;
import com.example.hello.vo.LoginVO;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证业务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final EmpMapper empMapper;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(EmpMapper empMapper, JwtUtils jwtUtils) {
        this.empMapper = empMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String md5Password = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        var emp = empMapper.findByUsernameAndPassword(loginDTO.getUsername(), md5Password);
        if (emp == null) {
            return null;
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", String.valueOf(emp.getId()));
        claims.put("username", emp.getUsername());
        claims.put("name", emp.getName());

        LoginVO loginVO = new LoginVO();
        loginVO.setId(emp.getId());
        loginVO.setUsername(emp.getUsername());
        loginVO.setName(emp.getName());
        loginVO.setToken(jwtUtils.generateToken(claims));
        return loginVO;
    }
}

