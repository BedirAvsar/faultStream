package com.faultstream.domain.equipment.dto;
import com.faultstream.domain.equipment.EquipmentStatus;
import com.faultstream.domain.equipment.EquipmentType;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class EquipmentResponse {
    private UUID id;
    private String name;
    private EquipmentType type;
    private String location;
    private String serialNumber;
    private EquipmentStatus status;
}
