package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomEquipmentUpdateDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Positive(message = "Room ID cannot be negative or zero")
    private Long roomId;

    @Positive(message = "Equipment ID cannot be negative or zero")
    private Long equipmentId;
}