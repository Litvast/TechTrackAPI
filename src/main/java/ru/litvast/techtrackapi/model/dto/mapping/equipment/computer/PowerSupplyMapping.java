package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.PowerSupplyDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PowerSupply;

@Mapper(componentModel = "spring")
public interface PowerSupplyMapping {
    PowerSupplyDto toDto(PowerSupply powerSupply);
    PowerSupply toEntity(PowerSupplyDto powerSupplyDto);
}
