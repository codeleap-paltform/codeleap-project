package com.example.codeleapbackend.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.codeleapbackend.task.pojo.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    // 继承BaseMapper后自动拥有增删改查能力，无需额外代码
}