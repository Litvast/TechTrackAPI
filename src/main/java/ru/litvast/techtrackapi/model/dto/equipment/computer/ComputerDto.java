package ru.litvast.techtrackapi.model.dto.equipment.computer;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.litvast.techtrackapi.model.dto.equipment.EquipmentDto;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class ComputerDto extends EquipmentDto {
    ProcessorDto processor;
    MotherboardDto motherboard;
    List<RamDto> rams;
    VideoCardDto videoCard;
    List<StorageDeviceDto> storageDevice;
    PowerSupplyDto powerSupply;
}
