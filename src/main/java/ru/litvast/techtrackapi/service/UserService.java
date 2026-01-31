package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.dto.JwtTokensDTO;
import ru.litvast.techtrackapi.dto.RefreshTokenDTO;
import ru.litvast.techtrackapi.dto.UserCredentialsDTO;
import ru.litvast.techtrackapi.dto.mapper.UserMapper;
import ru.litvast.techtrackapi.model.User;
import ru.litvast.techtrackapi.repository.UserRepository;
import ru.litvast.techtrackapi.security.JwtService;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void signup(UserCredentialsDTO userCredentialsDTO) throws Exception {
        if (userRepository.existsByUsernameIgnoreCase(userCredentialsDTO.getUsername())) {
            throw new Exception("User with this nickname already exists");
        }

        userCredentialsDTO.setPassword(passwordEncoder.encode(userCredentialsDTO.getPassword()));
        userRepository.save(UserMapper.userCredentialsDTOToUser(userCredentialsDTO));
    }

    public JwtTokensDTO signin(UserCredentialsDTO userCredentialsDTO) throws AuthenticationException {
        User user = getUserByUsername(userCredentialsDTO.getUsername());
        if (passwordEncoder.matches(userCredentialsDTO.getPassword(), user.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCredentialsDTO.getUsername(), userCredentialsDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtService.generateJwtTokens(user.getUsername());
        }
        throw new BadCredentialsException("Invalid password");
    }

    public JwtTokensDTO refresh(RefreshTokenDTO refreshTokenDTO) throws AuthenticationException {
        String refreshToken = refreshTokenDTO.getToken();
        if (jwtService.validateJwtToken(refreshToken)
                && jwtService.isRefreshToken(refreshToken)) {
            String username = jwtService.getUsernameFromJwtToken(refreshToken);
            User user = getUserByUsername(username);
            String accessToken = jwtService.generateAccessToken(user.getUsername());
            return new JwtTokensDTO(accessToken, refreshToken);
        }
        throw new BadCredentialsException("This refresh token is not valid");
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new AuthenticationCredentialsNotFoundException("User with this username not found"));
    }
}
