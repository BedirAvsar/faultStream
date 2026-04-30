package com.faultstream.domain.equipment.dto;
import com.faultstream.domain.equipment.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
public record CreateEquipmentRequest(
    @NotBlank(message = "Ekipman ismi boş olamaz") String name,
    @NotNull(message = "Ekipman tipi boş olamaz") EquipmentType type,
    @NotBlank(message = "Ekipman lokasyonu boş olamaz") String location,
    String building,
    String floor,
    String manufacturer,
    String model,
    String serialNumber
) {}
