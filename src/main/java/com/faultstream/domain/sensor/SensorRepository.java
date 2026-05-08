package com.faultstream.domain.sensor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    @EntityGraph(attributePaths = "equipment")
    List<Sensor> findByIsActiveTrueOrderByNameAsc();
    @EntityGraph(attributePaths = "equipment")
    List<Sensor> findByEquipmentIdOrderByNameAsc(UUID equipmentId);
    @EntityGraph(attributePaths = "equipment")
    Optional<Sensor> findById(UUID id);
}
