package com.example.codeleapbackend.user.service;

import com.example.codeleapbackend.user.pojo.dto.LoginParam;
import com.example.codeleapbackend.user.pojo.entity.User;

public interface UserService {
    void register(LoginParam loginParam);
    User login(LoginParam loginParam);
    User getById(Long id);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
