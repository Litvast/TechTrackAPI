package ru.litvast.techtrackapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.model.dto.JwtTokensDto;
import ru.litvast.techtrackapi.model.dto.RefreshTokenDto;
import ru.litvast.techtrackapi.model.dto.user.UserCreateDto;
import ru.litvast.techtrackapi.model.dto.user.UserCredentialsDto;
import ru.litvast.techtrackapi.model.dto.user.UserNoPasswordDto;
import ru.litvast.techtrackapi.model.dto.user.UserUpdateDto;
import ru.litvast.techtrackapi.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    @Tag(name = "auth", description = "Методы для регистрации и аутентификации в системе")
    @Operation(summary = "Регистрация пользователя",
            description = "В ответ выдаётся сообщение о успешной регистрации.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserCredentialsDto user) {
        userService.signup(user);
        return ResponseEntity.ok("Successfully");
    }

    @Tag(name = "auth", description = "Методы для регистрации и аутентификации в системе")
    @Operation(summary = "Авторизация пользователя",
            description = "В ответ выдаётся json-объект с двумя JWT-токенами: access и refresh.")
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody UserCredentialsDto user) {
        JwtTokensDto tokens = userService.signin(user);
        return ResponseEntity.ok(tokens);
    }

    @Tag(name = "auth", description = "Методы для регистрации и аутентификации в системе")
    @Operation(summary = "Обновление access токена",
            description = "В ответ выдаётся json-объект с двумя JWT-токенами.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/refreshUserToken")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDTO) {
        JwtTokensDto tokens = userService.refreshUserToken(refreshTokenDTO);
        return ResponseEntity.ok(tokens);
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(summary = "Добавление пользователя в систему (ADMIN)",
            description = "В ответ выдаётся объект User-а.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserCreateDto userCreateDTO) {
        UserNoPasswordDto userNoPasswordDTO = userService.addUser(userCreateDTO);
        return ResponseEntity.ok(userNoPasswordDTO);
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(summary = "Изменение данных пользователя по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект User-а.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateDto userUpdateDTO) {
        UserNoPasswordDto userNoPasswordDTO = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(userNoPasswordDTO);
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(summary = "Поиск пользователя по id", description = "В ответ выдаётся найденный объект User-а.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        UserNoPasswordDto userNoPasswordDTO = userService.getUserById(id);
        return ResponseEntity.ok(userNoPasswordDTO);
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(summary = "Поиск пользователя по никнейму (username)", description = "В ответ выдаётся найденный объект User-а.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/by-username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        UserNoPasswordDto userNoPasswordDTO = userService.getUserByUsername(username);
        return ResponseEntity.ok(userNoPasswordDTO);
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(summary = "Вывод списка всех пользователей системы", description = "В ответ выдаётся страница с объектами UserNoPasswordDto", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@PageableDefault(size = 20, sort = "username") Pageable pageable) {
        Page<UserNoPasswordDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Tag(name = "users", description = "Методы для работы с аккаунтами пользователей")
    @Operation(summary = "Удаление пользователя по id (ADMIN)", description = "В ответ выдаётся сообщение об успешном удалении.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Successfully");
    }
}