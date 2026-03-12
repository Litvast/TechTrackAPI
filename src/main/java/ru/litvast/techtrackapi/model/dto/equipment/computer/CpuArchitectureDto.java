package ru.litvast.techtrackapi.model.dto.equipment.computer;

import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpuArchitectureDto {

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String name;

    @NegativeOrZero(message = "Bit width cannot be negative or zero")
    private Integer bitWidth;
}
