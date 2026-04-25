package org.example.smartunipro.service;

import lombok.RequiredArgsConstructor;

import org.example.smartunipro.dto.AIInteractionDto;
import org.example.smartunipro.entity.AIInteraction;
import org.example.smartunipro.entity.Student;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.AIInteractionMapper;
import org.example.smartunipro.repository.AIInteractionRepository;
import org.example.smartunipro.repository.StudentRepository;
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
    private final StudentRepository       studentRepository;

    public AIInteractionDto ask(AIInteractionDto dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new CustomException(
                        "Student not found with id: " + dto.getStudentId(),
                        HttpStatus.NOT_FOUND));

        String generatedAnswer = generateAnswer(dto.getQuestion());

        AIInteraction interaction = new AIInteraction();
        interaction.setQuestion(dto.getQuestion());
        interaction.setAnswer(generatedAnswer);
        interaction.setAskedAt(LocalDateTime.now());
        interaction.setStudent(student);

        AIInteraction saved = aiInteractionRepository.save(interaction);

        AIInteractionDto response = aiInteractionMapper.toDto(saved);
        response.setMessage("Your question has been answered successfully");  // ✅
        return response;
    }


    public List<AIInteractionDto> getHistory(Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new CustomException(
                    "Student not found with id: " + studentId,
                    HttpStatus.NOT_FOUND);
        }

        return aiInteractionRepository
                .findByStudentIdOrderByAskedAtDesc(studentId)
                .stream()
                .map(aiInteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    private String generateAnswer(String question) {
        return "This is a placeholder answer for: \"" + question + "\"";
    }
}