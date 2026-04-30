package com.faultstream.domain.workorder;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WorkOrderResponse {
    private UUID id;
    private UUID alertId;
    private UUID assignedTechnicianId;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
