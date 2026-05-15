package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomEquipmentDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Positive(message = "Room ID cannot be negative or zero")
    private Long roomId;

    private String roomName;

    @Positive(message = "Equipment ID cannot be negative or zero")
    private Long equipmentId;

    private String equipmentName;
    private String equipmentType;
}