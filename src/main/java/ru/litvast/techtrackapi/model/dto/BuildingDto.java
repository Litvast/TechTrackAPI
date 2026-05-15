package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @NotBlank(message = "Building name is required")
    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String name;

    @Size(message = "Description cannot be longer than 1000 characters", max = 1000)
    private String description;

    @Size(message = "Address cannot be longer than 500 characters", max = 500)
    private String address;

    @Positive(message = "Company ID cannot be negative or zero")
    private Long companyId;
}