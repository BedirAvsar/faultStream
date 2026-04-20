package com.faultstream.domain.sensor;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.equipment.EquipmentRepository;
import com.faultstream.domain.equipment.EquipmentType;
import com.faultstream.domain.sensor.dto.CreateSensorRequest;
import com.faultstream.domain.sensor.dto.SensorReadingResponse;
import com.faultstream.domain.sensor.dto.SensorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SensorServiceTest {
    @Mock
    private SensorRepository sensorRepository;
    @Mock
    private SensorReadingRepository sensorReadingRepository;
    @Mock
    private EquipmentRepository equipmentRepository;
    @InjectMocks
    private SensorService sensorService;

    private Equipment equipment;
    private Sensor sensor;
    private UUID sensorId;

    @BeforeEach
    void setUp() {
        sensorId = UUID.randomUUID();
        equipment = Equipment.builder()
                .id(UUID.randomUUID())
                .name("TRB-01")
                .type(EquipmentType.COMPRESSOR)
                .location("Turbine Hall")
                .build();
        sensor = Sensor.builder()
                .id(sensorId)
                .equipment(equipment)
                .name("Bearing Temp")
                .type(SensorType.TEMPERATURE)
                .unit("C")
                .thresholdMin(55.0)
                .thresholdMax(92.0)
                .build();
    }

    @Test
    void createSensor_ShouldPersistAndMapResponse() {
        CreateSensorRequest request = new CreateSensorRequest();
        request.setEquipmentId(equipment.getId());
        request.setName("Bearing Temp");
        request.setType(SensorType.TEMPERATURE);
        request.setUnit("C");
        request.setThresholdMin(55.0);
        request.setThresholdMax(92.0);

        when(equipmentRepository.findById(equipment.getId())).thenReturn(Optional.of(equipment));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        SensorResponse response = sensorService.createSensor(request);

        assertEquals("Bearing Temp", response.getName());
        assertEquals("TRB-01", response.getEquipmentName());
        assertEquals(SensorType.TEMPERATURE, response.getType());
        verify(sensorRepository).save(any(Sensor.class));
    }

    @Test
    void getSensorReadings_ShouldReturnDerivedStatuses() {
        SensorReading reading = SensorReading.builder()
                .id(1L)
                .sensor(sensor)
                .value(97.5)
                .recordedAt(LocalDateTime.now())
                .build();

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(sensorReadingRepository.findBySensorIdOrderByRecordedAtDesc(eq(sensorId), any(PageRequest.class)))
                .thenReturn(List.of(reading));

        List<SensorReadingResponse> readings = sensorService.getSensorReadings(sensorId, 10);

        assertEquals(1, readings.size());
        assertEquals("CRITICAL", readings.get(0).getStatus());
        assertEquals("TRB-01", readings.get(0).getEquipmentName());
    }
}
