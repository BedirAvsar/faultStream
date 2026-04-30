package com.faultstream.domain.sensor.dto;

import com.faultstream.domain.sensor.SensorType;

import java.time.LocalDateTime;
import java.util.UUID;

public record SensorDataEvent(
    UUID sensorId,
    UUID equipmentId,
    SensorType type,
    Double value,
    String unit,
    LocalDateTime recordedAt
) {}
