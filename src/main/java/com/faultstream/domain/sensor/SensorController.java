package com.faultstream.domain.sensor;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.sensor.dto.CreateSensorRequest;
import com.faultstream.domain.sensor.dto.SensorReadingResponse;
import com.faultstream.domain.sensor.dto.SensorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<ApiResponse<SensorResponse>> createSensor(@Valid @RequestBody CreateSensorRequest request) {
        SensorResponse response = sensorService.createSensor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Sensor basariyla olusturuldu"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SensorResponse>>> getAllSensors() {
        return ResponseEntity.ok(ApiResponse.success(sensorService.getAllSensors(), "Sensorler listelendi"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SensorResponse>> getSensorById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(sensorService.getSensorById(id), "Sensor getirildi"));
    }

    @GetMapping("/{id}/readings")
    public ResponseEntity<ApiResponse<List<SensorReadingResponse>>> getSensorReadings(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "50") int last) {
        return ResponseEntity.ok(ApiResponse.success(
                sensorService.getSensorReadings(id, last),
                "Sensor okumalari getirildi"));
    }
}
