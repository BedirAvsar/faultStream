package com.faultstream.domain.sensor.stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class SensorDataConsumer {
    private final SensorReadingIngestionService ingestionService;

    @KafkaListener(topics = "${faultstream.kafka.sensor-topic:sensor-readings}")
    public void consume(SensorReadingEvent event) {
        ingestionService.ingest(event);
        log.debug("Sensor reading consumed for sensor={}", event.getSensorId());
    }
}
