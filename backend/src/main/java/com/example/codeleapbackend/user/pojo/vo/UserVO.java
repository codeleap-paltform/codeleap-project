package com.example.codeleapbackend.user.pojo.vo;

import com.example.codeleapbackend.user.pojo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String username;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername());
    }
}
