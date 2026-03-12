package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class RefreshTokenDto {

    @NotBlank(message = "Refresh token is required")
    String token;
}
