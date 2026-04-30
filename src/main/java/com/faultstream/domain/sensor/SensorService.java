package com.faultstream.domain.sensor;

import com.faultstream.domain.alert.Alert;
import com.faultstream.domain.sensor.dto.SensorDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final com.faultstream.domain.alert.AlertService alertService;
    private final com.faultstream.domain.workorder.WorkOrderService workOrderService;

    @Transactional
    public void processSensorData(SensorDataEvent event) {
        Sensor sensor = sensorRepository.findById(event.sensorId())
                .orElseThrow(() -> new RuntimeException("Sensör bulunamadı: " + event.sensorId()));

        SensorReading reading = SensorReading.builder()
                .sensor(sensor)
                .value(event.value())
                .recordedAt(event.recordedAt())
                .build();

        sensorReadingRepository.save(reading);

        // Check thresholds for alerting
        if (sensor.getThresholdMax() != null && event.value() > sensor.getThresholdMax()) {
            log.warn("CRITICAL: Sensor {} value {} exceeded max threshold {}", sensor.getId(), event.value(), sensor.getThresholdMax());
            Alert alert = alertService.createAlert(
                sensor, 
                sensor.getEquipment(), 
                com.faultstream.domain.alert.AlertLevel.CRITICAL, 
                String.format("Value %.2f exceeded MAX threshold %.2f", event.value(), sensor.getThresholdMax())
            );
            workOrderService.createWorkOrder(alert);
        } else if (sensor.getThresholdMin() != null && event.value() < sensor.getThresholdMin()) {
            log.warn("WARNING: Sensor {} value {} dropped below min threshold {}", sensor.getId(), event.value(), sensor.getThresholdMin());
            alertService.createAlert(
                sensor, 
                sensor.getEquipment(), 
                com.faultstream.domain.alert.AlertLevel.WARNING, 
                String.format("Value %.2f dropped below MIN threshold %.2f", event.value(), sensor.getThresholdMin())
            );
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<com.faultstream.domain.sensor.dto.SensorResponse> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<com.faultstream.domain.sensor.dto.SensorReadingResponse> getSensorReadings(java.util.UUID sensorId, int lastN) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, lastN);
        return sensorReadingRepository.findBySensorIdOrderByRecordedAtDesc(sensorId, pageable).stream()
                .map(r -> new com.faultstream.domain.sensor.dto.SensorReadingResponse(r.getId(), r.getValue(), r.getRecordedAt()))
                .toList();
    }

    private com.faultstream.domain.sensor.dto.SensorResponse mapToResponse(Sensor sensor) {
        return new com.faultstream.domain.sensor.dto.SensorResponse(
                sensor.getId(),
                sensor.getEquipment() != null ? sensor.getEquipment().getId() : null,
                sensor.getName(),
                sensor.getType(),
                sensor.getUnit(),
                sensor.getThresholdMin(),
                sensor.getThresholdMax(),
                sensor.isActive()
        );
    }
}
