package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnEquipmentDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    private String condition;
}