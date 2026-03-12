package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import ru.litvast.techtrackapi.model.entity.equipment.computer.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MotherboardDto {

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String name;

    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String manufacturer;

    @Size(message = "Chipset cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    private String chipset;

    private List<MemorySupportDto> memorySupports;
    private List<StoragePortDto> storagePorts;
    private List<IoPortDto> ioPorts;

    private MotherboardFormFactorDto formFactor;
    private CpuSocketDto socket;
}