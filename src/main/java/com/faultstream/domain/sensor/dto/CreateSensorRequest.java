package com.faultstream.domain.sensor.dto;
import com.faultstream.domain.sensor.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
@Data
public class CreateSensorRequest {
    @NotNull(message = "Ekipman secimi zorunludur")
    private UUID equipmentId;
    @NotBlank(message = "Sensor adi bos olamaz")
    private String name;
    @NotNull(message = "Sensor tipi bos olamaz")
    private SensorType type;
    @NotBlank(message = "Birim bos olamaz")
    private String unit;
    private Double thresholdMin;
    private Double thresholdMax;
}
