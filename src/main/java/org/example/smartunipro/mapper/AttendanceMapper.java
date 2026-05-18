package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.AttendanceDto;
import org.example.smartunipro.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper extends AbstractMapper<AttendanceDto, Attendance> {

    public AttendanceMapper() {
        super(AttendanceDto.class, Attendance.class);
    }

    @Override
    public AttendanceDto toDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setStatus(attendance.getStatus());
        dto.setTimestamp(attendance.getTimestamp());
        dto.setLatitude(attendance.getLatitude());
        dto.setLongitude(attendance.getLongitude());

        if (attendance.getStudent() != null) {
            dto.setStudentId(attendance.getStudent().getId());
            dto.setStudentName(attendance.getStudent().getName());
        }
        if (attendance.getSession() != null) {
            dto.setSessionId(attendance.getSession().getId());
            dto.setSessionName(attendance.getSession().getName());
        }
        return dto;
    }

    @Override
    public Attendance updateToEntity(AttendanceDto dto, Attendance entity) {
        // Only status is updatable after the fact
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        return entity;
    }
}