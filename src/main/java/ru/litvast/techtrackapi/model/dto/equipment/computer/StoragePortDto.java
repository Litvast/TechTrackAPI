package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.*;
import lombok.Value;

@Value
public class StoragePortDto {

    @NotBlank(message = "Port type is required")
    @Size(message = "Port type cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String portType;

    @Size(message = "Version cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String version;

    @Size(message = "Form factor cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String formFactor;

    @NotNull(message = "Count is required")
    @NegativeOrZero(message = "Count cannot be negative or zero")
    Integer count;

    @Size(message = "Connection interface cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String connectionInterface;

    @NegativeOrZero(message = "Lanes cannot be negative or zero")
    Integer lanes;

    @Size(message = "Max speed cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String maxSpeed;

    Boolean shared;

}
