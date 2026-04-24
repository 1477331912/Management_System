package com.example.hello.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 用户端 token 解析工具。
 */
@Component
public class UserTokenUtils {

    private final JwtUtils jwtUtils;

    public UserTokenUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public Integer currentUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = jwtUtils.parseToken(token);
        Object sub = claims.get("sub");
        if (sub == null) {
            throw new IllegalArgumentException("登录信息无效");
        }
        return Integer.valueOf(String.valueOf(sub));
    }
}
