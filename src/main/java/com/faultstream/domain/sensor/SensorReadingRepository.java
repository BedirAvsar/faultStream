package com.faultstream.domain.sensor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    @EntityGraph(attributePaths = {"sensor", "sensor.equipment"})
    List<SensorReading> findBySensorIdOrderByRecordedAtDesc(UUID sensorId, Pageable pageable);
    @EntityGraph(attributePaths = {"sensor", "sensor.equipment"})
    List<SensorReading> findByRecordedAtAfterOrderByRecordedAtDesc(LocalDateTime recordedAt);
    @EntityGraph(attributePaths = {"sensor", "sensor.equipment"})
    List<SensorReading> findAllByOrderByRecordedAtDesc(Pageable pageable);
}
