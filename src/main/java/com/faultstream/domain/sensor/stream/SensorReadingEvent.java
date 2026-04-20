package com.faultstream.domain.sensor.stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingEvent {
    private UUID sensorId;
    private double value;
    private String recordedAt;
    private String source;
}
