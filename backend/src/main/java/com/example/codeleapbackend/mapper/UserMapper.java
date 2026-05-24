package com.example.codeleapbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.codeleapbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;


public interface UserMapper extends BaseMapper<User> {
    // 什么都不用写！BaseMapper已经帮我们写好了所有常用的增删改查方法
}
