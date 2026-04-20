package com.faultstream.domain.maintenance.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
@Data
public class CreateMaintenanceLogRequest {
    @NotNull(message = "Work order secimi zorunludur")
    private UUID workOrderId;
    private UUID technicianId;
    @NotBlank(message = "Action taken bos olamaz")
    private String actionTaken;
    private String partsUsed;
    private Double laborHours;
    private String notes;
}
