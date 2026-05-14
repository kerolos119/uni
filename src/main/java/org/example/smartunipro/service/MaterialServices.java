package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.MaterialDto;
import org.example.smartunipro.dto.MaterialFilterDto;
import org.example.smartunipro.entity.Course;
import org.example.smartunipro.entity.Material;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.MaterialMapper;
import org.example.smartunipro.repository.CourseRepository;
import org.example.smartunipro.repository.FilterableRepository;
import org.example.smartunipro.repository.MaterialRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialServices extends FilterableService<Material, MaterialDto, MaterialFilterDto> {

    private static final List<String> SORTABLE_FIELDS = List.of("title");

    private final MaterialRepository materialRepository;
    private final CourseRepository   courseRepository;
    private final MaterialMapper     materialMapper;

    // ── FilterableService wiring ─────────────────────────────────────────────

    @Override
    protected List<String> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    protected FilterableRepository<Material, ?> repository() {
        return materialRepository;
    }

    @Override
    protected Specification<Material> toSpec(MaterialFilterDto f) {
        SpecificationBuilder<Material> builder = SpecificationBuilder.<Material>builder()
                .like("title",      f.getTitle())
                .equal("course.id", f.getCourseId())
                .like("course.name", f.getCourseName());

        // hasPdf: true → pdfUrl IS NOT NULL, false → IS NULL
        if (Boolean.TRUE.equals(f.getHasPdf())) {
            builder.isNotNull("pdfUrl");
        } else if (Boolean.FALSE.equals(f.getHasPdf())) {
            builder.isNull("pdfUrl");
        }

        // hasVideo: true → videoUrl IS NOT NULL, false → IS NULL
        if (Boolean.TRUE.equals(f.getHasVideo())) {
            builder.isNotNull("videoUrl");
        } else if (Boolean.FALSE.equals(f.getHasVideo())) {
            builder.isNull("videoUrl");
        }

        return builder.build();
    }

    @Override
    protected MaterialDto toDto(Material entity) {
        return materialMapper.toDto(entity);
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public MaterialDto materialUpload(@Valid MaterialDto request) {
        Material material = buildMaterialFromDto(request, new Material());
        return materialMapper.toDto(materialRepository.save(material));
    }

    public List<MaterialDto> getAllMaterial() {
        return materialRepository.findAll()
                .stream()
                .map(materialMapper::toDto)
                .collect(Collectors.toList());
    }

    public MaterialDto getById(Long id) {
        return materialMapper.toDto(findOrThrow(id));
    }

    public MaterialDto update(Long id, MaterialDto dto) {
        Material material = findOrThrow(id);
        materialMapper.updateToEntity(dto, material);
        if (dto.getCourseId() != null) material.setCourse(resolveCourse(dto.getCourseId()));
        return materialMapper.toDto(materialRepository.save(material));
    }

    public void delete(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new CustomException(
                    "Material not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        materialRepository.deleteById(id);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Material findOrThrow(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Material not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Material buildMaterialFromDto(MaterialDto dto, Material material) {
        material.setTitle(dto.getTitle());
        material.setPdfUrl(dto.getPdfUrl());
        material.setVideoUrl(dto.getVideoUrl());
        material.setCourse(resolveCourse(dto.getCourseId()));
        return material;
    }

    private Course resolveCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Course not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}