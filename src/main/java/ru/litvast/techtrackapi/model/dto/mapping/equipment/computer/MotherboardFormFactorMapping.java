package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.MotherboardFormFactorDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.MotherboardFormFactor;

@Mapper(componentModel = "spring")
public interface MotherboardFormFactorMapping {
    MotherboardFormFactorDto toDto(MotherboardFormFactor formFactor);
    MotherboardFormFactor toEntity(MotherboardFormFactorDto formFactorDto);
}