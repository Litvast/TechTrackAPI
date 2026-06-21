package ru.litvast.techtrackapi.model.dto.equipment;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.litvast.techtrackapi.model.entity.equipment.EquipmentStatus;

public abstract class EquipmentUpdateDto {

    @Getter
    @Setter
    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    private String type;

    @Getter
    @Setter
    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String name;

    @Getter
    @Setter
    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String manufacturer;

    @Getter
    @Setter
    @Size(message = "Inventory number cannot be longer than 50 characters", max = 50)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String inventoryNumber;

    @AssertTrue(message = "The type must be \"COMPUTER\" or \"PRINTER\" or \"ROUTER\"")
    private Boolean isTypeValid() {
        return "COMPUTER".equals(type) || "PRINTER".equals(type) || "ROUTER".equals(type);
    }

    @Getter
    @Setter
    private EquipmentStatus status;
}