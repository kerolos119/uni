package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.InstructorDto;
import org.example.smartunipro.entity.Instructor;
import org.springframework.stereotype.Component;

@Component
public class InstructorMapper extends AbstractMapper<InstructorDto, Instructor> {

    public InstructorMapper() {
        super(InstructorDto.class, Instructor.class);
    }

    @Override
    public InstructorDto toDto(Instructor instructor) {
        InstructorDto dto = super.toDto(instructor);
        if (instructor.getUser() != null) {
            dto.setUserId(instructor.getUser().getId());
            dto.setName(instructor.getUser().getName());
            dto.setEmail(instructor.getUser().getEmail());
        }
        return dto;
    }

    @Override
    public Instructor updateToEntity(InstructorDto dto, Instructor entity) {
        if (dto.getDepartment() != null) entity.setDepartment(dto.getDepartment());
        return entity;
    }
}