package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.LocationDto;
import org.example.smartunipro.dto.LocationFilterDto;
import org.example.smartunipro.entity.Location;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.LocationMapper;
import org.example.smartunipro.repository.FilterableRepository;
import org.example.smartunipro.repository.LocationRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService extends FilterableService<Location, LocationDto, LocationFilterDto> {

    private static final List<String> SORTABLE_FIELDS =
            List.of("name", "latitude", "longitude");

    private final LocationRepository locationRepository;
    private final LocationMapper     locationMapper;



    @Override
    protected LocationDto toDto(Location entity) {
        return locationMapper.toDto(entity);
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public LocationDto createLocation(@Valid LocationDto request) {
        Location location = locationMapper.toEntity(request);
        return locationMapper.toDto(locationRepository.save(location));
    }

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDto)
                .collect(Collectors.toList());
    }

    public LocationDto getById(Long id) {
        return locationMapper.toDto(findOrThrow(id));
    }

    public LocationDto update(Long id, @Valid LocationDto request) {
        Location location = findOrThrow(id);
        locationMapper.updateToEntity(request, location);
        return locationMapper.toDto(locationRepository.save(location));
    }

    public void deleteById(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new CustomException(
                    "Location not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        locationRepository.deleteById(id);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Location findOrThrow(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Location not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    // ── FilterableService wiring ─────────────────────────────────────────────

    @Override
    protected List<String> sortableFields() {
        return SORTABLE_FIELDS;
    }

    @Override
    protected FilterableRepository<Location, ?> repository() {
        return locationRepository;
    }

    @Override
    protected Specification<Location> toSpec(LocationFilterDto f) {
        return SpecificationBuilder.<Location>builder()
                .like("name",                    f.getName())
                .greaterThanOrEqual("latitude",  f.getMinLatitude())
                .lessThanOrEqual("latitude",     f.getMaxLatitude())
                .greaterThanOrEqual("longitude", f.getMinLongitude())
                .lessThanOrEqual("longitude",    f.getMaxLongitude())
                .build();
    }
}