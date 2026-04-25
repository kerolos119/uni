package org.example.smartunipro.service;



import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.MaterialDto;
import org.example.smartunipro.entity.Course;
import org.example.smartunipro.entity.Material;
import org.example.smartunipro.repository.CourseRepository;
import org.example.smartunipro.repository.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialServices {

    private final MaterialRepository repo;
    private final CourseRepository courseRepo;



    public MaterialDto materialUpload(@Valid MaterialDto request) {

        Material material = MaterialMapper.toEntity(request);

        //  (تحويل courseId → Course)
        Course course = courseRepo.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        material.setCourse(course);

        Material saved = repo.save(material);

        return MaterialMapper.toDto(saved);
    }


    // GET ALL
    public Collection<Material> getAll() {
        return repo.findAll();
    }

    //  GET BY ID
    public Material getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));
    }

    //  UPDATE
    public MaterialDto update(Long id, @Valid MaterialDto request) {
        Material material = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));

        MaterialMapper.updateEntity(request, material);

        Material saved = repo.save(material);

        return MaterialMapper.toDto(saved);
    }
    //  DELETE
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Material not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
