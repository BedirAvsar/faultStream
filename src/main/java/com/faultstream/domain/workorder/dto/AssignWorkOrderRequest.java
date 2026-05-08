package com.faultstream.domain.workorder.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
@Data
public class AssignWorkOrderRequest {
    @NotNull(message = "Technician secimi zorunludur")
    private UUID technicianId;
}
