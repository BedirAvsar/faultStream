package com.faultstream.domain.alert;
import com.faultstream.common.exception.ResourceNotFoundException;
import com.faultstream.domain.alert.dto.AlertResponse;
import com.faultstream.domain.sensor.Sensor;
import com.faultstream.domain.sensor.SensorReading;
import com.faultstream.domain.sensor.SensorService;
import com.faultstream.domain.user.User;
import com.faultstream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getActiveAlerts() {
        return alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public AlertResponse getAlertById(UUID id) {
        return alertRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Alert bulunamadi"));
    }

    @Transactional
    public AlertResponse acknowledgeAlert(UUID alertId, UUID acknowledgedById) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert bulunamadi"));
        if (!alert.isAcknowledged()) {
            alert.setAcknowledged(true);
            alert.setAcknowledgedAt(LocalDateTime.now());
            if (acknowledgedById != null) {
                User user = userRepository.findById(acknowledgedById)
                        .orElseThrow(() -> new ResourceNotFoundException("Onaylayan kullanici bulunamadi"));
                alert.setAcknowledgedBy(user);
            }
        }
        return mapToResponse(alertRepository.save(alert));
    }

    @Transactional
    public Alert createAlert(SensorReading reading) {
        Sensor sensor = reading.getSensor();
        Alert alert = Alert.builder()
                .sensor(sensor)
                .equipment(sensor.getEquipment())
                .value(reading.getValue())
                .thresholdExceeded(resolveThreshold(sensor, reading.getValue()))
                .severity(resolveSeverity(sensor, reading.getValue()))
                .message(buildMessage(sensor, reading.getValue()))
                .build();
        return alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public Optional<Alert> findRecentActiveAlertForSensor(UUID sensorId) {
        return alertRepository.findTopBySensor_IdAndAcknowledgedFalseOrderByCreatedAtDesc(sensorId);
    }

    public AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .sensorId(alert.getSensor() != null ? alert.getSensor().getId() : null)
                .equipmentId(alert.getEquipment() != null ? alert.getEquipment().getId() : null)
                .sensorName(alert.getSensor() != null ? alert.getSensor().getName() : null)
                .equipmentName(alert.getEquipment() != null ? alert.getEquipment().getName() : null)
                .value(alert.getValue())
                .thresholdExceeded(alert.getThresholdExceeded())
                .severity(alert.getSeverity())
                .message(alert.getMessage())
                .acknowledged(alert.isAcknowledged())
                .acknowledgedById(alert.getAcknowledgedBy() != null ? alert.getAcknowledgedBy().getId() : null)
                .acknowledgedByName(alert.getAcknowledgedBy() != null ? alert.getAcknowledgedBy().getFullName() : null)
                .acknowledgedAt(alert.getAcknowledgedAt())
                .createdAt(alert.getCreatedAt())
                .build();
    }

    public static AlertThresholdExceeded resolveThreshold(Sensor sensor, double value) {
        if (sensor.getThresholdMin() != null && value < sensor.getThresholdMin()) {
            return AlertThresholdExceeded.MIN;
        }
        return AlertThresholdExceeded.MAX;
    }

    public static AlertSeverity resolveSeverity(Sensor sensor, double value) {
        String sensorStatus = SensorService.resolveStatus(sensor, value);
        double deviation = SensorService.calculateDeviation(sensor, value);
        if ("CRITICAL".equals(sensorStatus)) {
            return AlertSeverity.CRITICAL;
        }
        if (deviation >= 0.12) {
            return AlertSeverity.HIGH;
        }
        if (deviation >= 0.06) {
            return AlertSeverity.MEDIUM;
        }
        return AlertSeverity.LOW;
    }

    private static String buildMessage(Sensor sensor, double value) {
        AlertThresholdExceeded thresholdExceeded = resolveThreshold(sensor, value);
        String boundary = thresholdExceeded == AlertThresholdExceeded.MIN ? "below minimum" : "above maximum";
        return sensor.getEquipment().getName() + " / " + sensor.getName() + " is " + boundary + " threshold with value " + value + " " + sensor.getUnit();
    }
}
