package ru.litvast.techtrackapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.dto.*;
import ru.litvast.techtrackapi.exception.NoUsersFoundException;
import ru.litvast.techtrackapi.exception.UserNotFoundException;
import ru.litvast.techtrackapi.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    @Tag(name = "auth", description = "Методы для регистрации и аутентификации в системе")
    @Operation(
            summary = "Регистрация пользователя",
            description = "В ответ выдаётся сообщение о успешной регистрации."
    )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserCredentialsDTO user) {
        try {
            userService.signup(user);
            return ResponseEntity.ok("Successfully");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration failed");
        }
    }

    @Tag(name = "auth", description = "Методы для регистрации и аутентификации в системе")
    @Operation(
            summary = "Авторизация пользователя",
            description = "В ответ выдаётся json-объект с двумя JWT-токенами: accept (доступа) и refresh (обновления)."
    )
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody UserCredentialsDTO user) {
        try {
            JwtTokensDTO tokens = userService.signin(user);
            return ResponseEntity.ok(tokens);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Tag(name = "auth", description = "Методы для регистрации и аутентификации в системе")
    @Operation(
            summary = "Обновление accept (доступа) токена",
            description = "В ответ выдаётся json-объект с двумя JWT-токенами: accept (доступа) и refresh (обновления, который передал пользователь).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/refreshUserToken")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            JwtTokensDTO tokens = userService.refreshUserToken(refreshTokenDTO);
            return ResponseEntity.ok(tokens);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(
            summary = "Добавление пользователя в систему (ADMIN)",
            description = "В ответ выдаётся объект User-а с полями id, username и role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        try {
            UserNoPasswordDTO userNoPasswordDTO = userService.addUser(userCreateDTO);
            return ResponseEntity.ok(userNoPasswordDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(
            summary = "Изменение данных пользователя по айди (ADMIN)",
            description = "В ответ выдаётся обновлённый объект User-а с полями id, username и role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id,
                                        @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            UserNoPasswordDTO userNoPasswordDTO = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(userNoPasswordDTO);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(
            summary = "Поиск пользователя по айди",
            description = "В ответ выдаётся найденный объект User-а с полями id, username и role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") String id) {
        try {
            UserNoPasswordDTO userNoPasswordDTO = userService.getUserById(id);
            return ResponseEntity.ok(userNoPasswordDTO);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(
            summary = "Вывод списка всех пользователей системы",
            description = "В ответ выдаётся список объектов User-а с полями id, username и role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (NoUsersFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(
            summary = "Удаление пользователя по айди (ADMIN)",
            description = "В ответ выдаётся сообщение о успешном удалении пользователя.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Successfully");
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
