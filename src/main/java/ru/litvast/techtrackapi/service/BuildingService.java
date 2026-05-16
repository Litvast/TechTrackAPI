package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.BuildingDto;
import ru.litvast.techtrackapi.model.dto.BuildingUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.BuildingMapping;
import ru.litvast.techtrackapi.model.entity.Building;
import ru.litvast.techtrackapi.model.entity.Company;
import ru.litvast.techtrackapi.repository.BuildingRepository;
import ru.litvast.techtrackapi.repository.CompanyRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final CompanyRepository companyRepository;
    private final BuildingMapping buildingMapping;

    @Transactional
    public BuildingDto addBuilding(BuildingDto dto) {
        log.info("=== НАЧАЛО: Добавление здания ===");
        log.info("Название: {}, Компания ID: {}", dto.getName(), dto.getCompanyId());

        if (dto.getId() != null) {
            log.error("Передан ID при создании здания. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a building, you must specify a name, not an ID");
        }

        if (buildingRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Здание с названием '{}' уже существует", dto.getName());
            throw new IllegalArgumentException(
                    String.format("Building '%s' is already taken", dto.getName())
            );
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> {
                    log.error("Компания с ID {} не найдена", dto.getCompanyId());
                    return new EntityNotFoundException(
                            String.format("Company with id '%d' not found", dto.getCompanyId())
                    );
                });

        log.info("Компания найдена: {}", company.getName());

        Building building = buildingMapping.toEntity(dto);
        building.setCompany(company);
        buildingRepository.save(building);

        log.info("Здание создано. ID: {}", building.getId());
        log.info("=== УСПЕШНО: Здание добавлено ===");

        return buildingMapping.toDto(building);
    }

    public Page<BuildingDto> getAllBuildings(Pageable pageable) {
        log.debug("Запрос всех зданий с пагинацией");

        Page<Building> buildings = buildingRepository.findAll(pageable);
        if (buildings.isEmpty()) {
            log.warn("Здания не найдены");
            throw new NoEntitiesFoundException("No buildings found");
        }

        log.debug("Найдено {} зданий", buildings.getTotalElements());
        return buildings.map(buildingMapping::toDto);
    }

    public Page<BuildingDto> getBuildingsByCompanyId(Long companyId, Pageable pageable) {
        log.debug("Поиск зданий по компании ID: {}", companyId);

        if (!companyRepository.existsById(companyId)) {
            log.error("Компания с ID {} не найдена", companyId);
            throw new EntityNotFoundException(
                    String.format("Company with id '%d' not found", companyId)
            );
        }

        Page<Building> buildings = buildingRepository.findByCompanyId(companyId, pageable);
        if (buildings.isEmpty()) {
            log.warn("Здания для компании ID {} не найдены", companyId);
            throw new NoEntitiesFoundException("No buildings found for this company");
        }

        log.debug("Найдено {} зданий для компании ID {}", buildings.getTotalElements(), companyId);
        return buildings.map(buildingMapping::toDto);
    }

    public BuildingDto getBuildingById(Long id) {
        log.debug("Поиск здания по ID: {}", id);

        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Здание с ID {} не найдено", id);
                    return new EntityNotFoundException(
                            String.format("Building with id '%d' not found", id)
                    );
                });

        return buildingMapping.toDto(building);
    }

    public BuildingDto getBuildingByName(String name) {
        log.debug("Поиск здания по названию: {}", name);

        Building building = buildingRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Здание с названием '{}' не найдено", name);
                    return new EntityNotFoundException(
                            String.format("Building with name '%s' not found", name)
                    );
                });

        return buildingMapping.toDto(building);
    }

    @Transactional
    public BuildingDto updateBuilding(Long id, BuildingUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление здания ===");
        log.info("ID здания: {}", id);

        Building existingBuilding = buildingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Здание с ID {} не найдено", id);
                    return new EntityNotFoundException(
                            String.format("Building with id '%d' not found", id)
                    );
                });

        if (dto.getName() != null && !existingBuilding.getName().equalsIgnoreCase(dto.getName())) {
            log.info("Изменение названия: {} -> {}", existingBuilding.getName(), dto.getName());

            if (buildingRepository.existsByNameIgnoreCase(dto.getName())) {
                log.warn("Здание с названием '{}' уже существует", dto.getName());
                throw new IllegalArgumentException(
                        String.format("Building '%s' is already taken", dto.getName())
                );
            }
            existingBuilding.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            log.info("Обновление описания");
            existingBuilding.setDescription(dto.getDescription());
        }

        if (dto.getAddress() != null) {
            log.info("Обновление адреса: {}", dto.getAddress());
            existingBuilding.setAddress(dto.getAddress());
        }

        if (dto.getCompanyId() != null) {
            log.info("Обновление компании: {} -> {}",
                    existingBuilding.getCompany().getId(), dto.getCompanyId());

            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> {
                        log.error("Компания с ID {} не найдена", dto.getCompanyId());
                        return new EntityNotFoundException(
                                String.format("Company with id '%d' not found", dto.getCompanyId())
                        );
                    });
            existingBuilding.setCompany(company);
        }

        buildingRepository.save(existingBuilding);
        log.info("=== УСПЕШНО: Здание обновлено ===");

        return buildingMapping.toDto(existingBuilding);
    }

    @Transactional
    public void deleteBuilding(Long id) {
        log.info("=== НАЧАЛО: Удаление здания ===");
        log.info("ID здания: {}", id);

        if (!buildingRepository.existsById(id)) {
            log.error("Здание с ID {} не найдено", id);
            throw new EntityNotFoundException(
                    String.format("Building with id '%d' not found", id)
            );
        }

        buildingRepository.deleteById(id);
        log.info("=== УСПЕШНО: Здание удалено ===");
    }
}