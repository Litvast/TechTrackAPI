package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamFormFactor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemorySupportDto {

    private RamType type;
    private RamFormFactor formFactor;

    @NotNull(message = "Number of slots is required")
    @Positive(message = "Bit width cannot be negative or zero")
    private Integer numberOfSlots;

    @Positive(message = "Max memory cannot be negative or zero")
    private Integer maxMemoryGb;

    @Positive(message = "Max frequency cannot be negative or zero")
    private Integer maxFrequencyMHz;

    @Positive(message = "Max memory channels cannot be negative or zero")
    private Integer maxMemoryChannels;

    private Boolean eccSupported;
    private Boolean nonEccSupported;
}
