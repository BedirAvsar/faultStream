package com.faultstream.domain.sensor;

import com.faultstream.domain.sensor.dto.SensorDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorSimulatorScheduler {

    private final SensorRepository sensorRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();

    @Value("${simulator.enabled:false}")
    private boolean simulatorEnabled;

    @Scheduled(fixedRateString = "${simulator.interval-ms:5000}")
    @Transactional(readOnly = true)
    public void generateSensorData() {
        if (!simulatorEnabled) {
            return;
        }

        List<Sensor> activeSensors = sensorRepository.findByIsActiveTrue();
        
        for (Sensor sensor : activeSensors) {
            double value = simulateValue(sensor);
            
            SensorDataEvent event = new SensorDataEvent(
                    sensor.getId(),
                    sensor.getEquipment().getId(),
                    sensor.getType(),
                    value,
                    sensor.getUnit(),
                    LocalDateTime.now()
            );

            kafkaTemplate.send("sensor-data", sensor.getId().toString(), event);
            log.debug("Sent simulated data for sensor {}: {}", sensor.getId(), value);
        }
    }

    private double simulateValue(Sensor sensor) {
        // Generate a random value near the expected thresholds, or just random
        double min = sensor.getThresholdMin() != null ? sensor.getThresholdMin() * 0.9 : 0.0;
        double max = sensor.getThresholdMax() != null ? sensor.getThresholdMax() * 1.1 : 100.0;
        return min + (max - min) * random.nextDouble();
    }
}
