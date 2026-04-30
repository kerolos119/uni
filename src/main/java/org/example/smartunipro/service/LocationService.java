package org.example.smartunipro.service;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.LocationDto;
import org.example.smartunipro.entity.Location;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.LocationMapper;
import org.example.smartunipro.repository.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository repo;
    private final LocationMapper locationMapper;

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
        return locationMapper.toDto(repo.save(location));
    }

    //  GET ALL
    public List<LocationDto> getAllLocations() {
        return repo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // GET BY ID
    public LocationDto getById(Long id) {
        return convertToResponse(findOrThrow(id));
    }

    //  UPDATE

    public LocationDto update(Long id, @Valid LocationDto request) {
        Location location = findOrThrow(id);

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
            throw new CustomException("Location not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
    }


    // ── Helpers ─────────────────────────────────────────────────────────────

    private Location findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Location not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}


