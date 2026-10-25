package com.example.codeleapbackend.project.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;

    /** 项目拥有者，由后端从 Session 注入，不接受前端传入 */
    private Long ownerId;

    private LocalDateTime createTime;

}