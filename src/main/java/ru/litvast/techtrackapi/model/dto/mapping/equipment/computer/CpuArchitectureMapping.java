package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuArchitectureDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuArchitecture;

@Mapper(componentModel = "spring")
public interface CpuArchitectureMapping {
    CpuArchitecture toEntity(CpuArchitectureDto dto);
    CpuArchitectureDto toDto(CpuArchitecture entity);
}