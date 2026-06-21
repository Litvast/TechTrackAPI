package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.Valid;
import lombok.*;
import ru.litvast.techtrackapi.model.dto.equipment.EquipmentDto;
import ru.litvast.techtrackapi.model.dto.equipment.EquipmentUpdateDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ComputerUpdateDto extends EquipmentUpdateDto {

    @Valid
    private ProcessorDto processor;

    @Valid
    private MotherboardDto motherboard;

    private List<@Valid RamDto> rams;

    @Valid
    private VideoCardDto videoCard;

    private List<@Valid StorageDeviceDto> storageDevices;

    @Valid
    private PowerSupplyDto powerSupply;
}
