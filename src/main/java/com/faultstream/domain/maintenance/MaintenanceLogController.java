package com.faultstream.domain.maintenance;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.maintenance.dto.CreateMaintenanceLogRequest;
import com.faultstream.domain.maintenance.dto.MaintenanceLogResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/maintenance-logs")
@RequiredArgsConstructor
public class MaintenanceLogController {
    private final MaintenanceLogService maintenanceLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceLogResponse>> createMaintenanceLog(
            @Valid @RequestBody CreateMaintenanceLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        maintenanceLogService.createMaintenanceLog(request),
                        "Maintenance log olusturuldu"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaintenanceLogResponse>>> getAllLogs() {
        return ResponseEntity.ok(ApiResponse.success(
                maintenanceLogService.getAllMaintenanceLogs(),
                "Maintenance loglar listelendi"));
    }

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<ApiResponse<List<MaintenanceLogResponse>>> getLogsByWorkOrder(@PathVariable UUID workOrderId) {
        return ResponseEntity.ok(ApiResponse.success(
                maintenanceLogService.getLogsByWorkOrder(workOrderId),
                "Work order maintenance loglari getirildi"));
    }
}
