package com.faultstream.domain.workorder;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOpenWorkOrders() {
        List<WorkOrderResponse> workOrders = workOrderService.getOpenWorkOrders();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", workOrders
        ));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeWorkOrder(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> payload) {
        
        String notes = payload != null ? payload.get("notes") : "";
        WorkOrderResponse response = workOrderService.completeWorkOrder(id, notes);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }
}
