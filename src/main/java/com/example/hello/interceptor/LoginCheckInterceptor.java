package com.example.hello.interceptor;

import com.example.hello.common.Result;
import com.example.hello.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录校验拦截器。
 */
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public LoginCheckInterceptor(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            writeUnauthorized(response, "未登录");
            return false;
        }
        try {
            jwtUtils.parseToken(token);
            return true;
        } catch (Exception ex) {
            writeUnauthorized(response, "登录已过期或无效");
            return false;
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = new Result<>();
        result.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        result.setMsg(msg);
        result.setData(null);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

