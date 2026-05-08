package com.faultstream.domain.alert;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    @EntityGraph(attributePaths = {"sensor", "equipment", "acknowledgedBy"})
    List<Alert> findAllByOrderByCreatedAtDesc();
    @EntityGraph(attributePaths = {"sensor", "equipment", "acknowledgedBy"})
    List<Alert> findByAcknowledgedFalseOrderByCreatedAtDesc();
    @EntityGraph(attributePaths = {"sensor", "equipment", "acknowledgedBy"})
    Optional<Alert> findById(UUID id);
    @EntityGraph(attributePaths = {"sensor", "equipment"})
    Optional<Alert> findTopBySensor_IdAndAcknowledgedFalseOrderByCreatedAtDesc(UUID sensorId);
}
