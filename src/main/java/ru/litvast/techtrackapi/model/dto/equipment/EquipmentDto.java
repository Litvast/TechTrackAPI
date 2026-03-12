package ru.litvast.techtrackapi.model.dto.equipment;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public abstract class EquipmentDto {

    private String type;

    @Size(message = "Name cannot be longer than 10 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String name;

    @AssertTrue(message = "The type must be \"COMPUTER\" or \"PRINTER\" or \"ROUTER\"")
    private Boolean isTypeValid() {
        return type.equals("COMPUTER") || type.equals("PRINTER") || type.equals("ROUTER");
    }
}
