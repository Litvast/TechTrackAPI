package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessorDto {

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

    @Positive(message = "Clock frequency cannot be negative or zero")
    private Double clockFrequencyGHz;

    @Positive(message = "Turbo clock frequency cannot be negative or zero")
    private Double turboClockFrequencyGHz;

    @Positive(message = "Number of cores cannot be negative or zero")
    private Integer numberOfCores;

    @Positive(message = "Number of threads cannot be negative or zero")
    private Integer numberOfThreads;

    @Positive(message = "L1 cache cannot be negative or zero")
    private Integer l1CacheKB;

    @Positive(message = "L2 cache cannot be negative or zero")
    private Integer l2CacheKB;

    @Positive(message = "L3 cache cannot be negative or zero")
    private Integer l3CacheMB;

    @Positive(message = "TDP cannot be negative or zero")
    private Integer tdpWatts;

    @Positive(message = "Lithography cannot be negative or zero")
    private Integer lithographyNm;

    @Schema(description = "CPU architecture details")
    private CpuArchitectureDto architecture;

    @Schema(description = "CPU socket details")
    private CpuSocketDto socket;

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

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidProcessor() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNameProcessorNotBlank() {
        if (name == null) return true;

        return !name.isBlank();
    }
}