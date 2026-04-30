package com.faultstream.domain.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faultstream.domain.sensor.dto.SensorReadingResponse;
import com.faultstream.domain.sensor.dto.SensorResponse;
import com.faultstream.security.JwtAuthFilter;
import com.faultstream.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SensorController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for unit tests
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SensorService sensorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private UUID sensorId;

    @BeforeEach
    void setUp() {
        sensorId = UUID.randomUUID();
    }

    @Test
    void getAllSensors_ShouldReturnList() throws Exception {
        SensorResponse response = new SensorResponse(
                sensorId,
                UUID.randomUUID(),
                "Test Sensor",
                SensorType.TEMPERATURE,
                "C",
                0.0,
                100.0,
                true
        );

        when(sensorService.getAllSensors()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Test Sensor"))
                .andExpect(jsonPath("$.data[0].type").value("TEMPERATURE"));
    }

    @Test
    void getSensorReadings_ShouldReturnList() throws Exception {
        SensorReadingResponse reading = new SensorReadingResponse(
                1L,
                42.5,
                LocalDateTime.now()
        );

        when(sensorService.getSensorReadings(sensorId, 100)).thenReturn(List.of(reading));

        mockMvc.perform(get("/api/v1/sensors/{id}/readings?last=100", sensorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].value").value(42.5));
    }
}
