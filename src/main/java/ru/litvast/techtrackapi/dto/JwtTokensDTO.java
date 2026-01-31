package ru.litvast.techtrackapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtTokensDTO {
    private String accessToken;
    private String refreshToken;
}
