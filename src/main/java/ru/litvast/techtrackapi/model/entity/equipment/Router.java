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
@DiscriminatorValue("ROUTER")
@Table(name = "routers")
public class Router extends Equipment {

    private String model;

    private Integer wanBandwidth;
    private Integer lanBandwidth;

    private Integer wanPorts;
    private Integer lanPorts;
    private Integer usbPorts;

    @Enumerated(EnumType.STRING)
    private RouterBand band;

    @Enumerated(EnumType.STRING)
    private RouterSecurityStandard securityStandard;

    private Boolean isWiFi6;
    private Boolean isMesh;
    private Integer antennaGain;
}