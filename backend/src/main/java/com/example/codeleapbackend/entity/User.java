package com.example.codeleapbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data // 自动生成getter、setter、toString方法，不用我们手写
@TableName("user") // 告诉MyBatis-Plus：这个类对应数据库里的user表
public class User {
    @TableId(type = IdType.AUTO) // 告诉MyBatis-Plus：这个字段是主键，并且自动递增
    private Long id; // 用户id（对应数据库里的id字段）

    private String username; // 用户名（对应username字段）
    private String password; // 密码（对应password字段）
    private String email; // 邮箱（对应email字段）
    private LocalDateTime createTime; // 创建时间（对应create_time字段）
}
