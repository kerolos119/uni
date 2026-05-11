package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.StudentDto;
import org.example.smartunipro.entity.Student;
import org.example.smartunipro.entity.User;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.StudentMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.repository.StudentRepository;
import org.example.smartunipro.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;

    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByAcademicNumber(dto.getAcademicNumber())) {
            throw new CustomException(
                    "Academic number already exists: " + dto.getAcademicNumber(),
                    HttpStatus.CONFLICT);
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(
                    "Email already exists: " + dto.getEmail(),
                    HttpStatus.CONFLICT);
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + dto.getUserId(),
                        HttpStatus.NOT_FOUND));

        user.setRole(Role.STUDENT);

        Student student = new Student();
        student.setAcademicNumber(dto.getAcademicNumber());
        student.setLevel(dto.getLevel());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setUser(user);

        return studentMapper.toDto(studentRepository.save(student));
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto getById(Long id) {
        return studentMapper.toDto(findOrThrow(id));
    }

    public StudentDto update(Long id, StudentDto dto) {
        Student student = findOrThrow(id);
        studentMapper.updateToEntity(dto, student);
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new CustomException(
                            "User not found with id: " + dto.getUserId(),
                            HttpStatus.NOT_FOUND));
            student.setUser(user);
        }
        return studentMapper.toDto(studentRepository.save(student));
    }

    public void deleteById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new CustomException(
                    "Student not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        studentRepository.deleteById(id);
    }

    private Student findOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Student not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}