package com.example.codeleapbackend.project.controller;

import com.example.codeleapbackend.common.Result;
import com.example.codeleapbackend.common.StatusCode;
import com.example.codeleapbackend.project.pojo.dto.AddProjectMemberParam;
import com.example.codeleapbackend.project.service.ProjectMemberService;
import com.example.codeleapbackend.user.controller.UserController;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute(UserController.SESSION_USER_ID);
    }

    @GetMapping
    public Result list(@PathVariable Long projectId, HttpSession session) {
        if (projectId == null || projectId <= 0) {
            return new Result(StatusCode.VALIDATE_ERROR);
        }
        Long userId = currentUserId(session);
        return Result.ok(projectMemberService.listMembers(projectId, userId));
    }

    @PostMapping
    public Result add(@PathVariable Long projectId,
                      @Valid @RequestBody AddProjectMemberParam param,
                      HttpSession session) {
        if (projectId == null || projectId <= 0) {
            return new Result(StatusCode.VALIDATE_ERROR);
        }
        Long userId = currentUserId(session);
        return Result.ok(projectMemberService.addMember(projectId, userId, param));
    }
}