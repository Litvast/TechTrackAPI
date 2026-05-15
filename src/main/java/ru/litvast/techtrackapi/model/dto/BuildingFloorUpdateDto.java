package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingFloorUpdateDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @Positive(message = "Floor number cannot be negative or zero")
    private Integer floorNumber;

    @Size(message = "Description cannot be longer than 500 characters", max = 500)
    private String description;

    @Positive(message = "Building ID cannot be negative or zero")
    private Long buildingId;
}