package com.example.codeleapbackend.project.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.project.pojo.dto.CreateProjectParam;
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
    public Result list(HttpSession session) {
        return Result.ok(projectService.listByOwner(currentUserId(session)));
    }
}