package com.faultstream.domain.dashboard;
import com.faultstream.domain.dashboard.dto.*;
import com.faultstream.domain.equipment.EquipmentRepository;
import com.faultstream.domain.sensor.Sensor;
import com.faultstream.domain.sensor.SensorReading;
import com.faultstream.domain.sensor.SensorReadingRepository;
import com.faultstream.domain.sensor.SensorService;
import com.faultstream.domain.sensor.SensorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final EquipmentRepository equipmentRepository;
    private final SensorReadingRepository sensorReadingRepository;

    @Transactional(readOnly = true)
    public DashboardTerminalResponse getTerminalSnapshot() {
        List<SensorReading> latestReadings = sensorReadingRepository.findAllByOrderByRecordedAtDesc(PageRequest.of(0, 250));
        List<SensorReading> lastTwentyFourHours =
                sensorReadingRepository.findByRecordedAtAfterOrderByRecordedAtDesc(LocalDateTime.now().minusHours(24));
        List<SensorReading> lastSevenDays = sensorReadingRepository.findByRecordedAtAfterOrderByRecordedAtDesc(LocalDateTime.now().minusDays(7));
        List<SensorReading> recentAnomalies = latestReadings.stream()
                .filter(this::isAnomaly)
                .limit(9)
                .toList();

        return DashboardTerminalResponse.builder()
                .stats(buildStats(latestReadings, lastTwentyFourHours))
                .stream(buildStream(recentAnomalies))
                .patternAnalysis(buildPatternAnalysis(lastSevenDays))
                .frequencyDensity(buildFrequencyDensity(lastSevenDays))
                .build();
    }

    private DashboardStatsResponse buildStats(List<SensorReading> latestReadings, List<SensorReading> lastTwentyFourHours) {
        Map<UUID, SensorReading> latestBySensor = new LinkedHashMap<>();
        for (SensorReading reading : latestReadings) {
            latestBySensor.putIfAbsent(reading.getSensor().getId(), reading);
        }

        long anomalyCount = lastTwentyFourHours.stream().filter(this::isAnomaly).count();

        long healthy = latestBySensor.values().stream().filter(reading -> !isAnomaly(reading)).count();
        double integrity = latestBySensor.isEmpty() ? 100.0 : (healthy * 100.0) / latestBySensor.size();

        String status = "NOMINAL";
        if (anomalyCount >= 8) {
            status = "CRITICAL";
        } else if (anomalyCount > 0) {
            status = "AWARE";
        }

        return DashboardStatsResponse.builder()
                .activeNodes(equipmentRepository.count())
                .recordedAnomalies(anomalyCount)
                .systemIntegrity(Math.round(integrity * 10.0) / 10.0)
                .status(status)
                .build();
    }

    private List<DashboardEventResponse> buildStream(List<SensorReading> anomalies) {
        return anomalies.stream()
                .map(reading -> {
                    Sensor sensor = reading.getSensor();
                    String severity = SensorService.resolveStatus(sensor, reading.getValue());
                    return DashboardEventResponse.builder()
                            .id(String.valueOf(reading.getId()))
                            .time(reading.getRecordedAt().format(TIME_FORMAT))
                            .equipment(sensor.getEquipment().getName())
                            .faultCode(faultCode(sensor.getType()))
                            .severity("CRITICAL".equals(severity) ? "CRIT" : "WARN")
                            .color(colorFor(sensor.getType()))
                            .value(reading.getValue())
                            .unit(sensor.getUnit())
                            .build();
                })
                .toList();
    }

    private List<DashboardPatternResponse> buildPatternAnalysis(List<SensorReading> readings) {
        Map<SensorType, Long> counts = Arrays.stream(SensorType.values())
                .collect(LinkedHashMap::new, (map, type) -> map.put(type, 0L), Map::putAll);

        readings.stream()
                .filter(this::isAnomaly)
                .forEach(reading -> counts.computeIfPresent(reading.getSensor().getType(), (key, value) -> value + 1));

        return counts.entrySet().stream()
                .map(entry -> DashboardPatternResponse.builder()
                        .name(shortLabel(entry.getKey()))
                        .value(entry.getValue())
                        .color(colorFor(entry.getKey()))
                        .build())
                .toList();
    }

    private List<DashboardFrequencyResponse> buildFrequencyDensity(List<SensorReading> readings) {
        LocalDate today = LocalDate.now();
        List<DashboardFrequencyResponse> frequency = new ArrayList<>();
        for (int offset = 6; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            long faults = readings.stream()
                    .filter(this::isAnomaly)
                    .filter(reading -> reading.getRecordedAt().toLocalDate().isEqual(day))
                    .count();
            frequency.add(DashboardFrequencyResponse.builder()
                    .name(offset == 0 ? "NOW" : "D-" + offset)
                    .faults(faults)
                    .build());
        }
        return frequency;
    }

    private boolean isAnomaly(SensorReading reading) {
        return !"NORMAL".equals(SensorService.resolveStatus(reading.getSensor(), reading.getValue()));
    }

    private String faultCode(SensorType type) {
        return switch (type) {
            case TEMPERATURE -> "THERMAL_OVERLOAD";
            case VIBRATION -> "SYS_DESYNC";
            case PRESSURE -> "PRESSURE_DRIFT";
            case CURRENT -> "VOLTAGE_DROP";
            case HUMIDITY -> "HUMIDITY_SHIFT";
        };
    }

    private String shortLabel(SensorType type) {
        return switch (type) {
            case TEMPERATURE -> "THERMAL";
            case VIBRATION -> "SYS_DESYNC";
            case PRESSURE -> "PRESSURE";
            case CURRENT -> "PWR_DROP";
            case HUMIDITY -> "HUMIDITY";
        };
    }

    private String colorFor(SensorType type) {
        return switch (type) {
            case TEMPERATURE -> "#ef4444";
            case VIBRATION -> "#eab308";
            case PRESSURE -> "#38bdf8";
            case CURRENT -> "#f97316";
            case HUMIDITY -> "#22c55e";
        };
    }
}
