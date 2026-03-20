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
@DiscriminatorValue("ROUTER")
@Table(name = "routers")
public class Router extends Equipment {

    private int wanBandwidth;
    private int lanBandwidth;

    private int wanPorts;
    private int lanPorts;
    private int usbPorts;

    @Enumerated(EnumType.STRING)
    private RouterBand band;

    @Enumerated(EnumType.STRING)
    private RouterSecurityStandard securityStandard;
}
