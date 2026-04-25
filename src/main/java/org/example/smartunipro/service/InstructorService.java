package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.InstructorDto;
import org.example.smartunipro.entity.Instructor;
import org.example.smartunipro.entity.User;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.InstructorMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.repository.InstructorRepository;
import org.example.smartunipro.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository       userRepository;
    private final InstructorMapper     instructorMapper;

    public InstructorDto create(InstructorDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + dto.getUserId(),
                        HttpStatus.NOT_FOUND));

        if (instructorRepository.existsByUserId(dto.getUserId())) {
            throw new CustomException(
                    "Instructor already exists for user id: " + dto.getUserId(),
                    HttpStatus.CONFLICT);
        }

        user.setRole(Role.INSTRUCTOR);

        Instructor instructor = new Instructor();
        instructor.setDepartment(dto.getDepartment());
        instructor.setUser(user);

        return instructorMapper.toDto(instructorRepository.save(instructor));
    }

    public List<InstructorDto> getAll() {
        return instructorRepository.findAll()
                .stream()
                .map(instructorMapper::toDto)
                .collect(Collectors.toList());
    }

    public InstructorDto getById(Long id) {
        return instructorMapper.toDto(findOrThrow(id));
    }

    public InstructorDto update(Long id, InstructorDto dto) {
        Instructor instructor = findOrThrow(id);
        instructorMapper.updateToEntity(dto, instructor);

        // allow reassigning to a different user if needed
        if (dto.getUserId() != null &&
                !dto.getUserId().equals(instructor.getUser().getId())) {

            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new CustomException(
                            "User not found with id: " + dto.getUserId(),
                            HttpStatus.NOT_FOUND));

            if (instructorRepository.existsByUserId(dto.getUserId())) {
                throw new CustomException(
                        "Another instructor already linked to user id: " + dto.getUserId(),
                        HttpStatus.CONFLICT);
            }

            user.setRole(Role.INSTRUCTOR);
            instructor.setUser(user);
        }

        return instructorMapper.toDto(instructorRepository.save(instructor));
    }

    public void deleteById(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new CustomException(
                    "Instructor not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        instructorRepository.deleteById(id);
    }

    private Instructor findOrThrow(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Instructor not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}