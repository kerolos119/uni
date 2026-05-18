package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.UserDto;
import org.example.smartunipro.dto.UserFilterDto;
import org.example.smartunipro.entity.User;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.UserMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.repository.FilterableRepository;
import org.example.smartunipro.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService extends FilterableService<User, UserDto, UserFilterDto> {

    private static final List<String> SORTABLE_FIELDS =
            List.of("name", "email", "role", "level", "department");

    private final UserRepository  userRepository;
    private final UserMapper      userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    protected UserDto toDto(User entity) { return userMapper.toDto(entity); }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public UserDto createUser(UserDto dto) {
        validateRoleFields(dto);

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(
                    "Email already in use: " + dto.getEmail(), HttpStatus.CONFLICT);
        }
        if (dto.getRole() == Role.STUDENT
                && dto.getAcademicNumber() != null
                && userRepository.existsByAcademicNumber(dto.getAcademicNumber())) {
            throw new CustomException(
                    "Academic number already exists: " + dto.getAcademicNumber(),
                    HttpStatus.CONFLICT);
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userMapper.toDto(userRepository.save(user));
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getById(Long id) {
        return userMapper.toDto(findOrThrow(id));
    }

    /** Convenience: all students */
    public List<UserDto> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /** Convenience: all instructors */
    public List<UserDto> getAllInstructors() {
        return userRepository.findByRole(Role.INSTRUCTOR).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public UserDto update(Long id, UserDto dto) {
        User user = findOrThrow(id);

        // If role is changing, re-validate required fields for the new role
        Role targetRole = dto.getRole() != null ? dto.getRole() : user.getRole();
        UserDto merged = buildMergedDto(dto, user, targetRole);
        validateRoleFields(merged);

        userMapper.updateToEntity(dto, user);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Clear fields that don't belong to the new role
        clearIrrelevantFields(user);

        return userMapper.toDto(userRepository.save(user));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(
                    "User not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    // ── ROLE VALIDATION ───────────────────────────────────────────────────────

    /**
     * Enforces role-specific required fields:
     * STUDENT  → academicNumber + level required
     * INSTRUCTOR → department required
     * ADMIN    → no extra fields needed
     */
    private void validateRoleFields(UserDto dto) {
        if (dto.getRole() == null) return;

        switch (dto.getRole()) {
            case STUDENT -> {
                if (dto.getAcademicNumber() == null || dto.getAcademicNumber().isBlank()) {
                    throw new CustomException(
                            "academicNumber is required for STUDENT role",
                            HttpStatus.BAD_REQUEST);
                }
                if (dto.getLevel() == null || dto.getLevel().isBlank()) {
                    throw new CustomException(
                            "level is required for STUDENT role",
                            HttpStatus.BAD_REQUEST);
                }
            }
            case INSTRUCTOR -> {
                if (dto.getDepartment() == null || dto.getDepartment().isBlank()) {
                    throw new CustomException(
                            "department is required for INSTRUCTOR role",
                            HttpStatus.BAD_REQUEST);
                }
            }
            // ADMIN — no extra fields required
        }
    }

    /**
     * After a role change, null out fields that no longer apply.
     * e.g. STUDENT → INSTRUCTOR: clear academicNumber + level
     */
    private void clearIrrelevantFields(User user) {
        switch (user.getRole()) {
            case STUDENT -> user.setDepartment(null);
            case INSTRUCTOR -> {
                user.setAcademicNumber(null);
                user.setLevel(null);
            }
            case ADMIN -> {
                user.setAcademicNumber(null);
                user.setLevel(null);
                user.setDepartment(null);
            }
        }
    }

    /** Build a fully-merged DTO (existing entity data + incoming changes) for validation */
    private UserDto buildMergedDto(UserDto incoming, User existing, Role targetRole) {
        UserDto merged = new UserDto();
        merged.setRole(targetRole);
        merged.setAcademicNumber(
                incoming.getAcademicNumber() != null
                        ? incoming.getAcademicNumber()
                        : existing.getAcademicNumber());
        merged.setLevel(
                incoming.getLevel() != null
                        ? incoming.getLevel()
                        : existing.getLevel());
        merged.setDepartment(
                incoming.getDepartment() != null
                        ? incoming.getDepartment()
                        : existing.getDepartment());
        return merged;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + id, HttpStatus.NOT_FOUND));
    }


    // ── FilterableService wiring ─────────────────────────────────────────────

    @Override
    protected List<String> sortableFields() { return SORTABLE_FIELDS; }

    @Override
    protected FilterableRepository<User, ?> repository() { return userRepository; }

    @Override
    protected Specification<User> toSpec(UserFilterDto f) {
        return SpecificationBuilder.<User>builder()
                .like("name",           f.getName())
                .like("email",          f.getEmail())
                .equal("role",          f.getRole())
                .like("academicNumber", f.getAcademicNumber())
                .like("level",          f.getLevel())
                .like("department",     f.getDepartment())
                .build();
    }
}