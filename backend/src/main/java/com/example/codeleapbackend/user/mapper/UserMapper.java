package com.example.codeleapbackend.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.codeleapbackend.user.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
