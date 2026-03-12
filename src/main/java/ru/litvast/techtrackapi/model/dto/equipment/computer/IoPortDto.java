package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IoPortDto {

    @Size(message = "Type cannot be longer than 20 characters", max = 20)
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String type;

    @Size(message = "Version cannot be longer than 20 characters", max = 20)
    @NotBlank(message = "Version is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String version;

    @NotNull(message = "Count is required")
    @NegativeOrZero(message = "Count cannot be negative or zero")
    private Integer count;
}
