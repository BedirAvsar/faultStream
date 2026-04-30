package com.faultstream.domain.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getActiveAlerts() {
        List<AlertResponse> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", alerts
        ));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAlert(@PathVariable UUID id) {
        AlertResponse response = alertService.resolveAlert(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }
}
