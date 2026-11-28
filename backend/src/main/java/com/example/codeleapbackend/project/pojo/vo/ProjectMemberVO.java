package com.example.codeleapbackend.project.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 返回给页面的一条成员信息。 */
@Data // 生成 getUserId()、getUsername()、getRole() 等方法。
@AllArgsConstructor // 生成构造方法，创建对象时可以一次传入三个字段。
public class ProjectMemberVO {

    /** 成员的用户编号。 */
    private Long userId;

    /** 成员的用户名，从用户表查出。 */
    private String username;

    /** 成员角色，创建者为 OWNER，普通成员为 MEMBER。 */
    private String role;
}