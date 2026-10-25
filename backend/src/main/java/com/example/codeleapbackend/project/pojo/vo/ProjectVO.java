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

    public static ProjectVO from(Project project) {
        return new ProjectVO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreateTime()
        );
    }
}