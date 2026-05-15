package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.litvast.techtrackapi.model.dto.BuildingDto;
import ru.litvast.techtrackapi.model.dto.BuildingUpdateDto;
import ru.litvast.techtrackapi.model.entity.Building;

@Mapper(componentModel = "spring")
public interface BuildingMapping {

    @Mapping(source = "company.id", target = "companyId")
    BuildingDto toDto(Building building);

    Building toEntity(BuildingDto dto);
    Building toEntity(BuildingUpdateDto dto);
}