package com.example.codeleapbackend.task.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "请输入任务标题")
    @Size(max = 100, message = "任务标题不能超过100个字符")
    private String title;

    @Size(max = 1000, message = "任务内容不能超过1000个字符")
    private String content;

    private Integer status;
    private Long userId;
    private LocalDateTime createTime;
}
