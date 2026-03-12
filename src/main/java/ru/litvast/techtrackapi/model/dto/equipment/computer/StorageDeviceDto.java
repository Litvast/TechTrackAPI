package ru.litvast.techtrackapi.model.dto.equipment.computer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageDeviceDto {

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String name;

    @Size(message = "Manufacturer cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String manufacturer;

    @NegativeOrZero(message = "Capacity cannot be negative or zero")
    Integer capacityGb;

    @Size(message = "Connection interface cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String connectionInterface;

    @Size(message = "Connection interface cannot be longer than 10 characters", max = 10)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String formFactor;

    @NegativeOrZero(message = "Read speed cannot be negative or zero")
    Integer readSpeedMbps;

    @NegativeOrZero(message = "Write speed cannot be negative or zero")
    Integer writeSpeedMbps;

    // SSD
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.-/]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, slashes and hyphens are allowed")
    String nandType;

    @NegativeOrZero(message = "TBW cannot be negative or zero")
    Integer tbw;

    // HDD
    @NegativeOrZero(message = "RPM cannot be negative or zero")
    Integer rpm;

    @NegativeOrZero(message = "Height cannot be negative or zero")
    Double heightMm;

    @AssertTrue(message = "Specify the parameters that are specific to only one type of storage device (SSD or HDD)")
    private boolean checkDriveMatchesSameType() {
        return (nandType == null && tbw == null) || (rpm == null && heightMm == null);
    }
}