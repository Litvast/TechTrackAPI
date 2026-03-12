package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Value;
import ru.litvast.techtrackapi.model.entity.equipment.computer.GpuMemoryType;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoCardDto {

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String name;

    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String manufacturer;

    @Size(message = "Architecture cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String architecture;

    @NegativeOrZero(message = "Clock frequency cannot be negative or zero")
    Integer clockFrequencyMHz;

    @NegativeOrZero(message = "Turbo clock frequency cannot be negative or zero")
    Integer turboClockFrequencyMHz;

    @NegativeOrZero(message = "Lithography cannot be negative or zero")
    Integer lithographyNm;

    @NegativeOrZero(message = "Number of ALUs cannot be negative or zero")
    Integer numberOfAlus;

    @NegativeOrZero(message = "Number of TPUs cannot be negative or zero")
    Integer numberOfTmus;

    @NegativeOrZero(message = "Number of ROPs cannot be negative or zero")
    Integer numberOfRops;

    GpuMemoryType vramType;

    @NegativeOrZero(message = "VRAM capacity cannot be negative or zero")
    Integer vramCapacityMb;

    @NegativeOrZero(message = "VRAM frequency cannot be negative or zero")
    Integer vramFrequencyMHz;

    @NegativeOrZero(message = "VRAM bus cannot be negative or zero")
    Integer vramBusBit;

    @NegativeOrZero(message = "TDP cannot be negative or zero")
    Integer tdpWatts;

    @Pattern(regexp = "^[0-9]\\.[0-9]$",
            message = "PCIe version must be in format X.Y (e.g., 3.0, 4.0, 5.0)")
    String pcieVersion;

    @AssertTrue(message = "If the turbo clock frequency is filled, then the clock frequency must also be filled")
    private boolean isClockPresentWhenTurboClockPresent() {
        return clockFrequencyMHz != null || turboClockFrequencyMHz == null;
    }

    @AssertTrue(message = "The turbo clock frequency ({turboClockFrequencyMHz} MHz) must be >= the clock frequency ({clockFrequencyMHz} MHz)")
    private boolean isTurboClockFrequencyValid() {
        if (clockFrequencyMHz == null || turboClockFrequencyMHz == null) return true;
        return turboClockFrequencyMHz >= clockFrequencyMHz;
    }
}