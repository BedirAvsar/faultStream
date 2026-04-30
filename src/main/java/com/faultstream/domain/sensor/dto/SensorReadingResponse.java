package com.faultstream.domain.sensor.dto;

import java.time.LocalDateTime;

public record SensorReadingResponse(
        Long id,
        Double value,
        LocalDateTime recordedAt
) {}
