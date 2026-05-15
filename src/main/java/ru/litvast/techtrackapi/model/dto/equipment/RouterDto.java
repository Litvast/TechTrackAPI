package ru.litvast.techtrackapi.model.dto.equipment;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.litvast.techtrackapi.model.entity.equipment.RouterBand;
import ru.litvast.techtrackapi.model.entity.equipment.RouterSecurityStandard;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RouterDto extends EquipmentDto {

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

    @AssertTrue(message = "If WAN bandwidth is filled, LAN bandwidth must also be filled")
    private boolean isBandwidthValid() {
        if (wanBandwidth == null && lanBandwidth == null) return true;
        if (wanBandwidth != null && lanBandwidth != null) return true;
        return false;
    }
}