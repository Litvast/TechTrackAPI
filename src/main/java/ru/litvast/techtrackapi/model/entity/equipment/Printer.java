package ru.litvast.techtrackapi.model.entity.equipment;

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

    private Boolean isColor;

    private Double speed;

    private Integer widthResolution;
    private Integer heightResolution;

    private String consumption;

    private String model;

    private Boolean isDuplex;

    private Boolean isNetwork;

    private String paperSize;
}