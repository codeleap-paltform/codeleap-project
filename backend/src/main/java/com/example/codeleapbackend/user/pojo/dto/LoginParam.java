package com.example.codeleapbackend.user.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginParam {
    @NotBlank(message = "请输入用户名")
    @Size(max = 50, message = "用户名不能超过50个字符")
    private String username;

    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 72, message = "密码长度必须为6到72位")
    private String password;
}
