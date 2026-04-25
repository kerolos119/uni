package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.StudentDto;
import org.example.smartunipro.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper extends AbstractMapper<StudentDto, Student> {

    public StudentMapper() {
        super(StudentDto.class, Student.class);
    }

    @Override
    public StudentDto toDto(Student student) {
        StudentDto dto = super.toDto(student);
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setUserName(student.getUser().getName());
            dto.setUserEmail(student.getUser().getEmail());
        }
        return dto;
    }

    @Override
    public Student updateToEntity(StudentDto dto, Student entity) {
        if (dto.getAcademicNumber() != null) entity.setAcademicNumber(dto.getAcademicNumber());
        if (dto.getLevel()          != null) entity.setLevel(dto.getLevel());
        if (dto.getName()           != null) entity.setName(dto.getName());
        if (dto.getEmail()          != null) entity.setEmail(dto.getEmail());
        return entity;
    }
}