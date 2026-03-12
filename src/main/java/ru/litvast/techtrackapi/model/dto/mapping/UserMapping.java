package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.user.UserCredentialsDto;
import ru.litvast.techtrackapi.model.dto.user.UserCreateDto;
import ru.litvast.techtrackapi.model.dto.user.UserNoPasswordDto;
import ru.litvast.techtrackapi.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapping {
    User userCredentialsDtoToUser(UserCredentialsDto userCredentialsDto);
    User userFullInfoDtoToUser(UserCreateDto userFullInfoDto);
    UserNoPasswordDto userToUserNoPasswordDto(User user);
}
