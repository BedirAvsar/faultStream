package com.faultstream.domain.alert.dto;
import com.faultstream.domain.alert.AlertSeverity;
import com.faultstream.domain.alert.AlertThresholdExceeded;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class AlertResponse {
    private UUID id;
    private UUID sensorId;
    private UUID equipmentId;
    private String sensorName;
    private String equipmentName;
    private double value;
    private AlertThresholdExceeded thresholdExceeded;
    private AlertSeverity severity;
    private String message;
    private boolean acknowledged;
    private UUID acknowledgedById;
    private String acknowledgedByName;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime createdAt;
}
