package org.example.smartunipro.mapper;


import org.example.smartunipro.dto.LocationDto;

import org.example.smartunipro.entity.Location;
import org.springframework.stereotype.Component;


@Component
public class LocationMapper extends AbstractMapper<LocationDto, Location> {

    public LocationMapper() {
        super(LocationDto.class, Location.class);
    }

    @Override
    public Location updateToEntity(LocationDto dto, Location entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getLatitude() != null) entity.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) entity.setLongitude(dto.getLongitude());
        return entity;
    }
}
