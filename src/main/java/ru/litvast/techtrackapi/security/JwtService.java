package ru.litvast.techtrackapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.litvast.techtrackapi.dto.JwtTokensDTO;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {

    @Value("0TaFz9KtlBGRxcP2U8K1fqbFBVR2CyBbiG7J7NQJPd0EDmVaTvzBnTKOIxgGKgn71MpeaMKi4bqdXzHY5r5vXt")
    private String jwtSecret;

    public String generateAccessToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(date)
                .claim("token_type", "access")
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(date)
                .claim("token_type", "refreshUserToken")
                .signWith(getSigningKey())
                .compact();
    }

    public JwtTokensDTO generateJwtTokens(String username) {
        return new JwtTokensDTO(
                generateAccessToken(username),
                generateRefreshToken(username)
        );
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        Claims claims = getTokenClaims(token);
        return "access".equals(claims.get("token_type", String.class));
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getTokenClaims(token);
        return "refreshUserToken".equals(claims.get("token_type", String.class));
    }

    public String getUsernameFromJwtToken(String token) {
        return getTokenClaims(token).getSubject();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getTokenClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
