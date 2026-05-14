package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuSocketDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuSocket;

@Mapper(componentModel = "spring")
public interface CpuSocketMapping {
    CpuSocket toEntity(CpuSocketDto dto);
    CpuSocketDto toDto(CpuSocket entity);
}