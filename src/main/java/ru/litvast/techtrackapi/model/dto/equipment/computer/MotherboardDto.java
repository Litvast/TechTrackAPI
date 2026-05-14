package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MotherboardDto {

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

    @Size(message = "Chipset cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String chipset;

    private List<MemorySupportDto> memorySupports;
    private List<StoragePortDto> storagePorts;
    private List<IoPortDto> ioPorts;

    private MotherboardFormFactorDto formFactor;
    private CpuSocketDto socket;

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidMotherboard() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNameMotherboardNotBlank() {
        if (name == null) return true;

        return !name.isBlank();
    }
}