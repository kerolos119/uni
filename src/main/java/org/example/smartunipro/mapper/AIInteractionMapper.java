package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.AIInteractionDto;
import org.example.smartunipro.entity.AIInteraction;
import org.springframework.stereotype.Component;

@Component
public class AIInteractionMapper extends AbstractMapper<AIInteractionDto, AIInteraction> {

    public AIInteractionMapper() {
        super(AIInteractionDto.class, AIInteraction.class);
    }

    @Override
    public AIInteractionDto toDto(AIInteraction entity) {
        AIInteractionDto dto = new AIInteractionDto();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setAnswer(entity.getAnswer());
        dto.setAskedAt(entity.getAskedAt());
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
        }
        return dto;
    }

    @Override
    public AIInteraction updateToEntity(AIInteractionDto dto, AIInteraction entity) {
        if (dto.getQuestion() != null) entity.setQuestion(dto.getQuestion());
        return entity;
    }
}