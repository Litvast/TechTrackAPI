package ru.litvast.techtrackapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.litvast.techtrackapi.model.Role;

@Data
public class UserCreateDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    @Pattern(regexp = "^[A-Za-z0-9_]+$",
            message = "A nickname can only contain uppercase, lowercase letters, and numbers")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,
            message = "The password must be at least 8 characters and no more than 255 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$",
            message = "The password must contain at least one capital and lowercase letter, as well as a number")
    private String password;

    private Role role;
}
