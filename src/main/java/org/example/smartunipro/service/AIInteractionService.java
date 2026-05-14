package org.example.smartunipro.service;

import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AIInteractionDto;
import org.example.smartunipro.entity.AIInteraction;
import org.example.smartunipro.entity.User;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.AIInteractionMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.repository.AIInteractionRepository;
import org.example.smartunipro.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIInteractionService {

    private final AIInteractionRepository aiInteractionRepository;
    private final AIInteractionMapper     aiInteractionMapper;
    private final UserRepository          userRepository;

    public AIInteractionDto ask(AIInteractionDto dto) {
        User student = resolveStudent(dto.getStudentId());

        AIInteraction interaction = new AIInteraction();
        interaction.setQuestion(dto.getQuestion());
        interaction.setAnswer(generateAnswer(dto.getQuestion()));
        interaction.setAskedAt(LocalDateTime.now());
        interaction.setStudent(student);

        AIInteractionDto response = aiInteractionMapper.toDto(
                aiInteractionRepository.save(interaction));
        response.setMessage("Your question has been answered successfully");
        return response;
    }

    public List<AIInteractionDto> getHistory(Long studentId) {
        resolveStudent(studentId); // validates existence + role
        return aiInteractionRepository
                .findByStudent_IdOrderByAskedAtDesc(studentId)
                .stream()
                .map(aiInteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User resolveStudent(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + id, HttpStatus.NOT_FOUND));
        if (user.getRole() != Role.STUDENT) {
            throw new CustomException(
                    "User with id " + id + " is not a STUDENT", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    private String generateAnswer(String question) {
        return "This is a placeholder answer for: \"" + question + "\"";
    }
}