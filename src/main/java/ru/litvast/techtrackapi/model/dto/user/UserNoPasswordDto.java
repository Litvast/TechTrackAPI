package ru.litvast.techtrackapi.model.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import ru.litvast.techtrackapi.model.entity.Role;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNoPasswordDto {
    Integer id;
    String username;
    Role role;
}
