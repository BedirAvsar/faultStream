package com.faultstream.domain.sensor;
import com.faultstream.common.exception.ResourceNotFoundException;
import com.faultstream.domain.dashboard.DashboardCacheService;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.equipment.EquipmentRepository;
import com.faultstream.domain.sensor.dto.CreateSensorRequest;
import com.faultstream.domain.sensor.dto.SensorReadingResponse;
import com.faultstream.domain.sensor.dto.SensorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final EquipmentRepository equipmentRepository;
    private final DashboardCacheService dashboardCacheService;

    @Transactional
    public SensorResponse createSensor(CreateSensorRequest request) {
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor eklenecek ekipman bulunamadi"));

        Sensor sensor = Sensor.builder()
                .equipment(equipment)
                .name(request.getName())
                .type(request.getType())
                .unit(request.getUnit())
                .thresholdMin(request.getThresholdMin())
                .thresholdMax(request.getThresholdMax())
                .build();

        Sensor savedSensor = sensorRepository.save(sensor);
        dashboardCacheService.evictTerminalSnapshot();
        return mapSensor(savedSensor);
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> getAllSensors() {
        return sensorRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapSensor)
                .toList();
    }

    @Transactional(readOnly = true)
    public SensorResponse getSensorById(UUID id) {
        return sensorRepository.findById(id)
                .map(this::mapSensor)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor bulunamadi"));
    }

    @Transactional(readOnly = true)
    public List<SensorReadingResponse> getSensorReadings(UUID sensorId, int last) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor bulunamadi"));

        int limit = Math.max(1, Math.min(last, 250));
        return sensorReadingRepository.findBySensorIdOrderByRecordedAtDesc(sensorId, PageRequest.of(0, limit))
                .stream()
                .map(reading -> mapReading(reading, sensor))
                .toList();
    }

    private SensorResponse mapSensor(Sensor sensor) {
        return SensorResponse.builder()
                .id(sensor.getId())
                .equipmentId(sensor.getEquipment().getId())
                .equipmentName(sensor.getEquipment().getName())
                .name(sensor.getName())
                .type(sensor.getType())
                .unit(sensor.getUnit())
                .thresholdMin(sensor.getThresholdMin())
                .thresholdMax(sensor.getThresholdMax())
                .active(sensor.isActive())
                .build();
    }

    private SensorReadingResponse mapReading(SensorReading reading, Sensor fallbackSensor) {
        Sensor sensor = reading.getSensor() != null ? reading.getSensor() : fallbackSensor;
        return SensorReadingResponse.builder()
                .id(reading.getId())
                .equipmentName(sensor.getEquipment().getName())
                .sensorName(sensor.getName())
                .sensorType(sensor.getType().name())
                .value(reading.getValue())
                .unit(sensor.getUnit())
                .status(resolveStatus(sensor, reading.getValue()))
                .recordedAt(reading.getRecordedAt())
                .build();
    }

    public static String resolveStatus(Sensor sensor, double value) {
        boolean belowMin = sensor.getThresholdMin() != null && value < sensor.getThresholdMin();
        boolean aboveMax = sensor.getThresholdMax() != null && value > sensor.getThresholdMax();
        if (belowMin || aboveMax) {
            double deviation = calculateDeviation(sensor, value);
            return deviation >= 0.18 ? "CRITICAL" : "WARNING";
        }
        return "NORMAL";
    }

    public static double calculateDeviation(Sensor sensor, double value) {
        if (sensor.getThresholdMin() != null && value < sensor.getThresholdMin()) {
            return safeDeviation(sensor.getThresholdMin(), value);
        }
        if (sensor.getThresholdMax() != null && value > sensor.getThresholdMax()) {
            return safeDeviation(sensor.getThresholdMax(), value);
        }
        return 0.0;
    }

    private static double safeDeviation(double threshold, double value) {
        double base = Math.max(Math.abs(threshold), 1.0);
        return Math.abs(value - threshold) / base;
    }
}
