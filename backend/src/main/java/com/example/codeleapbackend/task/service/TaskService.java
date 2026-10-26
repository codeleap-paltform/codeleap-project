package com.example.codeleapbackend.task.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.codeleapbackend.task.mapper.TaskMapper;
import com.example.codeleapbackend.task.pojo.entity.Task;
import org.springframework.stereotype.Service;

@Service // 交给Spring容器管理
public class TaskService extends ServiceImpl<TaskMapper, Task> {
    // 继承ServiceImpl后自动拥有通用业务方法
}