package ru.litvast.techtrackapi.dto;

import lombok.Data;
import ru.litvast.techtrackapi.model.Role;

@Data
public class UserNoPasswordDTO {
    private Integer id;
    private String username;
    private Role role;
}
