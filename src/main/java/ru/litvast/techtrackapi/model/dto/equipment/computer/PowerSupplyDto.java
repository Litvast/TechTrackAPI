package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PsuEfficiency;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PsuFormFactor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PsuModular;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerSupplyDto {

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

    @Positive(message = "Power cannot be negative or zero")
    private Integer powerWatts;

    private PsuEfficiency efficiency;
    private PsuFormFactor formFactor;
    private PsuModular modular;

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidPowerSupply() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNamePowerSupplyNotBlank() {
        if (name == null) return true;

        return !name.isBlank();
    }
}
