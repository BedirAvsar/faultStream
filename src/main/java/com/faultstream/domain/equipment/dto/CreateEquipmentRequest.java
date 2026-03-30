package com.faultstream.domain.equipment.dto;

import com.faultstream.domain.equipment.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEquipmentRequest {
    @NotBlank(message = "Ekipman ismi boş olamaz")
    private String name;

    @NotNull(message = "Ekipman tipi boş olamaz")
    private EquipmentType type;

    @NotBlank(message = "Ekipman lokasyonu boş olamaz")
    private String location;

    private String building;
    private String floor;
    private String manufacturer;
    private String model;
    private String serialNumber;
}
