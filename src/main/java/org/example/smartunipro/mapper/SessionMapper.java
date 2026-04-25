package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.SessionDto;
import org.example.smartunipro.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper extends AbstractMapper<SessionDto, Session> {

    public SessionMapper() {
        super(SessionDto.class, Session.class);
    }

    @Override
    public SessionDto toDto(Session session) {
        SessionDto dto = super.toDto(session);

        if (session.getCourse() != null) {
            dto.setCourseId(session.getCourse().getId());
            dto.setCourseName(session.getCourse().getName());
        }
        if (session.getInstructor() != null) {
            dto.setInstructorId(session.getInstructor().getId());
            // name comes from the linked User
            if (session.getInstructor().getUser() != null) {
                dto.setInstructorName(String.valueOf(session.getInstructor().getUser()));
            }
        }
        if (session.getLocation() != null) {
            dto.setLocationId(session.getLocation().getId());
            dto.setLocationName(session.getLocation().getName());
        }

        return dto;
    }

    @Override
    public Session updateToEntity(SessionDto dto, Session entity) {
        if (dto.getName()      != null) entity.setName(dto.getName());
        if (dto.getType()      != null) entity.setType(dto.getType());
        if (dto.getStartTime() != null) entity.setStartTime(dto.getStartTime());
        if (dto.getEndTime()   != null) entity.setEndTime(dto.getEndTime());
        return entity;
    }
}