package com.faultstream.domain.sensor;

import com.faultstream.domain.sensor.dto.SensorReadingResponse;
import com.faultstream.domain.sensor.dto.SensorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSensors() {
        List<SensorResponse> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", sensors
        ));
    }

    @GetMapping("/{id}/readings")
    public ResponseEntity<Map<String, Object>> getSensorReadings(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "100") int last) {
        List<SensorReadingResponse> readings = sensorService.getSensorReadings(id, last);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", readings
        ));
    }
}
