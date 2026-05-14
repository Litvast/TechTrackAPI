package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotherboardFormFactorDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @Size(message = "Code cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String code;

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String name;

    @Positive(message = "Width cannot be negative or zero")
    private Integer widthMm;

    @Positive(message = "Height cannot be negative or zero")
    private Integer heightMm;

    @AssertTrue(message = "Either provide ID (to reference existing) OR name and code (to create new)")
    private boolean isValidMotherboard() {
        return (id == null) != (name == null && code == null);
    }

    @AssertTrue(message = "The name and code must be filled in")
    private boolean isNameAndCodeMotherboardFormFactorNotBlank() {
        if (name == null || code == null) return true;

        return !name.isBlank() && !code.isBlank();
    }
}
