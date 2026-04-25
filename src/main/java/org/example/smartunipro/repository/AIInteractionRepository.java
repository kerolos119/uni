package org.example.smartunipro.repository;

import org.example.smartunipro.entity.AIInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIInteractionRepository extends JpaRepository<AIInteraction, Long> {

    List<AIInteraction> findByStudentIdOrderByAskedAtDesc(Long studentId);
}