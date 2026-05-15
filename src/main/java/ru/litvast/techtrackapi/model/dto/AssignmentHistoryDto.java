package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentHistoryDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @NotNull(message = "Equipment ID is required")
    @Positive(message = "Equipment ID cannot be negative or zero")
    private Long equipmentId;

    private String equipmentName;
    private String equipmentType;

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID cannot be negative or zero")
    private Long employeeId;

    private String employeeName;

    private LocalDateTime assignedAt;
    private LocalDateTime returnedAt;
    private String condition;
}