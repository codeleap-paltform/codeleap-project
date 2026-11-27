package com.example.codeleapbackend.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.codeleapbackend.common.ServiceException;
import com.example.codeleapbackend.common.StatusCode;
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
        // 开始查询项目。ownerId 是调用这个方法时传进来的当前用户编号。
        return lambdaQuery()
                // 只保留创建者编号等于 ownerId 的项目。
                .eq(Project::getOwnerId, ownerId)
                // 按创建时间倒序，让最新的项目排在前面。
                .orderByDesc(Project::getCreateTime)
                .list()
                // 把查到的每个 Project 转成页面需要的 ProjectVO。
                .stream()
                .map(ProjectVO::from)
                .toList();

    }
    @Override
    public ProjectVO getOwnedProject(Long ownerId, Long projectId) {
        Project project = lambdaQuery()
                // 根据项目编号定位记录，再用创建者编号限制查询范围。
                .eq(Project::getId, projectId)
                .eq(Project::getOwnerId, ownerId)
                .one();

        // 项目不存在或属于他人时，统一返回“请求数据不存在”。
        if (project == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 转成详情页需要的四个字段，避免把内部的 ownerId 返回给前端。
        return ProjectVO.from(project);
    }
    @Override
    @Transactional
    public ProjectVO updateOwnedProject(Long ownerId, Long projectId,
                                        String name, String description) {
        // 复用详情查询检查归属。找不到自己的项目时，这里就会抛出异常。
        ProjectVO current = getOwnedProject(ownerId, projectId);

        // 去掉名称首尾的空格。描述传入 null 时，明确用空字符串覆盖旧描述。
        String newName = name.trim();
        String newDescription = description == null ? "" : description;

        // 保存相同内容也算成功，此时直接返回当前详情。
        if (newName.equals(current.getName())
                && newDescription.equals(current.getDescription())) {
            return current;
        }

        // UPDATE 本身也必须带两个条件。只设置名称和描述，保留创建者和创建时间。
        boolean updated = lambdaUpdate()
                .eq(Project::getId, projectId)
                .eq(Project::getOwnerId, ownerId)
                .set(Project::getName, newName)
                .set(Project::getDescription, newDescription)
                .update();

        // 如果目标记录已经无法匹配，沿用详情查询的业务错误。
        if (!updated) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 从数据库重新读取，返回保存后的详情。
        return getOwnedProject(ownerId, projectId);
    }
}