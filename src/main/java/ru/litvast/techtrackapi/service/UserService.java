package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.model.dto.JwtTokensDto;
import ru.litvast.techtrackapi.model.dto.RefreshTokenDto;
import ru.litvast.techtrackapi.model.dto.mapping.UserMapping;
import ru.litvast.techtrackapi.model.dto.user.UserCreateDto;
import ru.litvast.techtrackapi.model.dto.user.UserCredentialsDto;
import ru.litvast.techtrackapi.model.dto.user.UserNoPasswordDto;
import ru.litvast.techtrackapi.model.dto.user.UserUpdateDto;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.model.entity.Role;
import ru.litvast.techtrackapi.model.entity.User;
import ru.litvast.techtrackapi.repository.UserRepository;
import ru.litvast.techtrackapi.security.JwtService;
import ru.litvast.techtrackapi.util.Converter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapping userMapping;
    private final JwtService jwtService;

    @Transactional
    public UserNoPasswordDto signup(UserCredentialsDto userCredentialsDTO) {
        log.info("=== НАЧАЛО: Регистрация пользователя ===");
        log.info("Username: {}", userCredentialsDTO.getUsername());

        if (userRepository.existsByUsernameIgnoreCase(userCredentialsDTO.getUsername())) {
            log.warn("Пользователь с username '{}' уже существует", userCredentialsDTO.getUsername());
            throw new IllegalArgumentException("User with this nickname already exists");
        }

        User user = userMapping.userCredentialsDtoToUser(userCredentialsDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        UserNoPasswordDto userDto = userMapping.userToUserNoPasswordDto(userRepository.save(user));

        log.info("Пользователь зарегистрирован. ID: {}", user.getId());
        log.info("=== УСПЕШНО: Регистрация завершена ===");

        return userDto;
    }

    public JwtTokensDto signin(UserCredentialsDto userCredentialsDTO) throws AuthenticationException {
        log.info("=== НАЧАЛО: Авторизация пользователя ===");
        log.info("Username: {}", userCredentialsDTO.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentialsDTO.getUsername(),
                            userCredentialsDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtTokensDto tokens = jwtService.generateJwtTokens(userCredentialsDTO.getUsername());

            log.info("Пользователь авторизован. Username: {}", userCredentialsDTO.getUsername());
            log.info("=== УСПЕШНО: Авторизация завершена ===");

            return tokens;
        } catch (AuthenticationException e) {
            log.warn("Ошибка авторизации для username '{}': {}", userCredentialsDTO.getUsername(), e.getMessage());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public JwtTokensDto refreshUserToken(RefreshTokenDto refreshTokenDTO) throws AuthenticationException {
        log.info("=== НАЧАЛО: Обновление токена ===");

        String refreshToken = refreshTokenDTO.getToken();

        if (!jwtService.validateJwtToken(refreshToken) && !jwtService.isRefreshToken(refreshToken)) {
            log.warn("Невалидный refresh токен");
            throw new BadCredentialsException("This refresh token is not valid");
        }

        String username = jwtService.getUsernameFromJwtToken(refreshToken);
        log.debug("Username из refresh токена: {}", username);

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.error("Пользователь с username '{}' не найден", username);
                    return new EntityNotFoundException(
                            String.format("User with username '%s' not found", username)
                    );
                });

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        log.info("Токен обновлён для пользователя: {}", username);
        log.info("=== УСПЕШНО: Токен обновлён ===");

        return new JwtTokensDto(accessToken, refreshToken);
    }

    // CREATE
    @Transactional
    public UserNoPasswordDto addUser(UserCreateDto userCreateDTO) {
        log.info("=== НАЧАЛО: Добавление пользователя администратором ===");
        log.info("Username: {}, Role: {}", userCreateDTO.getUsername(), userCreateDTO.getRole());

        if (userRepository.existsByUsernameIgnoreCase(userCreateDTO.getUsername())) {
            log.warn("Пользователь с username '{}' уже существует", userCreateDTO.getUsername());
            throw new IllegalArgumentException(
                    String.format("Username '%s' is already taken", userCreateDTO.getUsername())
            );
        }

        User user = userMapping.userFullInfoDtoToUser(userCreateDTO);

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
            log.debug("Роль не указана, установлена ROLE_USER");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.info("Пользователь создан администратором. ID: {}", user.getId());
        log.info("=== УСПЕШНО: Пользователь добавлен ===");

        return userMapping.userToUserNoPasswordDto(user);
    }

    // UPDATE
    @Transactional
    public UserNoPasswordDto updateUser(String stringId, UserUpdateDto userUpdateDTO) {
        log.info("=== НАЧАЛО: Обновление пользователя ===");
        log.info("ID пользователя: {}", stringId);

        int id = Converter.convertIdStringToInt(stringId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("User with id '%d' not found", id)
                    );
                });

        boolean usernameFilled = userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().isBlank();
        if (usernameFilled && !userUpdateDTO.getUsername().equalsIgnoreCase(user.getUsername())
                && userRepository.existsByUsernameIgnoreCase(userUpdateDTO.getUsername())) {
            log.warn("Username '{}' уже занят", userUpdateDTO.getUsername());
            throw new IllegalArgumentException(
                    String.format("Username '%s' is already taken", userUpdateDTO.getUsername())
            );
        }

        if (usernameFilled) {
            log.info("Изменение username: {} -> {}", user.getUsername(), userUpdateDTO.getUsername());
            user.setUsername(userUpdateDTO.getUsername());
        }

        boolean passwordFilled = userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isBlank();
        if (passwordFilled) {
            log.info("Изменение пароля для пользователя ID: {}", id);
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        if (userUpdateDTO.getRole() != null) {
            log.info("Изменение роли: {} -> {}", user.getRole(), userUpdateDTO.getRole());
            user.setRole(userUpdateDTO.getRole());
        }

        userRepository.save(user);
        log.info("=== УСПЕШНО: Пользователь обновлён ===");

        return userMapping.userToUserNoPasswordDto(user);
    }

    // READ by id
    public UserNoPasswordDto getUserById(String stringId) {
        log.debug("Поиск пользователя по ID: {}", stringId);

        int id = Converter.convertIdStringToInt(stringId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("User with id '%d' not found", id)
                    );
                });

        return userMapping.userToUserNoPasswordDto(user);
    }

    // READ by username
    public UserNoPasswordDto getUserByUsername(String username) {
        log.debug("Поиск пользователя по username: {}", username);

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.error("Пользователь с username '{}' не найден", username);
                    return new EntityNotFoundException(
                            String.format("User with username '%s' not found", username)
                    );
                });

        return userMapping.userToUserNoPasswordDto(user);
    }

    // READ all with pagination
    public Page<UserNoPasswordDto> getAllUsers(Pageable pageable) {
        log.debug("Запрос всех пользователей с пагинацией");

        Page<User> users = userRepository.findAll(pageable);
        if (users.isEmpty()) {
            log.warn("Пользователи не найдены");
            throw new NoEntitiesFoundException("No users found");
        }

        log.debug("Найдено {} пользователей", users.getTotalElements());
        return users.map(userMapping::userToUserNoPasswordDto);
    }

    // DELETE
    @Transactional
    public void deleteUser(String stringId) {
        log.info("=== НАЧАЛО: Удаление пользователя ===");
        log.info("ID пользователя: {}", stringId);

        int id = Converter.convertIdStringToInt(stringId);

        if (!userRepository.existsById(id)) {
            log.error("Пользователь с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("User with id '%d' not found", id)
            );
        }

        userRepository.deleteById(id);
        log.info("=== УСПЕШНО: Пользователь удалён ===");
    }
}