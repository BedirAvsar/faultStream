package com.faultstream.domain.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByStatus(AlertStatus status);
    List<Alert> findByEquipmentId(UUID equipmentId);
    List<Alert> findBySensorId(UUID sensorId);
}
