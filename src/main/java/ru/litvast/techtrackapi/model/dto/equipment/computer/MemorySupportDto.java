package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamFormFactor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamType;

@Value
public class MemorySupportDto {

    RamType type;
    RamFormFactor formFactor;

    @NotNull(message = "Number of slots is required")
    @NegativeOrZero(message = "Bit width cannot be negative or zero")
    Integer numberOfSlots;

    @NegativeOrZero(message = "Max memory cannot be negative or zero")
    Integer maxMemoryGb;

    @NegativeOrZero(message = "Max frequency cannot be negative or zero")
    Integer maxFrequencyMHz;

    @NegativeOrZero(message = "Max memory channels cannot be negative or zero")
    Integer maxMemoryChannels;

    Boolean eccSupported;
    Boolean nonEccSupported;
}
