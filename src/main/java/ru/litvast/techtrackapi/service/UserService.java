package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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
import ru.litvast.techtrackapi.exception.NoUsersFoundException;
import ru.litvast.techtrackapi.exception.UserNotFoundException;
import ru.litvast.techtrackapi.model.entity.Role;
import ru.litvast.techtrackapi.model.entity.User;
import ru.litvast.techtrackapi.repository.UserRepository;
import ru.litvast.techtrackapi.security.JwtService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapping userMapping;
    private final JwtService jwtService;

    @Transactional
    public void signup(UserCredentialsDto userCredentialsDTO) {
        if (userRepository.existsByUsernameIgnoreCase(userCredentialsDTO.getUsername())) {
            throw new IllegalArgumentException("User with this nickname already exists");
        }

        User user = userMapping.userCredentialsDtoToUser(userCredentialsDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
    }

    public JwtTokensDto signin(UserCredentialsDto userCredentialsDTO) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentialsDTO.getUsername(),
                            userCredentialsDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtService.generateJwtTokens(userCredentialsDTO.getUsername());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public JwtTokensDto refreshUserToken(RefreshTokenDto refreshTokenDTO) throws AuthenticationException {
        String refreshToken = refreshTokenDTO.getToken();

        if (!jwtService.validateJwtToken(refreshToken)
                && !jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("This refresh token is not valid");
        }

        String username = jwtService.getUsernameFromJwtToken(refreshToken);
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
            new UserNotFoundException(username)
        );
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        return new JwtTokensDto(accessToken, refreshToken);
    }

    @Transactional
    public UserNoPasswordDto addUser(UserCreateDto userCreateDTO) {
        if (userCreateDTO == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }

        if (userCreateDTO.getUsername() == null || userCreateDTO.getUsername().isBlank()
                || userCreateDTO.getPassword() == null || userCreateDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("No username or password entered");
        }

        if (userRepository.existsByUsernameIgnoreCase(userCreateDTO.getUsername())) {
            throw new IllegalArgumentException(
                    String.format("Username '%s' is already taken", userCreateDTO.getUsername())
            );
        }

        User user = userMapping.userFullInfoDtoToUser(userCreateDTO);

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userMapping.userToUserNoPasswordDto(user);
    }

    @Transactional
    public UserNoPasswordDto updateUser(String stringId, UserUpdateDto userUpdateDTO) {
        int id = parseIdStringToInt(stringId);

        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id)
        );

        if ((userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().isBlank())
                && !userUpdateDTO.getUsername().equalsIgnoreCase(user.getUsername())
                && userRepository.existsByUsernameIgnoreCase(userUpdateDTO.getUsername())) {
            throw new IllegalArgumentException(
                    String.format("Username '%s' is already taken", userUpdateDTO.getUsername())
            );
        }

        user.setUsername(userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().isBlank()
                ? userUpdateDTO.getUsername()
                : user.getUsername());

        user.setPassword(userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isBlank()
                ? passwordEncoder.encode(userUpdateDTO.getPassword())
                : user.getPassword());

        user.setRole(userUpdateDTO.getRole() != null
                ? userUpdateDTO.getRole()
                : user.getRole());

        userRepository.save(user);
        return userMapping.userToUserNoPasswordDto(user);
    }

    public UserNoPasswordDto getUserById(String stringId) {
        int id  = parseIdStringToInt(stringId);

        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id)
        );

        return userMapping.userToUserNoPasswordDto(user);
    }

    public UserNoPasswordDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new UserNotFoundException("User with this username not found"));

        return userMapping.userToUserNoPasswordDto(user);
    }

    public List<UserNoPasswordDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoUsersFoundException();
        }

        List<UserNoPasswordDto> usersWithoutPassword = new ArrayList<>(users.size());
        users.forEach(user -> usersWithoutPassword.add(userMapping.userToUserNoPasswordDto(user)));
        return usersWithoutPassword;
    }

    @Transactional
    public void deleteUser(String stringId) {
        int id = parseIdStringToInt(stringId);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }

    private int parseIdStringToInt(String stringId) {
        int id;
        try {
            id = Integer.parseInt(stringId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("User's ID should be a number");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        return id;
    }
}
