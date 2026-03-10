package ru.litvast.techtrackapi.dto.mapping;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.dto.user.UserCredentialsDTO;
import ru.litvast.techtrackapi.dto.user.UserCreateDTO;
import ru.litvast.techtrackapi.dto.user.UserNoPasswordDTO;
import ru.litvast.techtrackapi.model.User;

@Mapper(componentModel = "spring")
public interface UserMapping {
    User userCredentialsDTOToUser(UserCredentialsDTO userCredentialsDTO);
    User userFullInfoDTOToUser (UserCreateDTO userFullInfoDTO);
    UserNoPasswordDTO userToUserNoPasswordDTO(User user);
}
