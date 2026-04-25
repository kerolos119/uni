package org.example.smartunipro.service;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.LocationDto;
import org.example.smartunipro.entity.Location;
import org.example.smartunipro.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository repo;

    //  DTO → Entity
    private Location convertToEntity(LocationDto dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        return location;
    }

    //  Entity → DTO
    private LocationDto convertToResponse(Location location) {
        LocationDto response = new LocationDto();
        response.setId(location.getId());
        response.setName(location.getName());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        return response;
    }

    //  CREATE
    public LocationDto createLocation(@Valid LocationDto request) {
        Location location = convertToEntity(request);
        Location savedLocation = repo.save(location);
        return convertToResponse(savedLocation);
    }

    //  GET ALL
    public List<LocationDto> getAllLocations() {
        return repo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // GET BY ID
    public LocationDto getById(Long id) {
        Location location = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return convertToResponse(location);
    }

    //  UPDATE
    public LocationDto update(Long id, @Valid @RequestBody LocationDto request) {
        Location location = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        if (request.getName() != null) {
            location.setName(request.getName());
        }
        if (request.getLatitude() != null) {
            location.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            location.setLongitude(request.getLongitude());
        }

        Location updatedLocation = repo.save(location);
        return convertToResponse(updatedLocation);
    }

    //  DELETE
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        repo.deleteById(id);
    }
}

