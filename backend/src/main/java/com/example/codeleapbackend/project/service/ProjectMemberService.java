package com.example.codeleapbackend.project.service;

import com.example.codeleapbackend.project.pojo.vo.ProjectMemberVO;

import java.util.List;

/** 定义项目成员相关的业务操作。 */
public interface ProjectMemberService {

    /** 查询某个项目的成员。操作者必须是创建者或普通成员。 */
    List<ProjectMemberVO> listMembers(Long actorId, Long projectId);

    /** 由项目创建者按用户名添加普通成员，并返回该成员的信息。 */
    ProjectMemberVO addMember(Long actorId, Long projectId, String username);

    /** 判断某用户是否有这条普通成员关系。 */
    boolean isMember(Long userId, Long projectId);

    /** 查询某用户作为普通成员加入的所有项目编号。 */
    List<Long> listJoinedProjectIds(Long userId);
}