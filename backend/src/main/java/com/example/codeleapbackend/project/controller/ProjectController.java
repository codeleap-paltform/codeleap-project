package com.example.codeleapbackend.project.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.common.StatusCode;
import com.example.codeleapbackend.project.pojo.dto.CreateProjectParam;
import com.example.codeleapbackend.project.pojo.dto.UpdateProjectParam;
import com.example.codeleapbackend.project.service.ProjectService;
import com.example.codeleapbackend.user.controller.UserController;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(UserController.SESSION_USER_ID);
    }

    @PostMapping
    public Result add(@Valid @RequestBody CreateProjectParam param, HttpSession session) {
        return Result.ok(projectService.create(currentUserId(session), param));
    }
    @GetMapping
    //修改查询，让只查自己的变为查可见的
    public Result list(HttpSession session) {
        Long userId = currentUserId(session);
        return Result.ok(projectService.listVisibleProjects(userId));
    }
    @GetMapping("/{id}")
    //同样修改查询，让只查自己变为查可见
    public Result getDetail(@PathVariable Long id, HttpSession session) {
        if (id == null || id <= 0) {
            return new Result(StatusCode.VALIDATE_ERROR);
        }
        Long ownerId = currentUserId(session);
        return Result.ok(projectService.getVisibleProject(id, ownerId));
    }

    // 编辑项目
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id,
                         @Valid @RequestBody UpdateProjectParam param,
                         HttpSession session) {
        if (id == null || id <= 0) {
            return new Result(StatusCode.VALIDATE_ERROR);
        }
        Long ownerId = currentUserId(session);
        return Result.ok(projectService.updateOwnedProject(
                ownerId, id, param.getName(), param.getDescription()));
    }

}