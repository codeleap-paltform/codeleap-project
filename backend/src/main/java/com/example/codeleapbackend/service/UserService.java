package com.example.codeleapbackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.codeleapbackend.entity.User;
import com.example.codeleapbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service // 告诉SpringBoot：这是一个Service类，交给Spring管理
public class UserService extends ServiceImpl<UserMapper, User> {
    // 同样什么都不用写！ServiceImpl已经帮我们实现了所有常用的业务方法
}
