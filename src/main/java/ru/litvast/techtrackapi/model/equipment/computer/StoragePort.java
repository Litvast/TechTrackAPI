package ru.litvast.techtrackapi.model.equipment.computer;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class StoragePort {
    private String portType;
    private String version;
    private String formFactor;
    private Integer count;
    private String connectionInterface;
    private Integer lanes;
    private String maxSpeed;
    private Boolean isShared;
}
