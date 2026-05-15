package ru.litvast.techtrackapi.model.dto.equipment;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.PrintType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrinterDto {

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

    @Size(message = "Inventory number cannot be longer than 50 characters", max = 50)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String inventoryNumber;

    private PrintType printType;

    private Boolean isColor;

    @Positive(message = "Speed cannot be negative or zero")
    private Double speed;

    @Positive(message = "Width resolution cannot be negative or zero")
    private Integer widthResolution;

    @Positive(message = "Height resolution cannot be negative or zero")
    private Integer heightResolution;

    @Size(message = "Consumption cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String consumption;

    @Size(message = "Model cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String model;

    private Boolean isDuplex;
    private Boolean isNetwork;

    @Size(message = "Paper size cannot be longer than 20 characters", max = 20)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String paperSize;

    @AssertTrue(message = "Either provide ID (to reference existing) OR name (to create new)")
    private boolean isValidPrinter() {
        return (id == null) != (name == null);
    }

    @AssertTrue(message = "The name must be filled in")
    private boolean isNamePrinterNotBlank() {
        if (name == null) return true;
        return !name.isBlank();
    }
}