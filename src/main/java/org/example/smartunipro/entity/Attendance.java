package org.example.smartunipro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.Status;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "session_id"})
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attendance extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Double latitude;
    private Double longitude;

    /** Student — must be a User with role = STUDENT */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
}