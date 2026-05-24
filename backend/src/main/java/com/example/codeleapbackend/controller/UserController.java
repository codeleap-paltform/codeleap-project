package com.example.codeleapbackend.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.entity.User;
import com.example.codeleapbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController // 告诉SpringBoot：这是一个Controller类，返回JSON数据
@RequestMapping("/user") // 这个Controller的所有接口地址都以/user开头
public class UserController {

    @Autowired // 自动注入UserService，不用我们自己new对象
    private UserService userService;

    // 注册接口：前端发POST请求到/user/register，就会调用这个方法
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        user.setCreateTime(LocalDateTime.now()); // 设置用户的创建时间为当前时间
        userService.save(user); // 调用Service的save方法，把用户保存到数据库
        return Result.success(user); // 返回成功的结果，带用户信息
    }

    // 登录接口：前端发POST请求到/user/login，就会调用这个方法
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        // 根据用户名和密码查询数据库里的用户
        User dbUser = userService.lambdaQuery()
                .eq(User::getUsername, user.getUsername()) // 用户名相等
                .eq(User::getPassword, user.getPassword()) // 密码相等
                .one(); // 只查询一条记录

        if (dbUser == null) {
            // 如果查询不到，说明用户名或密码错误，返回失败
            return Result.error("用户名或密码错误");
        }
        // 如果查询到了，返回成功的结果，带用户信息
        return Result.success(dbUser);
    }

    // 查询所有用户接口：前端发GET请求到/user/list，就会调用这个方法
    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userService.list()); // 返回所有用户
    }

    // 根据id查询用户接口：前端发GET请求到/user/1，就会查询id为1的用户
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id)); // 根据id查询用户
    }

    // 更新用户接口：前端发PUT请求到/user，就会调用这个方法
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(userService.updateById(user)); // 更新用户
    }

    // 删除用户接口：前端发DELETE请求到/user/1，就会删除id为1的用户
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id)); // 根据id删除用户
    }
}
