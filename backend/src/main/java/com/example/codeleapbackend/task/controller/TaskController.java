package com.example.codeleapbackend.task.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.common.ServiceException;
import com.example.codeleapbackend.common.StatusCode;
import com.example.codeleapbackend.task.pojo.entity.Task;
import com.example.codeleapbackend.task.service.TaskService;
import com.example.codeleapbackend.user.controller.UserController;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(UserController.SESSION_USER_ID);
    }

    @PostMapping
    public Result add(@Valid @RequestBody Task input, HttpSession session) {
        Task task = new Task();
        task.setTitle(input.getTitle().trim());
        task.setContent(input.getContent());
        task.setStatus(0);
        task.setUserId(currentUserId(session));
        task.setCreateTime(LocalDateTime.now());
        taskService.save(task);
        return Result.ok(task);
    }

    @GetMapping
    public Result list(HttpSession session) {
        return Result.ok(taskService.lambdaQuery()
                .eq(Task::getUserId, currentUserId(session))
                .orderByDesc(Task::getCreateTime)
                .list());
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @Valid @RequestBody Task input, HttpSession session) {
        Task task = taskService.lambdaQuery()
                .eq(Task::getId, id)
                .eq(Task::getUserId, currentUserId(session))
                .one();
        if (task == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        task.setTitle(input.getTitle().trim());
        task.setContent(input.getContent());
        if (input.getStatus() != null && (input.getStatus() == 0 || input.getStatus() == 1)) {
            task.setStatus(input.getStatus());
        }
        taskService.updateById(task);
        return Result.ok(task);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpSession session) {
        boolean removed = taskService.lambdaUpdate()
                .eq(Task::getId, id)
                .eq(Task::getUserId, currentUserId(session))
                .remove();
        if (!removed) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }
        return Result.ok();
    }
}
