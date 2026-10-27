package com.example.codeleapbackend.project.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectParam {
    @NotBlank(message = "请输入项目名称")
    @Size(max = 100, message = "项目名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "项目描述不能超过500个字符")
    private String description;
}