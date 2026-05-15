package ru.litvast.techtrackapi.model.dto.mapping.equipment;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.PrinterDto;
import ru.litvast.techtrackapi.model.dto.equipment.PrinterUpdateDto;
import ru.litvast.techtrackapi.model.entity.equipment.Printer;

@Mapper(componentModel = "spring")
public interface PrinterMapping {
    PrinterDto toDto(Printer printer);
    Printer toEntity(PrinterDto dto);
    Printer toEntity(PrinterUpdateDto dto);
}