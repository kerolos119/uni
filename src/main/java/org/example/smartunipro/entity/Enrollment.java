package org.example.smartunipro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.EnrollmentStatus;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "session_id"})
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Enrollment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    /** Student — must be a User with role = STUDENT */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
}