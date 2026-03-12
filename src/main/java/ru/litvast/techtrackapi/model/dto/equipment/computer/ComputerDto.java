package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.litvast.techtrackapi.model.dto.equipment.EquipmentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ComputerDto extends EquipmentDto {

    @NotNull(message = "Processor is required")
    private ProcessorDto processor;

    @NotNull(message = "Motherboard is required")
    private MotherboardDto motherboard;

    @NotEmpty(message = "At least one RAM is required")
    private List<RamDto> rams;

    private VideoCardDto videoCard;

    @NotEmpty(message = "At least one storage device is required")
    private List<StorageDeviceDto> storageDevice;

    @NotNull(message = "Power supply is required")
    private PowerSupplyDto powerSupply;
}
