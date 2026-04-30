package com.faultstream.domain.sensor;

import com.faultstream.domain.sensor.dto.SensorDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorDataConsumer {

    private final SensorService sensorService;

    @KafkaListener(topics = "sensor-data", groupId = "faultstream-group")
    public void consumeSensorData(@Payload SensorDataEvent event, Acknowledgment acknowledgment) {
        log.debug("Received sensor data event: {}", event);
        try {
            sensorService.processSensorData(event);
            acknowledgment.acknowledge(); // Manual ack for at-least-once reliability
        } catch (Exception e) {
            log.error("Error processing sensor data event: {}", event, e);
            // Will not acknowledge, letting Kafka retry or send to DLQ based on config
            throw e;
        }
    }
}
