package ru.litvast.techtrackapi.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDto {

    private Long id;

    @Size(message = "Name cannot be longer than 255 characters", max = 255)
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9\\s.\\-/'()]+$",
            message = "Only Latin and Russian characters, numbers, spaces, dots, hyphens, slashes, apostrophes, brackets are allowed")
    private String name;

    @Size(message = "Description cannot be longer than 1000 characters", max = 1000)
    private String description;

    @Pattern(regexp = "^\\d{10}$|^\\d{12}$", message = "INN must be 10 or 12 digits")
    private String inn;

    @Pattern(regexp = "^\\d{9}$", message = "KPP must be 9 digits")
    private String kpp;

    @Pattern(regexp = "^\\d{13}$|^\\d{15}$", message = "OGRN must be 13 or 15 digits")
    private String ogrn;

    @Size(message = "Address cannot be longer than 500 characters", max = 500)
    private String address;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;

    @Email(message = "Email should be valid")
    private String email;
}