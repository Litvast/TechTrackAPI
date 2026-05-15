package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageDeviceDto {

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

    @Positive(message = "Capacity cannot be negative or zero")
    private Integer capacityGb;

    @Size(message = "Connection interface cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String portType;

    @Size(message = "Connection interface cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String connectionInterface;

    @Size(message = "Connection interface cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String formFactor;

    @Positive(message = "Read speed cannot be negative or zero")
    private Integer readSpeedMbps;

    @Positive(message = "Write speed cannot be negative or zero")
    private Integer writeSpeedMbps;

    // SSD
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
         message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String nandType;

    @Positive(message = "TBW cannot be negative or zero")
    private Integer tbw;

    // HDD
    @Positive(message = "RPM cannot be negative or zero")
    private Integer rpm;

    @Positive(message = "Height cannot be negative or zero")
    private Double heightMm;

    @AssertTrue(message = "Specify the parameters that are specific to only one type of storage device (SSD or HDD)")
    private boolean isDriveMatchesSameType() {
        return (nandType == null && tbw == null) || (rpm == null && heightMm == null);
    }

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidStorageDevice() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNameStorageDeviceNotBlank() {
        if (name == null) return true;

        return !name.isBlank();
    }
}