package com.example.codeleapbackend.task.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.task.pojo.entity.Task;
import com.example.codeleapbackend.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/task") // 接口统一前缀
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 1. 新增任务
    @PostMapping
    public Result add(@RequestBody Task task) {
        task.setCreateTime(LocalDateTime.now()); // 自动填充创建时间
        task.setStatus(0); // 新建任务默认未完成
        taskService.save(task);
        return Result.ok(task);
    }

    // 2. 查询所有任务
    @GetMapping("/list")
    public Result list() {
        return Result.ok(taskService.list());
    }

    // 3. 根据用户ID查询该用户的所有任务
    @GetMapping("/user/{userId}")
    public Result getByUserId(@PathVariable Long userId) {
        List<Task> tasks = taskService.lambdaQuery()
                .eq(Task::getUserId, userId)
                .orderByDesc(Task::getCreateTime)
                .list();
        return Result.ok(tasks);
    }

    // 4. 更新任务（修改内容、状态）
    @PutMapping
    public Result update(@RequestBody Task task) {
        return Result.ok(taskService.updateById(task));
    }

    // 5. 根据ID删除任务
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return Result.ok(taskService.removeById(id));
    }
}