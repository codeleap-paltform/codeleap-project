package com.example.codeleapbackend.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.codeleapbackend.project.pojo.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}