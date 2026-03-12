package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Value;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuArchitecture;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuSocket;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessorDto {

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String name;

    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String manufacturer;

    @NegativeOrZero(message = "Clock frequency cannot be negative or zero")
    Double clockFrequencyGHz;

    @NegativeOrZero(message = "Turbo clock frequency cannot be negative or zero")
    Double turboClockFrequencyGHz;

    @NegativeOrZero(message = "Number of cores cannot be negative or zero")
    Integer numberOfCores;

    @NegativeOrZero(message = "Number of threads cannot be negative or zero")
    Integer numberOfThreads;

    @NegativeOrZero(message = "L1 cache cannot be negative or zero")
    Integer l1CacheKB;

    @NegativeOrZero(message = "L2 cache cannot be negative or zero")
    Integer l2CacheKB;

    @NegativeOrZero(message = "L3 cache cannot be negative or zero")
    Integer l3CacheMB;

    @NegativeOrZero(message = "TDP cannot be negative or zero")
    Integer tdpWatts;

    @NegativeOrZero(message = "Lithography cannot be negative or zero")
    Integer lithographyNm;

    CpuArchitectureDto architecture;
    CpuSocketDto socket;

    @AssertTrue(message = "If the number of threads is filled, then the number of cores must also be filled")
    private boolean isCoresPresentWhenThreadsPresent() {
        return numberOfCores != null || numberOfThreads == null;
    }

    @AssertTrue(message = "The number of threads ({numberOfThreads}) must be >= the number of cores ({numberOfCores})")
    private boolean isNumberOfThreadsValid() {
        if (numberOfCores == null || numberOfThreads == null) return true;
        return numberOfThreads >= numberOfCores;
    }

    @AssertTrue(message = "If the turbo clock frequency is filled, then the clock frequency must also be filled")
    private boolean isClockPresentWhenTurboClockPresent() {
        return clockFrequencyGHz != null || turboClockFrequencyGHz == null;
    }

    @AssertTrue(message = "The turbo clock frequency ({turboClockFrequencyGHz} GHz) must be >= the clock frequency ({clockFrequencyGHz} GHz)")
    private boolean isTurboClockValid() {
        if (clockFrequencyGHz == null || turboClockFrequencyGHz == null) return true;
        return turboClockFrequencyGHz >= clockFrequencyGHz;
    }
}