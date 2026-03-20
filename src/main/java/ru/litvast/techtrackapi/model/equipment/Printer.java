package ru.litvast.techtrackapi.model.equipment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PRINTER")
@Table(name = "printers")
public class Printer extends Equipment {

    @Enumerated(EnumType.STRING)
    private PrintType printType;

    private boolean isColor;
    private double speed;

    private int widthResolution;
    private int heightResolution;

    private String consumption;
}
