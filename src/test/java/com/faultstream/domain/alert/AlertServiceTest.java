package com.faultstream.domain.alert;

import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.sensor.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Sensor sensor;
    private Equipment equipment;
    private Alert alert;

    @BeforeEach
    void setUp() {
        equipment = Equipment.builder().id(UUID.randomUUID()).name("Test Equipment").build();
        sensor = Sensor.builder().id(UUID.randomUUID()).name("Test Sensor").equipment(equipment).build();
        alert = Alert.builder()
                .id(UUID.randomUUID())
                .sensor(sensor)
                .equipment(equipment)
                .level(AlertLevel.CRITICAL)
                .message("Test Message")
                .triggeredAt(LocalDateTime.now())
                .status(AlertStatus.ACTIVE)
                .build();
    }

    @Test
    void testCreateAlert() {
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        Alert created = alertService.createAlert(sensor, equipment, AlertLevel.CRITICAL, "Test Message");

        assertNotNull(created);
        assertEquals(AlertLevel.CRITICAL, created.getLevel());
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void testResolveAlert() {
        when(alertRepository.findById(alert.getId())).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponse response = alertService.resolveAlert(alert.getId());

        assertNotNull(response);
        assertEquals(AlertStatus.RESOLVED.name(), response.getStatus());
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void testGetActiveAlerts() {
        when(alertRepository.findByStatus(AlertStatus.ACTIVE)).thenReturn(List.of(alert));

        List<AlertResponse> responses = alertService.getActiveAlerts();

        assertEquals(1, responses.size());
        assertEquals(AlertStatus.ACTIVE.name(), responses.get(0).getStatus());
        verify(alertRepository, times(1)).findByStatus(AlertStatus.ACTIVE);
    }
}
