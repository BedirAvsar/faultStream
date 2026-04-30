package com.faultstream.domain.sensor;

import com.faultstream.domain.sensor.dto.SensorDataEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @Mock
    private com.faultstream.domain.alert.AlertService alertService;

    @Mock
    private com.faultstream.domain.workorder.WorkOrderService workOrderService;

    @InjectMocks
    private SensorService sensorService;

    private Sensor sensor;
    private SensorDataEvent event;
    private UUID sensorId;

    @BeforeEach
    void setUp() {
        sensorId = UUID.randomUUID();
        sensor = Sensor.builder()
                .id(sensorId)
                .name("Test Sıcaklık Sensörü")
                .type(SensorType.TEMPERATURE)
                .unit("C")
                .thresholdMax(100.0)
                .thresholdMin(10.0)
                .build();

        event = new SensorDataEvent(
                sensorId,
                UUID.randomUUID(),
                SensorType.TEMPERATURE,
                85.0,
                "C",
                LocalDateTime.now()
        );
    }

    @Test
    void processSensorData_ShouldSaveReading_WhenSensorExists() {
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        sensorService.processSensorData(event);

        ArgumentCaptor<SensorReading> readingCaptor = ArgumentCaptor.forClass(SensorReading.class);
        verify(sensorReadingRepository, times(1)).save(readingCaptor.capture());

        SensorReading savedReading = readingCaptor.getValue();
        assertEquals(sensor, savedReading.getSensor());
        assertEquals(85.0, savedReading.getValue());
    }

    @Test
    void processSensorData_ShouldThrowException_WhenSensorDoesNotExist() {
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sensorService.processSensorData(event));

        assertEquals("Sensör bulunamadı: " + sensorId, exception.getMessage());
        verify(sensorReadingRepository, never()).save(any());
    }

    @Test
    void processSensorData_ShouldCreateCriticalAlertAndWorkOrder_WhenExceedsMaxThreshold() {
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        event = new SensorDataEvent(sensorId, UUID.randomUUID(), SensorType.TEMPERATURE, 105.0, "C", LocalDateTime.now());
        
        com.faultstream.domain.alert.Alert alert = new com.faultstream.domain.alert.Alert();
        when(alertService.createAlert(any(), any(), eq(com.faultstream.domain.alert.AlertLevel.CRITICAL), anyString())).thenReturn(alert);

        sensorService.processSensorData(event);

        verify(alertService, times(1)).createAlert(any(), any(), eq(com.faultstream.domain.alert.AlertLevel.CRITICAL), anyString());
        verify(workOrderService, times(1)).createWorkOrder(alert);
    }

    @Test
    void processSensorData_ShouldCreateWarningAlert_WhenDropsBelowMinThreshold() {
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        event = new SensorDataEvent(sensorId, UUID.randomUUID(), SensorType.TEMPERATURE, 5.0, "C", LocalDateTime.now());

        sensorService.processSensorData(event);

        verify(alertService, times(1)).createAlert(any(), any(), eq(com.faultstream.domain.alert.AlertLevel.WARNING), anyString());
        verify(workOrderService, never()).createWorkOrder(any());
    }
}
