package com.faultstream.domain.sensor;

import com.faultstream.domain.sensor.dto.SensorDataEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@Disabled("Geliştirme ortamında Docker olmadığı için şimdilik devre dışı")
class SensorIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("simulator.enabled", () -> "false"); // Disable simulator for test
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Test
    void testKafkaToDatabaseFlow() {
        // Create a test sensor
        Sensor sensor = Sensor.builder()
                .name("Integration Test Sensor")
                .type(SensorType.VIBRATION)
                .unit("mm/s")
                .thresholdMax(50.0)
                .build();
        sensor = sensorRepository.save(sensor);

        // Send a message via Kafka
        SensorDataEvent event = new SensorDataEvent(
                sensor.getId(),
                UUID.randomUUID(), // Mock equipment ID
                SensorType.VIBRATION,
                42.5,
                "mm/s",
                LocalDateTime.now()
        );

        kafkaTemplate.send("sensor-data", event.sensorId().toString(), event);

        // Wait and verify if it's saved in DB (via Consumer -> Service -> Repository)
        Sensor finalSensor = sensor;
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            long count = sensorReadingRepository.count();
            assertThat(count).isGreaterThanOrEqualTo(1);
            
            // Check latest reading
            SensorReading latest = sensorReadingRepository.findAll().stream()
                    .filter(r -> r.getSensor().getId().equals(finalSensor.getId()))
                    .findFirst()
                    .orElse(null);
            
            assertThat(latest).isNotNull();
            assertThat(latest.getValue()).isEqualTo(42.5);
        });
    }
}
