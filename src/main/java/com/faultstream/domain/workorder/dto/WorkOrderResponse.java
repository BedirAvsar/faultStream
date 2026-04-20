package com.faultstream.domain.workorder.dto;
import com.faultstream.domain.workorder.WorkOrderPriority;
import com.faultstream.domain.workorder.WorkOrderStatus;
import com.faultstream.domain.workorder.WorkOrderType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class WorkOrderResponse {
    private UUID id;
    private UUID equipmentId;
    private String equipmentName;
    private UUID alertId;
    private String title;
    private String description;
    private WorkOrderType type;
    private WorkOrderPriority priority;
    private WorkOrderStatus status;
    private UUID assignedToId;
    private String assignedToName;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime dueDate;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
}
