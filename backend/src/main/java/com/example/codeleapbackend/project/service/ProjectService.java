package com.example.codeleapbackend.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.codeleapbackend.project.pojo.dto.CreateProjectParam;
import com.example.codeleapbackend.project.pojo.entity.Project;
import com.example.codeleapbackend.project.pojo.vo.ProjectVO;

import java.util.List;

public interface ProjectService extends IService<Project> {

    /** 创建项目：ownerId 由调用方传入（来自 Session） */
    ProjectVO create(Long ownerId, CreateProjectParam param);

    /** 查询当前用户创建的项目，按创建时间倒序 */
    List<ProjectVO> listByOwner(Long ownerId);
}