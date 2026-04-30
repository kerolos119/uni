package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.MaterialDto;
import org.example.smartunipro.entity.Material;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper extends AbstractMapper<MaterialDto, Material> {

    public MaterialMapper() {
        super(MaterialDto.class, Material.class);
    }

    @Override
    public MaterialDto toDto(Material material) {
        MaterialDto dto = super.toDto(material);

        if (material.getCourse() != null) {
            dto.setCourseId(material.getCourse().getId());
            dto.setCourseName(material.getCourse().getName());
        }
        return dto;
    }


    @Override
    public Material updateToEntity(MaterialDto dto, Material entity) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getPdfUrl() != null) entity.setPdfUrl(dto.getPdfUrl());
        if (dto.getVideoUrl() != null) entity.setVideoUrl(dto.getVideoUrl());
        return entity;
    }
}


