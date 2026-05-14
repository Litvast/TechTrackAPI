package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.GpuMemoryType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoCardDto {

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

    @Size(message = "Architecture cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String architecture;

    @Positive(message = "Clock frequency cannot be negative or zero")
    private Integer clockFrequencyMHz;

    @Positive(message = "Turbo clock frequency cannot be negative or zero")
    private Integer turboClockFrequencyMHz;

    @Positive(message = "Lithography cannot be negative or zero")
    private Integer lithographyNm;

    @Positive(message = "Number of ALUs cannot be negative or zero")
    private Integer numberOfAlus;

    @Positive(message = "Number of TPUs cannot be negative or zero")
    private Integer numberOfTmus;

    @Positive(message = "Number of ROPs cannot be negative or zero")
    private Integer numberOfRops;

    private GpuMemoryType vramType;

    @Positive(message = "VRAM capacity cannot be negative or zero")
    private Integer vramCapacityMb;

    @Positive(message = "VRAM frequency cannot be negative or zero")
    private Integer vramFrequencyMHz;

    @Positive(message = "VRAM bus cannot be negative or zero")
    private Integer vramBusBit;

    @Positive(message = "TDP cannot be negative or zero")
    private Integer tdpWatts;

    @Pattern(regexp = "^[0-9]\\.[0-9]$",
            message = "PCIe version must be in format X.Y (e.g., 3.0, 4.0, 5.0)")
    private String pcieVersion;

    @AssertTrue(message = "If the turbo clock frequency is filled, then the clock frequency must also be filled")
    private boolean isClockPresentWhenTurboClockPresent() {
        return clockFrequencyMHz != null || turboClockFrequencyMHz == null;
    }

    @AssertTrue(message = "The turbo clock frequency ({turboClockFrequencyMHz} MHz) must be >= the clock frequency ({clockFrequencyMHz} MHz)")
    private boolean isTurboClockFrequencyValid() {
        if (clockFrequencyMHz == null || turboClockFrequencyMHz == null) return true;
        return turboClockFrequencyMHz >= clockFrequencyMHz;
    }

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidVideoCard() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNameVideoCardNotBlank() {
        if (name == null) return true;

        return !name.isBlank();
    }
}