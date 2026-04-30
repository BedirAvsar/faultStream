package com.faultstream.domain.sensor.dto;

import com.faultstream.domain.sensor.SensorType;

import java.util.UUID;

public record SensorResponse(
        UUID id,
        UUID equipmentId,
        String name,
        SensorType type,
        String unit,
        Double thresholdMin,
        Double thresholdMax,
        boolean isActive
) {}
