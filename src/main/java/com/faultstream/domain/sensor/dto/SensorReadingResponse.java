package com.faultstream.domain.sensor.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Builder
public class SensorReadingResponse {
    private Long id;
    private String equipmentName;
    private String sensorName;
    private String sensorType;
    private double value;
    private String unit;
    private String status;
    private LocalDateTime recordedAt;
}
