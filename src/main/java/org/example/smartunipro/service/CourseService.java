package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.CourseDto;
import org.example.smartunipro.dto.CourseFilterDto;
import org.example.smartunipro.entity.Course;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.CourseMapper;
import org.example.smartunipro.repository.CourseRepository;
import org.example.smartunipro.repository.FilterableRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService extends FilterableService<Course, CourseDto, CourseFilterDto> {

    private static final List<String> SORTABLE_FIELDS = List.of("name", "code");

    private final CourseRepository courseRepository;
    private final CourseMapper     courseMapper;

    // ── FilterableService wiring ─────────────────────────────────────────────

    @Override
    protected List<String> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    protected FilterableRepository<Course, ?> repository() {
        return courseRepository;
    }

    @Override
    protected Specification<Course> toSpec(CourseFilterDto f) {
        return SpecificationBuilder.<Course>builder()
                .like("name", f.getName())
                .like("code", f.getCode())
                .build();
    }

    @Override
    protected CourseDto toDto(Course entity) {
        return courseMapper.toDto(entity);
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public CourseDto createCourse(CourseDto dto) {
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new CustomException(
                    "Course with code '" + dto.getCode() + "' already exists",
                    HttpStatus.CONFLICT);
        }
        Course saved = courseRepository.save(courseMapper.toEntity(dto));
        return courseMapper.toDto(saved);
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    public CourseDto getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Course not found with id: " + id, HttpStatus.NOT_FOUND));
        return courseMapper.toDto(course);
    }

    public CourseDto update(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Course not found with id: " + id, HttpStatus.NOT_FOUND));
        courseMapper.updateToEntity(dto, course);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public void deleteById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new CustomException(
                    "Course not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        courseRepository.deleteById(id);
    }
}