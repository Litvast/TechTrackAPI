package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
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

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String name;

    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String manufacturer;

    private RamType type;
    private RamFormFactor formFactor;

    @Positive(message = "Capacity cannot be negative or zero")
    private Integer capacityMb;

    @Positive(message = "Frequency cannot be negative or zero")
    private Integer frequencyMHz;

    @Pattern(regexp = "^[0-9]{1,2}-[0-9]{1,2}-[0-9]{1,2}-[0-9]{1,2}",
            message = "Timings must be in format: XX-XX-XX-XX (each part 1-2 digits)")
    private String timings;

    @Positive(message = "Voltage cannot be negative or zero")
    private Double voltage;

    private Boolean ecc;
    private Boolean registered;
    private Boolean xmpSupport;
    private Boolean expoSupport;
    private Boolean dualRank;
    private Boolean onDieEcc;

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidRam() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNameRamNotBlank() {
        if (name == null) return true;

        return !name.isBlank();
    }
}
