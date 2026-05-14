package org.example.smartunipro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartunipro.model.Auditable;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_interactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIInteraction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String question;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String answer;

    @Column(name = "asked_at", nullable = false)
    private LocalDateTime askedAt;

    /** Student — must be a User with role = STUDENT */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;
}