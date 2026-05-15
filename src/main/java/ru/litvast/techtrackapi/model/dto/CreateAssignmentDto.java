package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentDto {

    @NotNull(message = "Equipment ID is required")
    @Positive(message = "Equipment ID cannot be negative or zero")
    private Long equipmentId;

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID cannot be negative or zero")
    private Long employeeId;

    private String condition;
}