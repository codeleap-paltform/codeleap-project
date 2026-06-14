package com.example.codeleapbackend.task.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task") // 关联数据库中的task表
public class Task {
    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;          // 任务唯一ID
    private String title;     // 任务标题
    private String content;   // 任务详情内容
    private Integer status;   // 任务状态：0=未完成，1=已完成
    private Long userId;      // 所属用户ID，关联user表
    private LocalDateTime createTime; // 任务创建时间
}