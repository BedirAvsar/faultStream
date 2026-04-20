package com.faultstream.domain.alert;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.alert.dto.AlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getAllAlerts() {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAllAlerts(), "Alertler listelendi"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AlertResponse>>> getActiveAlerts() {
        return ResponseEntity.ok(ApiResponse.success(alertService.getActiveAlerts(), "Aktif alertler listelendi"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AlertResponse>> getAlertById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(alertService.getAlertById(id), "Alert getirildi"));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<AlertResponse>> resolveAlert(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID acknowledgedBy) {
        return ResponseEntity.ok(ApiResponse.success(
                alertService.acknowledgeAlert(id, acknowledgedBy),
                "Alert onaylandi"));
    }
}
