package com.faultstream.domain.maintenance.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class MaintenanceLogResponse {
    private UUID id;
    private UUID workOrderId;
    private String workOrderTitle;
    private UUID equipmentId;
    private String equipmentName;
    private UUID technicianId;
    private String technicianName;
    private String actionTaken;
    private String partsUsed;
    private Double laborHours;
    private String notes;
    private LocalDateTime loggedAt;
}
