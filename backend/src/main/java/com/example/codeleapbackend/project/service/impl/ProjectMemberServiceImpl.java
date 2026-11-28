package com.example.codeleapbackend.project.service.impl;
// Spring 管理写入事务，Java 提供加入时间。
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
// 使用项目已有的业务异常和错误码处理无权访问。
import com.example.codeleapbackend.common.ServiceException;
import com.example.codeleapbackend.common.StatusCode;

// 通过项目和用户 Mapper 读取创建者编号和用户名。
import com.example.codeleapbackend.project.mapper.ProjectMapper;
import com.example.codeleapbackend.project.pojo.entity.Project;
import com.example.codeleapbackend.user.mapper.UserMapper;
import com.example.codeleapbackend.user.pojo.entity.User;

// ArrayList 用来逐项加入结果，Map 用来按用户编号找用户名。
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.codeleapbackend.project.mapper.ProjectMemberMapper;
import com.example.codeleapbackend.project.pojo.entity.ProjectMember;
import com.example.codeleapbackend.project.pojo.vo.ProjectMemberVO;
import com.example.codeleapbackend.project.service.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

/** 实现项目成员相关的业务操作。 */
@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    /** 读取普通成员关系。 */
    private final ProjectMemberMapper memberMapper;

    /** 读取项目及其创建者编号。 */
    private final ProjectMapper projectMapper;

    /** 按用户编号读取用户名。 */
    private final UserMapper userMapper;

    /** Spring 创建 Service 时，把三个 Mapper 交给这个构造方法。 */
    public ProjectMemberServiceImpl(ProjectMemberMapper memberMapper,
                                    ProjectMapper projectMapper,
                                    UserMapper userMapper) {
        this.memberMapper = memberMapper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public boolean isMember(Long userId, Long projectId) {
        // 同时匹配用户编号和项目编号，相当于 WHERE user_id = ? AND project_id = ?。
        LambdaQueryWrapper<ProjectMember> query = new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getUserId, userId)
                .eq(ProjectMember::getProjectId, projectId);

        // 只需要判断是否存在，所以数一数符合条件的行。
        return memberMapper.selectCount(query) > 0;
    }

    @Override
    public List<Long> listJoinedProjectIds(Long userId) {
        // 只查询该用户的关系记录，并且只读取 project_id 这一列。
        LambdaQueryWrapper<ProjectMember> query = new LambdaQueryWrapper<ProjectMember>()
                .select(ProjectMember::getProjectId)
                .eq(ProjectMember::getUserId, userId);

        // Mapper 返回的是对象列表。逐个取出 projectId，得到编号列表。
        return memberMapper.selectList(query)
                .stream()
                .map(ProjectMember::getProjectId)
                .toList();
    }

    @Override
    public List<ProjectMemberVO> listMembers(Long actorId, Long projectId) {
        // 先查项目。项目不存在时，直接结束请求。
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 创建者能直接查看。其他操作者必须在普通成员关系表里有记录。
        Long ownerId = project.getOwnerId();
        boolean isOwner = ownerId.equals(actorId);
        if (!isOwner && !isMember(actorId, projectId)) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 只查这个项目的普通成员。加入时间相同时，按关系记录编号排列。
        List<ProjectMember> members = memberMapper.selectList(
                new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .orderByAsc(ProjectMember::getJoinTime)
                        .orderByAsc(ProjectMember::getId)
        );

        // 收集创建者和成员的用户编号，一次查出需要的用户名。
        List<Long> userIds = new ArrayList<>();
        userIds.add(ownerId);
        for (ProjectMember member : members) {
            userIds.add(member.getUserId());
        }

        // 只从用户表读取编号和用户名。in 对应 SQL 的 WHERE id IN (...)。
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>()
                .select(User::getId, User::getUsername)
                .in(User::getId, userIds));

        // 把用户名按编号保存。之后用 get(用户编号) 就能找到对应的名字。
        Map<Long, String> usernames = new HashMap<>();
        for (User user : users) {
            usernames.put(user.getId(), user.getUsername());
        }
        if (!usernames.keySet().containsAll(userIds)) {
            // 关系里引用了不存在的用户，无法组装完整成员信息。
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 创建者固定放第一项。普通成员按之前查出的顺序追加。
        List<ProjectMemberVO> result = new ArrayList<>();
        result.add(new ProjectMemberVO(ownerId, usernames.get(ownerId), "OWNER"));
        for (ProjectMember member : members) {
            Long userId = member.getUserId();
            result.add(new ProjectMemberVO(userId, usernames.get(userId), "MEMBER"));
        }
        return result;
    }

    @Override
    @Transactional
    public ProjectMemberVO addMember(Long actorId, Long projectId, String username) {
        // 先确认项目存在，并且发起添加的人就是创建者。
        Project project = projectMapper.selectById(projectId);
        if (project == null || !project.getOwnerId().equals(actorId)) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 检查原始长度，再去掉首尾空格。空白用户名不能用于查找用户。
        if (username == null || username.length() > 50) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.isBlank()) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }

        // 按用户名查已注册用户，只读取后续需要的两个字段。
        User target = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .select(User::getId, User::getUsername)
                .eq(User::getUsername, trimmedUsername));
        if (target == null) {
            throw new ServiceException(StatusCode.DATA_UNEXISTS);
        }

        // 创建者已记录在 project.owner_id，拒绝把他再加入普通成员表。
        if (project.getOwnerId().equals(target.getId())) {
            throw new ServiceException(StatusCode.VALIDATE_ERROR);
        }

        // 第一次添加会写入当前时间，重复添加由数据库保留旧时间。
        memberMapper.insertIfAbsent(projectId, target.getId(), LocalDateTime.now());

        // 首次添加和重复添加都返回同一种成员信息。
        return new ProjectMemberVO(target.getId(), target.getUsername(), "MEMBER");
    }
}