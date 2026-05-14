package org.example.smartunipro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ── STUDENT fields ────────────────────────────────────────────────────────

    /** Unique academic number — required when role = STUDENT */
    @Column(name = "academic_number", unique = true, length = 50)
    private String academicNumber;

    /** Study level — required when role = STUDENT */
    @Column(length = 50)
    private String level;

    // ── INSTRUCTOR fields ─────────────────────────────────────────────────────

    /** Department — required when role = INSTRUCTOR */
    @Column(length = 100)
    private String department;
}