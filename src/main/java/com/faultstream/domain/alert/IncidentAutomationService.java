package com.faultstream.domain.alert;
import com.faultstream.domain.sensor.SensorReading;
import com.faultstream.domain.sensor.SensorService;
import com.faultstream.domain.workorder.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class IncidentAutomationService {
    private final AlertService alertService;
    private final WorkOrderService workOrderService;

    @Transactional
    public void processReading(SensorReading reading) {
        String status = SensorService.resolveStatus(reading.getSensor(), reading.getValue());
        if ("NORMAL".equals(status)) {
            return;
        }

        AlertSeverity targetSeverity = AlertService.resolveSeverity(reading.getSensor(), reading.getValue());
        AlertThresholdExceeded targetThreshold = AlertService.resolveThreshold(reading.getSensor(), reading.getValue());

        Alert alert = alertService.findRecentActiveAlertForSensor(reading.getSensor().getId())
                .filter(existing ->
                        existing.getSeverity() == targetSeverity &&
                        existing.getThresholdExceeded() == targetThreshold &&
                        existing.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(10)))
                .orElseGet(() -> alertService.createAlert(reading));

        if (alert.getSeverity() == AlertSeverity.CRITICAL) {
            workOrderService.createAutomatedWorkOrder(alert);
        }
    }
}
