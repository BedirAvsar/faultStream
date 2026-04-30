package com.faultstream.domain.alert;

import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.sensor.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    @CacheEvict(value = "activeAlerts", allEntries = true)
    public Alert createAlert(Sensor sensor, Equipment equipment, AlertLevel level, String message) {
        Alert alert = Alert.builder()
                .sensor(sensor)
                .equipment(equipment)
                .level(level)
                .message(message)
                .triggeredAt(LocalDateTime.now())
                .status(AlertStatus.ACTIVE)
                .build();
        
        alert = alertRepository.save(alert);
        log.warn("New Alert created: {} - Level: {} - Sensor: {}", alert.getId(), level, sensor.getName());
        return alert;
    }

    @Transactional
    @CacheEvict(value = "activeAlerts", allEntries = true)
    public AlertResponse resolveAlert(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with id: " + alertId));
        
        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert = alertRepository.save(alert);
        
        log.info("Alert resolved: {}", alertId);
        return mapToResponse(alert);
    }

    @Cacheable(value = "activeAlerts")
    @Transactional(readOnly = true)
    public List<AlertResponse> getActiveAlerts() {
        return alertRepository.findByStatus(AlertStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .sensorId(alert.getSensor().getId())
                .equipmentId(alert.getEquipment().getId())
                .level(alert.getLevel().name())
                .message(alert.getMessage())
                .triggeredAt(alert.getTriggeredAt())
                .resolvedAt(alert.getResolvedAt())
                .status(alert.getStatus().name())
                .build();
    }
}
