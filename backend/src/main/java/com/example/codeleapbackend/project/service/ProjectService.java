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
    /**
     * 查询当前用户拥有的指定项目。
     *
     * @param ownerId 当前登录用户的编号
     * @param projectId 要查询的项目编号
     * @return 项目详情
     */
    ProjectVO getOwnedProject(Long ownerId, Long projectId);
    /**
     * 修改当前用户拥有的指定项目的名称和描述。
     *
     * @param ownerId 当前登录用户的编号
     * @param projectId 要修改的项目编号
     * @param name 新项目名称
     * @param description 新项目描述，传入 null 表示清空
     * @return 修改后的项目详情
     */
    ProjectVO updateOwnedProject(Long ownerId, Long projectId,
                                 String name, String description);
}