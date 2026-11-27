package com.example.codeleapbackend.project.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑项目请求参数。
 * 规则与 CreateProjectParam 保持一致：
 * name 非空白，原始字符串长度 <= 100（trim 在保存时做）
 * description 可为 null，长度 <= 500
 */
@Data
public class UpdateProjectParam {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100")
    private String name;

    @Size(max = 500, message = "项目描述长度不能超过500")
    private String description;
}