package org.example.smartunipro.repository;

import org.example.smartunipro.entity.AIInteraction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIInteractionRepository extends FilterableRepository<AIInteraction, Long> {
    List<AIInteraction> findByStudent_IdOrderByAskedAtDesc(Long studentId);
}