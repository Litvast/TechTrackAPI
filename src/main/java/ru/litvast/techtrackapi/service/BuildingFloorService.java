package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.BuildingFloorDto;
import ru.litvast.techtrackapi.model.dto.BuildingFloorUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.BuildingFloorMapping;
import ru.litvast.techtrackapi.model.entity.Building;
import ru.litvast.techtrackapi.model.entity.BuildingFloor;
import ru.litvast.techtrackapi.repository.BuildingFloorRepository;
import ru.litvast.techtrackapi.repository.BuildingRepository;

@Service
@RequiredArgsConstructor
public class BuildingFloorService {

    private final BuildingFloorRepository buildingFloorRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingFloorMapping buildingFloorMapping;

    @Transactional
    public BuildingFloorDto addBuildingFloor(BuildingFloorDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a floor, you must specify a floor number, not an ID");
        }

        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building with id '%d' not found", dto.getBuildingId())
                ));

        if (buildingFloorRepository.existsByFloorNumberAndBuildingId(dto.getFloorNumber(), dto.getBuildingId())) {
            throw new IllegalArgumentException(
                    String.format("Floor number '%d' already exists in this building", dto.getFloorNumber())
            );
        }

        BuildingFloor buildingFloor = buildingFloorMapping.toEntity(dto);
        System.out.println(buildingFloor.getId());
        buildingFloor.setBuilding(building);
        buildingFloorRepository.save(buildingFloor);
        return buildingFloorMapping.toDto(buildingFloor);
    }

    public Page<BuildingFloorDto> getAllBuildingFloors(Pageable pageable) {
        Page<BuildingFloor> floors = buildingFloorRepository.findAll(pageable);
        if (floors.isEmpty()) {
            throw new NoEntitiesFoundException("No building floors found");
        }
        return floors.map(buildingFloorMapping::toDto);
    }

    public Page<BuildingFloorDto> getFloorsByBuildingId(Long buildingId, Pageable pageable) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new EntityNotFoundException(
                    String.format("Building with id '%d' not found", buildingId)
            );
        }

        Page<BuildingFloor> floors = buildingFloorRepository.findByBuildingId(buildingId, pageable);
        if (floors.isEmpty()) {
            throw new NoEntitiesFoundException("No floors found for this building");
        }
        return floors.map(buildingFloorMapping::toDto);
    }

    public BuildingFloorDto getBuildingFloorById(Long id) {
        BuildingFloor floor = buildingFloorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building floor with id '%d' not found", id)
                ));
        return buildingFloorMapping.toDto(floor);
    }

    public BuildingFloorDto getBuildingFloorByNumberAndBuilding(Integer floorNumber, Long buildingId) {
        BuildingFloor floor = buildingFloorRepository.findByFloorNumberAndBuildingId(floorNumber, buildingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Floor number '%d' not found in building with id '%d'", floorNumber, buildingId)
                ));
        return buildingFloorMapping.toDto(floor);
    }

    @Transactional
    public BuildingFloorDto updateBuildingFloor(Long id, BuildingFloorUpdateDto dto) {
        BuildingFloor existingFloor = buildingFloorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building floor with id '%d' not found", id)
                ));

        if (dto.getFloorNumber() != null && !existingFloor.getFloorNumber().equals(dto.getFloorNumber())) {
            Long buildingId = dto.getBuildingId() != null ? dto.getBuildingId() : existingFloor.getBuilding().getId();
            if (buildingFloorRepository.existsByFloorNumberAndBuildingId(dto.getFloorNumber(), buildingId)) {
                throw new IllegalArgumentException(
                        String.format("Floor number '%d' already exists in this building", dto.getFloorNumber())
                );
            }
            existingFloor.setFloorNumber(dto.getFloorNumber());
        }

        if (dto.getDescription() != null) existingFloor.setDescription(dto.getDescription());

        if (dto.getBuildingId() != null) {
            Building building = buildingRepository.findById(dto.getBuildingId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Building with id '%d' not found", dto.getBuildingId())
                    ));
            existingFloor.setBuilding(building);
        }

        buildingFloorRepository.save(existingFloor);
        return buildingFloorMapping.toDto(existingFloor);
    }

    @Transactional
    public void deleteBuildingFloor(Long id) {
        if (!buildingFloorRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Building floor with id '%d' not found", id)
            );
        }
        buildingFloorRepository.deleteById(id);
    }
}