package com.example.codeleapbackend.user.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.user.pojo.dto.ChangePasswordParam;
import com.example.codeleapbackend.user.pojo.dto.LoginParam;
import com.example.codeleapbackend.user.pojo.entity.User;
import com.example.codeleapbackend.user.pojo.vo.UserVO;
import com.example.codeleapbackend.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    public static final String SESSION_USER_ID = "userId";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result register(@Valid @RequestBody LoginParam param) {
        userService.register(param);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginParam param, HttpSession session) {
        User user = userService.login(param);
        session.setAttribute(SESSION_USER_ID, user.getId());
        return Result.ok(UserVO.from(user));
    }

    @GetMapping("/me")
    public Result me(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        return Result.ok(UserVO.from(userService.getById(userId)));
    }

    @PostMapping("/logout")
    public Result logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }

    @PutMapping("/password")
    public Result changePassword(@Valid @RequestBody ChangePasswordParam param, HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        userService.changePassword(userId, param.getOldPassword(), param.getNewPassword());
        session.invalidate();
        return Result.ok();
    }
}
