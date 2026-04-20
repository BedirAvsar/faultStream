package com.faultstream.domain.sensor.stream;
import com.faultstream.config.SimulatorProperties;
import com.faultstream.domain.sensor.Sensor;
import com.faultstream.domain.sensor.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
@RequiredArgsConstructor
public class SensorSimulationService {
    private final SimulatorProperties simulatorProperties;
    private final SensorRepository sensorRepository;
    private final SensorReadingIngestionService ingestionService;
    private final KafkaTemplate<String, SensorReadingEvent> kafkaTemplate;
    private final Random random = new Random();

    @Value("${faultstream.kafka.sensor-topic:sensor-readings}")
    private String sensorTopic;

    @Scheduled(fixedDelayString = "${simulator.interval-ms:5000}")
    public void scheduledSimulationTick() {
        if (!simulatorProperties.isEnabled()) {
            return;
        }
        simulateOneCycle(false);
    }

    public void seedInitialHistory(int cycles) {
        if (!simulatorProperties.isEnabled()) {
            return;
        }
        for (int i = 0; i < cycles; i++) {
            simulateOneCycle(true);
        }
    }

    private void simulateOneCycle(boolean directOnly) {
        List<Sensor> sensors = sensorRepository.findByIsActiveTrueOrderByNameAsc();
        for (Sensor sensor : sensors) {
            SensorReadingEvent event = SensorReadingEvent.builder()
                    .sensorId(sensor.getId())
                    .value(generateValue(sensor))
                    .recordedAt(LocalDateTime.now().toString())
                    .source(directOnly ? "bootstrap" : "simulator")
                    .build();
            dispatch(sensor, event, directOnly);
        }
    }

    private void dispatch(Sensor sensor, SensorReadingEvent event, boolean directOnly) {
        if (directOnly) {
            ingestionService.ingest(event);
            return;
        }

        try {
            kafkaTemplate.send(sensorTopic, sensor.getId().toString(), event).get(2, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.warn("Kafka unavailable, falling back to direct ingestion for sensor={}", sensor.getId());
            ingestionService.ingest(event);
        }
    }

    private double generateValue(Sensor sensor) {
        double normalValue = switch (sensor.getType()) {
            case TEMPERATURE -> 68 + random.nextDouble() * 16;
            case VIBRATION -> 1.2 + random.nextDouble() * 2.1;
            case PRESSURE -> 3.5 + random.nextDouble() * 2.8;
            case CURRENT -> 18 + random.nextDouble() * 24;
            case HUMIDITY -> 35 + random.nextDouble() * 20;
        };

        boolean anomaly = random.nextDouble() < 0.22;
        if (!anomaly) {
            return round(normalValue);
        }

        double direction = random.nextBoolean() ? 1 : -1;
        double anomalyValue = switch (sensor.getType()) {
            case TEMPERATURE -> normalValue + (14 + random.nextDouble() * 18) * direction;
            case VIBRATION -> normalValue + (1.8 + random.nextDouble() * 2.5) * direction;
            case PRESSURE -> normalValue + (1.4 + random.nextDouble() * 2.6) * direction;
            case CURRENT -> normalValue + (10 + random.nextDouble() * 18) * direction;
            case HUMIDITY -> normalValue + (10 + random.nextDouble() * 20) * direction;
        };
        return round(Math.max(0.1, anomalyValue));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
