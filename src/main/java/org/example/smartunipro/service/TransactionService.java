package org.example.smartunipro.service;

import org.example.smartunipro.entity.AIInteraction;
import org.example.smartunipro.entity.Attendance;
import org.example.smartunipro.entity.Material;
import org.example.smartunipro.repository.AIInteractionRepository;
import org.example.smartunipro.repository.AttendanceRepository;
import org.example.smartunipro.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final AttendanceRepository attendanceRepository;
    private final MaterialRepository materialRepository;
    private final AIInteractionRepository aiInteractionRepository;

    public TransactionService(AttendanceRepository attendanceRepository, MaterialRepository materialRepository, AIInteractionRepository aiInteractionRepository) {
        this.attendanceRepository = attendanceRepository;
        this.materialRepository = materialRepository;
        this.aiInteractionRepository = aiInteractionRepository;
    }

    public Attendance markAttendance(Attendance attendance) {
        attendance.setTimestamp(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }
    public Material saveMaterial(Material material) { return materialRepository.save(material); }
    public AIInteraction saveAIInteraction(AIInteraction interaction) {
        interaction.setCreatedAt(LocalDateTime.now());
        return aiInteractionRepository.save(interaction);
    }
}