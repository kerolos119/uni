package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.CourseDto;
import org.example.smartunipro.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper extends AbstractMapper<CourseDto, Course> {

    public CourseMapper() {
        super(CourseDto.class, Course.class);
    }

    @Override
    public Course updateToEntity(CourseDto dto, Course entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        return entity;
    }
}