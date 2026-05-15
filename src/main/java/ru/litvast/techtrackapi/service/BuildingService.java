package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final CompanyRepository companyRepository;
    private final BuildingMapping buildingMapping;

    @Transactional
    public BuildingDto addBuilding(BuildingDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a building, you must specify a name, not an ID");
        }

        if (buildingRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Building '%s' is already taken", dto.getName())
            );
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Company with id '%d' not found", dto.getCompanyId())
                ));

        Building building = buildingMapping.toEntity(dto);
        building.setCompany(company);
        buildingRepository.save(building);
        return buildingMapping.toDto(building);
    }

    public Page<BuildingDto> getAllBuildings(Pageable pageable) {
        Page<Building> buildings = buildingRepository.findAll(pageable);
        if (buildings.isEmpty()) {
            throw new NoEntitiesFoundException("No buildings found");
        }
        return buildings.map(buildingMapping::toDto);
    }

    public Page<BuildingDto> getBuildingsByCompanyId(Long companyId, Pageable pageable) {
        if (!companyRepository.existsById(companyId)) {
            throw new EntityNotFoundException(
                    String.format("Company with id '%d' not found", companyId)
            );
        }

        Page<Building> buildings = buildingRepository.findByCompanyId(companyId, pageable);
        if (buildings.isEmpty()) {
            throw new NoEntitiesFoundException("No buildings found for this company");
        }
        return buildings.map(buildingMapping::toDto);
    }

    public BuildingDto getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building with id '%d' not found", id)
                ));
        return buildingMapping.toDto(building);
    }

    public BuildingDto getBuildingByName(String name) {
        Building building = buildingRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building with name '%s' not found", name)
                ));
        return buildingMapping.toDto(building);
    }

    @Transactional
    public BuildingDto updateBuilding(Long id, BuildingUpdateDto dto) {
        Building existingBuilding = buildingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building with id '%d' not found", id)
                ));

        if (dto.getName() != null && !existingBuilding.getName().equalsIgnoreCase(dto.getName())) {
            if (buildingRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Building '%s' is already taken", dto.getName())
                );
            }
            existingBuilding.setName(dto.getName());
        }

        if (dto.getDescription() != null) existingBuilding.setDescription(dto.getDescription());
        if (dto.getAddress() != null) existingBuilding.setAddress(dto.getAddress());

        // Проверка существования компании (если изменилась)
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Company with id '%d' not found", dto.getCompanyId())
                    ));
            existingBuilding.setCompany(company);
        }

        buildingRepository.save(existingBuilding);
        return buildingMapping.toDto(existingBuilding);
    }

    @Transactional
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Building with id '%d' not found", id)
            );
        }
        buildingRepository.deleteById(id);
    }
}