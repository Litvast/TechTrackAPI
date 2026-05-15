package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.litvast.techtrackapi.model.dto.BuildingFloorDto;
import ru.litvast.techtrackapi.model.dto.BuildingFloorUpdateDto;
import ru.litvast.techtrackapi.model.entity.BuildingFloor;

@Mapper(componentModel = "spring")
public interface BuildingFloorMapping {

    @Mapping(source = "building.id", target = "buildingId")
    BuildingFloorDto toDto(BuildingFloor buildingFloor);

    BuildingFloor toEntity(BuildingFloorDto dto);
    BuildingFloor toEntity(BuildingFloorUpdateDto dto);
}