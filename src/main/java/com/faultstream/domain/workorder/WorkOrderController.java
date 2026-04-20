package com.faultstream.domain.workorder;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.workorder.dto.AssignWorkOrderRequest;
import com.faultstream.domain.workorder.dto.WorkOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkOrderResponse>>> getAllWorkOrders() {
        return ResponseEntity.ok(ApiResponse.success(workOrderService.getAllWorkOrders(), "Work orderlar listelendi"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> getWorkOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(workOrderService.getWorkOrderById(id), "Work order getirildi"));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> assignWorkOrder(
            @PathVariable UUID id,
            @Valid @RequestBody AssignWorkOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                workOrderService.assignWorkOrder(id, request.getTechnicianId()),
                "Work order technician'a atandi"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<WorkOrderResponse>> completeWorkOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                workOrderService.completeWorkOrder(id),
                "Work order kapatildi"));
    }
}
