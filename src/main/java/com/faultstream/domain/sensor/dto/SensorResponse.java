package com.faultstream.domain.sensor.dto;
import com.faultstream.domain.sensor.SensorType;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class SensorResponse {
    private UUID id;
    private UUID equipmentId;
    private String equipmentName;
    private String name;
    private SensorType type;
    private String unit;
    private Double thresholdMin;
    private Double thresholdMax;
    private boolean active;
}
