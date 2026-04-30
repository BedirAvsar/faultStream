package com.faultstream.domain.equipment.dto;
import com.faultstream.domain.equipment.EquipmentStatus;
import com.faultstream.domain.equipment.EquipmentType;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Builder
public record EquipmentResponse(
    UUID id,
    String name,
    EquipmentType type,
    String location,
    String serialNumber,
    EquipmentStatus status
) {}
