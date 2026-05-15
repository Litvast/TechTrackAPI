package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(message = "Full name cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String fullName;

    @Size(message = "Position cannot be longer than 255 characters", max = 255)
    private String position;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;

    @Positive(message = "User ID cannot be negative or zero")
    private Long userId;

    @Positive(message = "Room ID cannot be negative or zero")
    private Long roomId;

    @Positive(message = "Computer ID cannot be negative or zero")
    private Long assignedComputerId;

    @Positive(message = "Printer ID cannot be negative or zero")
    private Long assignedPrinterId;
}