package com.example.codeleapbackend.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.codeleapbackend.common.ServiceException;
import com.example.codeleapbackend.common.StatusCode;
import com.example.codeleapbackend.user.mapper.UserMapper;
import com.example.codeleapbackend.user.pojo.dto.LoginParam;
import com.example.codeleapbackend.user.pojo.entity.User;
import com.example.codeleapbackend.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private User findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    @Transactional
    public void register(LoginParam param) {
        String username = param.getUsername().trim();
        if (findByUsername(username) != null) {
            throw new ServiceException(StatusCode.USERNAME_ALREADY_EXISTS);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(param.getPassword()));
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public User login(LoginParam param) {
        User user = findByUsername(param.getUsername().trim());
        if (user == null) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        String stored = user.getPassword();
        boolean valid = stored != null && (stored.startsWith("$2")
                ? passwordEncoder.matches(param.getPassword(), stored)
                : param.getPassword().equals(stored));
        if (!valid) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        if (!stored.startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(param.getPassword()));
            userMapper.updateById(user);
        }
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        return user;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        boolean valid = user.getPassword().startsWith("$2")
                ? passwordEncoder.matches(oldPassword, user.getPassword())
                : oldPassword.equals(user.getPassword());
        if (!valid) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        if (oldPassword.equals(newPassword)) {
            throw new ServiceException(StatusCode.PASSWORD_SAME);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }
}
