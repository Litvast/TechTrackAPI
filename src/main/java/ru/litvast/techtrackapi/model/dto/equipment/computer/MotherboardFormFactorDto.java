package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class MotherboardFormFactorDto {

    @Size(message = "Code cannot be longer than 10 characters", max = 10)
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String code;

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String name;

    @NegativeOrZero(message = "Width cannot be negative or zero")
    Integer widthMm;

    @NegativeOrZero(message = "Height cannot be negative or zero")
    Integer heightMm;
}
