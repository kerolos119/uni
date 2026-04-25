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
    public AIInteraction updateToEntity(AIInteractionDto dto, AIInteraction entity) {
        if (dto.getQuestion() != null) entity.setQuestion(dto.getQuestion());
        return entity;
    }
}