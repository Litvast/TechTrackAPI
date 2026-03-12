package ru.litvast.techtrackapi.model.dto;

import lombok.Value;

@Value
public class JwtTokensDto {
    String accessToken;
    String refreshToken;
}
