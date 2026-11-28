package com.example.codeleapbackend.project.pojo.vo;

import com.example.codeleapbackend.project.pojo.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectVO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private String myRole;
//Project对象转为ProjectVO对象避免泄露敏感信息
    public static ProjectVO from(Project project) {
        return new ProjectVO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreateTime(),
                "OWNER"
        );
    }
    // 新增重载方法（给共享查询用）
    public static ProjectVO from(Project project,String myRole) {
        return new ProjectVO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreateTime(),
                myRole
        );
    }
}