package ru.litvast.techtrackapi.model.dto.equipment;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.RouterBand;
import ru.litvast.techtrackapi.model.entity.equipment.RouterSecurityStandard;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterUpdateDto {

    @Positive(message = "ID cannot be negative or zero")
    private Long id;

    private String name;
    private String manufacturer;
    private String inventoryNumber;

    @Positive(message = "WAN bandwidth cannot be negative or zero")
    private Integer wanBandwidth;

    @Positive(message = "LAN bandwidth cannot be negative or zero")
    private Integer lanBandwidth;

    @Positive(message = "WAN ports count cannot be negative or zero")
    private Integer wanPorts;

    @Positive(message = "LAN ports count cannot be negative or zero")
    private Integer lanPorts;

    @Positive(message = "USB ports count cannot be negative or zero")
    private Integer usbPorts;

    private RouterBand band;
    private RouterSecurityStandard securityStandard;
}