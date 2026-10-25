package com.example.codeleapbackend.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.codeleapbackend.project.mapper.ProjectMapper;
import com.example.codeleapbackend.project.pojo.dto.CreateProjectParam;
import com.example.codeleapbackend.project.pojo.entity.Project;
import com.example.codeleapbackend.project.pojo.vo.ProjectVO;
import com.example.codeleapbackend.project.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project>
        implements ProjectService {

    @Override
    @Transactional
    public ProjectVO create(Long ownerId, CreateProjectParam param) {
        Project project = new Project();
        project.setName(param.getName().trim());
        project.setDescription(param.getDescription());
        project.setOwnerId(ownerId);              // ownerId 只来自 Session
        project.setCreateTime(LocalDateTime.now());
        save(project);
        return ProjectVO.from(project);
    }

    @Override
    public List<ProjectVO> listByOwner(Long ownerId) {
        return lambdaQuery()
                .eq(Project::getOwnerId, ownerId)   // 查询条件必须带当前用户 id
                .orderByDesc(Project::getCreateTime) // 按创建时间倒序
                .list()
                .stream()
                .map(ProjectVO::from)
                .toList();
    }
}