package com.example.codeleapbackend.common;

import com.example.codeleapbackend.user.controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod; // 新增这个 import
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 防止options被拦截
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        Object userId = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute(UserController.SESSION_USER_ID);
        if (userId != null) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new Result(StatusCode.NOT_LOGIN)));
        return false;
    }
}