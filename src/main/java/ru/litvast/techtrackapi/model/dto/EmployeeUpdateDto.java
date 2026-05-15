package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    @Size(max = 255, message = "Full name cannot be longer than 255 characters")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String fullName;

    @Size(max = 255, message = "Position cannot be longer than 255 characters")
    private String position;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;

    @Positive(message = "ID cannot be negative or zero")
    private Long userId;

    @Positive(message = "ID cannot be negative or zero")
    private Long roomId;

    @Positive(message = "ID cannot be negative or zero")
    private Long assignedComputerId;

    @Positive(message = "ID cannot be negative or zero")
    private Long assignedPrinterId;
}