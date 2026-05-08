package com.faultstream.domain.sensor.stream;
import com.faultstream.common.exception.ResourceNotFoundException;
import com.faultstream.domain.alert.IncidentAutomationService;
import com.faultstream.domain.dashboard.DashboardCacheService;
import com.faultstream.domain.sensor.Sensor;
import com.faultstream.domain.sensor.SensorReading;
import com.faultstream.domain.sensor.SensorReadingRepository;
import com.faultstream.domain.sensor.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class SensorReadingIngestionService {
    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final IncidentAutomationService incidentAutomationService;
    private final DashboardCacheService dashboardCacheService;

    @Transactional
    public void ingest(SensorReadingEvent event) {
        Sensor sensor = sensorRepository.findById(event.getSensorId())
                .orElseThrow(() -> new ResourceNotFoundException("Okumasi yazilacak sensor bulunamadi"));

        SensorReading reading = SensorReading.builder()
                .sensor(sensor)
                .value(event.getValue())
                .recordedAt(LocalDateTime.parse(event.getRecordedAt()))
                .build();
        SensorReading saved = sensorReadingRepository.save(reading);
        incidentAutomationService.processReading(saved);
        dashboardCacheService.evictTerminalSnapshot();
    }
}
