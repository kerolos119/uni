package org.example.smartunipro.mapper;


import org.example.smartunipro.dto.EnrollmentDto;
import org.example.smartunipro.entity.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper extends AbstractMapper<EnrollmentDto, Enrollment> {

    public EnrollmentMapper() {
        super(EnrollmentDto.class, Enrollment.class);
    }

    @Override
    public EnrollmentDto toDto(Enrollment enrollment) {
        EnrollmentDto dto = super.toDto(enrollment);

        if (enrollment.getStudent() != null) {
            dto.setStudentId(enrollment.getStudent().getId());
            dto.setStudentName(enrollment.getStudent().getName());
        }

        if (enrollment.getSession() != null) {
            dto.setSessionId(enrollment.getSession().getId());
            dto.setSessionName(enrollment.getSession().getName());
        }

        return dto;
    }
    @Override
    public Enrollment updateToEntity(EnrollmentDto dto, Enrollment entity) {
        if (dto.getEnrollmentDate() != null) entity.setEnrollmentDate(dto.getEnrollmentDate());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        return entity;
    }
}

