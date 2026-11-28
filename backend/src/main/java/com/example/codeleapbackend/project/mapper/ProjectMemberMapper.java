package com.example.codeleapbackend.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.codeleapbackend.project.pojo.entity.ProjectMember;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/** 提供项目成员关系的数据库读写入口。 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {

    /** 首次添加时插入一行，重复添加时保留原来的记录和加入时间。 */
    @Insert("""
            INSERT INTO project_member (project_id, user_id, join_time)
            VALUES (#{projectId}, #{userId}, #{joinTime})
            ON DUPLICATE KEY UPDATE join_time = project_member.join_time
            """)
    int insertIfAbsent(@Param("projectId") Long projectId,
                       @Param("userId") Long userId,
                       @Param("joinTime") LocalDateTime joinTime);
}