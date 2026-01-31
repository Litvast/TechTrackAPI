package ru.litvast.techtrackapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.dto.JwtTokensDTO;
import ru.litvast.techtrackapi.dto.RefreshTokenDTO;
import ru.litvast.techtrackapi.dto.UserCredentialsDTO;
import ru.litvast.techtrackapi.repository.UserRepository;
import ru.litvast.techtrackapi.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserCredentialsDTO user) {
        try {
            userService.signup(user);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok("Successfully");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserCredentialsDTO user) {
        try {
            JwtTokensDTO tokens = userService.signin(user);
            return ResponseEntity.ok(tokens);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            JwtTokensDTO tokens = userService.refresh(refreshTokenDTO);
            return ResponseEntity.ok(tokens);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
