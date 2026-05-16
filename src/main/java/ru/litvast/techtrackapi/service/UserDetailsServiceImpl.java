package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.model.entity.User;
import ru.litvast.techtrackapi.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Попытка загрузки пользователя по username: {}", username);

        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("Пользователь с username '{}' не найден", username);
            return new UsernameNotFoundException("Not found user with username: " + username);
        });
    }
}