package com.faultstream.domain.alert;

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
    private String level;
    private String message;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String status;
}
