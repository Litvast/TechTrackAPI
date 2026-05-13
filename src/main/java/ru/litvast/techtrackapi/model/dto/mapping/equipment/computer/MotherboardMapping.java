package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.MotherboardDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Motherboard;

@Mapper(componentModel = "spring")
public interface MotherboardMapping {
    MotherboardDto toDto(Motherboard motherboard);
    Motherboard toEntity(MotherboardDto motherboardDto);
}
