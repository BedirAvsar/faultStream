package com.faultstream.domain.maintenance;
import com.faultstream.common.exception.ResourceNotFoundException;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.equipment.EquipmentRepository;
import com.faultstream.domain.maintenance.dto.CreateMaintenanceLogRequest;
import com.faultstream.domain.maintenance.dto.MaintenanceLogResponse;
import com.faultstream.domain.user.User;
import com.faultstream.domain.user.UserRepository;
import com.faultstream.domain.user.UserRole;
import com.faultstream.domain.workorder.WorkOrder;
import com.faultstream.domain.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class MaintenanceLogService {
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public MaintenanceLogResponse createMaintenanceLog(CreateMaintenanceLogRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Work order bulunamadi"));
        User technician = resolveTechnician(request.getTechnicianId(), workOrder);

        MaintenanceLog log = MaintenanceLog.builder()
                .workOrder(workOrder)
                .technician(technician)
                .actionTaken(request.getActionTaken())
                .partsUsed(request.getPartsUsed())
                .laborHours(request.getLaborHours())
                .notes(request.getNotes())
                .build();
        MaintenanceLog saved = maintenanceLogRepository.save(log);

        Equipment equipment = workOrder.getEquipment();
        if (equipment != null) {
            equipment.setLastMaintenanceDate(LocalDate.now());
            equipmentRepository.save(equipment);
        }
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceLogResponse> getAllMaintenanceLogs() {
        return maintenanceLogRepository.findAllByOrderByLoggedAtDesc().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceLogResponse> getLogsByWorkOrder(UUID workOrderId) {
        return maintenanceLogRepository.findByWorkOrder_IdOrderByLoggedAtDesc(workOrderId).stream().map(this::mapToResponse).toList();
    }

    private User resolveTechnician(UUID technicianId, WorkOrder workOrder) {
        if (technicianId != null) {
            User technician = userRepository.findById(technicianId)
                    .orElseThrow(() -> new ResourceNotFoundException("Technician bulunamadi"));
            validateTechnicianRole(technician);
            return technician;
        }
        User technician = workOrder.getAssignedTo();
        if (technician != null) {
            validateTechnicianRole(technician);
        }
        return technician;
    }

    private void validateTechnicianRole(User technician) {
        if (technician.getRole() != UserRole.TECHNICIAN) {
            throw new IllegalArgumentException("Maintenance log icin technician rolunde bir kullanici secilmelidir");
        }
    }

    private MaintenanceLogResponse mapToResponse(MaintenanceLog log) {
        return MaintenanceLogResponse.builder()
                .id(log.getId())
                .workOrderId(log.getWorkOrder() != null ? log.getWorkOrder().getId() : null)
                .workOrderTitle(log.getWorkOrder() != null ? log.getWorkOrder().getTitle() : null)
                .equipmentId(log.getWorkOrder() != null && log.getWorkOrder().getEquipment() != null ? log.getWorkOrder().getEquipment().getId() : null)
                .equipmentName(log.getWorkOrder() != null && log.getWorkOrder().getEquipment() != null ? log.getWorkOrder().getEquipment().getName() : null)
                .technicianId(log.getTechnician() != null ? log.getTechnician().getId() : null)
                .technicianName(log.getTechnician() != null ? log.getTechnician().getFullName() : null)
                .actionTaken(log.getActionTaken())
                .partsUsed(log.getPartsUsed())
                .laborHours(log.getLaborHours())
                .notes(log.getNotes())
                .loggedAt(log.getLoggedAt())
                .build();
    }
}
