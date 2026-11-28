package com.example.codeleapbackend.project.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 对应 project_member 表中的一条普通成员关系。 */
@Data // 让 Lombok 生成读取和设置字段的方法，例如 getUserId()。
@TableName("project_member") // 告诉 MyBatis-Plus 这个类对应哪张表。
public class ProjectMember {

    /** 成员关系自己的编号，插入时由 MySQL 自增生成。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 加入的项目编号，对应 project_member.project_id。 */
    private Long projectId;

    /** 加入者的用户编号，对应 project_member.user_id。 */
    private Long userId;

    /** 加入时间，由添加成员的 Service 填入。 */
    private LocalDateTime joinTime;
}