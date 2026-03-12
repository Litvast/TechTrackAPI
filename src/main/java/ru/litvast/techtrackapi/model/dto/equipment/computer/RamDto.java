package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamFormFactor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RamDto {

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String name;

    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String manufacturer;

    private RamType type;
    private RamFormFactor formFactor;

    @NegativeOrZero(message = "Capacity cannot be negative or zero")
    private Integer capacityMb;

    @NegativeOrZero(message = "Frequency cannot be negative or zero")
    private Integer frequencyMHz;

    @Pattern(regexp = "^[0-9]{1,2}-[0-9]{1,2}-[0-9]{1,2}-[0-9]{1,2}",
            message = "Timings must be in format: XX-XX-XX-XX (each part 1-2 digits)")
    private String timings;

    @NegativeOrZero(message = "Voltage cannot be negative or zero")
    private Double voltage;

    private Boolean ecc;
    private Boolean registered;
    private Boolean xmpSupport;
    private Boolean expoSupport;
    private Boolean dualRank;
    private Boolean onDieEcc;
}
