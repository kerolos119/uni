package org.example.smartunipro.service;



import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.MaterialDto;
import org.example.smartunipro.entity.Course;
import org.example.smartunipro.entity.Material;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.MaterialMapper;
import org.example.smartunipro.repository.CourseRepository;
import org.example.smartunipro.repository.MaterialRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialServices {

    private final MaterialRepository repo;
    private final CourseRepository courseRepo;
    private final MaterialMapper materialMapper;


    public MaterialDto materialUpload(@Valid MaterialDto request) {

        Material material = buildMaterialFromDto(request, new Material());

        return materialMapper.toDto(repo.save(material));
    }

    // GET ALL
    public List<MaterialDto> getAllMaterial() {
        return repo.findAll()
                .stream()
                .map(materialMapper::toDto)
                .collect(Collectors.toList());
    }

    //  GET BY ID
    public MaterialDto getById(Long id) {
        Material material = findOrThrow(id);
        return materialMapper.toDto(material);
    }

    //  UPDATE
    public MaterialDto update(Long id, MaterialDto dto) {
        Material material = findOrThrow(id);
        materialMapper.updateToEntity(dto, material);
        // re-resolve FK if client sent new IDs
        if (dto.getCourseId()     != null) material.setCourse(resolveCourse(dto.getCourseId()));
        return materialMapper.toDto(repo.save(material));
    }

    //  DELETE
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new CustomException("Material not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
    }

// ── helpers ─────────────────────────────────────────────────────────────

    private Material findOrThrow(Long id) {
        return repo.findById(id)
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
        return courseRepo.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Course not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}