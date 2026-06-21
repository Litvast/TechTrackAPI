package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingFloorService {

    private final BuildingFloorRepository buildingFloorRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingFloorMapping buildingFloorMapping;

    @Transactional
    public BuildingFloorDto addBuildingFloor(BuildingFloorDto dto) {
        log.info("=== НАЧАЛО: Добавление этажа ===");
        log.info("Этаж: {}, Здание ID: {}", dto.getFloorNumber(), dto.getBuildingId());

        if (dto.getId() != null) {
            log.error("Передан ID при создании этажа. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a floor, you must specify a floor number, not an ID");
        }

        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> {
                    log.error("Здание с ID {} не найдено", dto.getBuildingId());
                    return new EntityNotFoundException(
                            String.format("Building with id '%d' not found", dto.getBuildingId())
                    );
                });

        log.info("Здание найдено: {}", building.getName());

        if (buildingFloorRepository.existsByFloorNumberAndBuildingId(dto.getFloorNumber(), dto.getBuildingId())) {
            log.warn("Этаж {} в здании {} уже существует", dto.getFloorNumber(), building.getName());
            throw new IllegalArgumentException(
                    String.format("Floor number '%d' already exists in this building", dto.getFloorNumber())
            );
        }

        BuildingFloor buildingFloor = buildingFloorMapping.toEntity(dto);
        buildingFloor.setBuilding(building);
        buildingFloorRepository.save(buildingFloor);

        log.info("Этаж создан. ID: {}", buildingFloor.getId());
        log.info("=== УСПЕШНО: Этаж добавлен ===");

        return buildingFloorMapping.toDto(buildingFloor);
    }

    public Page<BuildingFloorDto> getAllBuildingFloors(Pageable pageable) {
        log.debug("Запрос всех этажей с пагинацией");

        Page<BuildingFloor> floors = buildingFloorRepository.findAll(pageable);
        if (floors.isEmpty()) {
            log.warn("Этажи не найдены");
            throw new NoEntitiesFoundException("No building floors found");
        }

        log.debug("Найдено {} этажей", floors.getTotalElements());
        return floors.map(buildingFloorMapping::toDto);
    }

    public Page<BuildingFloorDto> getFloorsByBuildingId(Long buildingId, Pageable pageable) {
        log.debug("Поиск этажей по зданию ID: {}", buildingId);

        if (!buildingRepository.existsById(buildingId)) {
            log.error("Здание с ID {} не найдено", buildingId);
            throw new EntityNotFoundException(
                    String.format("Building with id '%d' not found", buildingId)
            );
        }

        Page<BuildingFloor> floors = buildingFloorRepository.findByBuildingId(buildingId, pageable);
        if (floors.isEmpty()) {
            log.warn("Этажи для здания ID {} не найдены", buildingId);
            throw new NoEntitiesFoundException("No floors found for this building");
        }

        log.debug("Найдено {} этажей для здания ID {}", floors.getTotalElements(), buildingId);
        return floors.map(buildingFloorMapping::toDto);
    }

    public BuildingFloorDto getBuildingFloorById(Long id) {
        log.debug("Поиск этажа по ID: {}", id);

        BuildingFloor floor = buildingFloorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Этаж с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Building floor with id '%d' not found", id)
                    );
                });

        return buildingFloorMapping.toDto(floor);
    }

    public BuildingFloorDto getBuildingFloorByNumberAndBuilding(Integer floorNumber, Long buildingId) {
        log.debug("Поиск этажа {} в здании {}", floorNumber, buildingId);

        BuildingFloor floor = buildingFloorRepository.findByFloorNumberAndBuildingId(floorNumber, buildingId)
                .orElseThrow(() -> {
                    log.error("Этаж {} в здании {} не найден", floorNumber, buildingId);
                    return new EntityNotFoundException(
                            String.format("Floor number '%d' not found in building with id '%d'", floorNumber, buildingId)
                    );
                });

        return buildingFloorMapping.toDto(floor);
    }

    public long getCountFloors() {
        log.debug("Подсчёт общего количества этажей");
        return buildingFloorRepository.count();
    }

    @Transactional
    public BuildingFloorDto updateBuildingFloor(Long id, BuildingFloorUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление этажа ===");
        log.info("ID этажа: {}", id);

        BuildingFloor existingFloor = buildingFloorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Этаж с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Building floor with id '%d' not found", id)
                    );
                });

        if (dto.getFloorNumber() != null && !existingFloor.getFloorNumber().equals(dto.getFloorNumber())) {
            Long buildingId = dto.getBuildingId() != null ? dto.getBuildingId() : existingFloor.getBuilding().getId();
            log.info("Изменение номера этажа: {} -> {}", existingFloor.getFloorNumber(), dto.getFloorNumber());

            if (buildingFloorRepository.existsByFloorNumberAndBuildingId(dto.getFloorNumber(), buildingId)) {
                log.warn("Этаж {} в здании {} уже существует", dto.getFloorNumber(), buildingId);
                throw new IllegalArgumentException(
                        String.format("Floor number '%d' already exists in this building", dto.getFloorNumber())
                );
            }
            existingFloor.setFloorNumber(dto.getFloorNumber());
        }

        if (dto.getDescription() != null) {
            log.info("Обновление описания");
            existingFloor.setDescription(dto.getDescription());
        }

        if (dto.getBuildingId() != null) {
            log.info("Обновление здания: {} -> {}", existingFloor.getBuilding().getId(), dto.getBuildingId());

            Building building = buildingRepository.findById(dto.getBuildingId())
                    .orElseThrow(() -> {
                        log.error("Здание с ID {} не найдено", dto.getBuildingId());
                        return new EntityNotFoundException(
                                String.format("Building with id '%d' not found", dto.getBuildingId())
                        );
                    });
            existingFloor.setBuilding(building);
        }

        buildingFloorRepository.save(existingFloor);
        log.info("=== УСПЕШНО: Этаж обновлён ===");

        return buildingFloorMapping.toDto(existingFloor);
    }

    @Transactional
    public void deleteBuildingFloor(Long id) {
        log.info("=== НАЧАЛО: Удаление этажа ===");
        log.info("ID этажа: {}", id);

        if (!buildingFloorRepository.existsById(id)) {
            log.error("Этаж с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Building floor with id '%d' not found", id)
            );
        }

        buildingFloorRepository.deleteById(id);
        log.info("=== УСПЕШНО: Этаж удалён ===");
    }
}