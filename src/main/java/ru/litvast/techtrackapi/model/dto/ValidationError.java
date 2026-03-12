package ru.litvast.techtrackapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ValidationError {

    String field;
    Object rejectedValue;
    String code;
    String message;
}
