package ru.litvast.techtrackapi.dto.mapper;

import ru.litvast.techtrackapi.dto.UserCredentialsDTO;
import ru.litvast.techtrackapi.dto.UserDTO;
import ru.litvast.techtrackapi.model.User;

public class UserMapper {

    public static UserDTO userToDTO(User user) {
        return new UserDTO(user.getUsername());
    }

    public static User userCredentialsDTOToUser(UserCredentialsDTO userCredentialsDTO) {
        return new User(userCredentialsDTO.getUsername(), userCredentialsDTO.getPassword());
    }
}
