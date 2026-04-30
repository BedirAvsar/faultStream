package com.faultstream.domain.workorder;

import com.faultstream.domain.alert.Alert;
import com.faultstream.domain.user.User;
import com.faultstream.domain.user.UserRepository;
import com.faultstream.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkOrder createWorkOrder(Alert alert) {
        // Try to find a technician
        List<User> technicians = userRepository.findByRole(UserRole.TECHNICIAN);
        User assignedTechnician = technicians.isEmpty() ? null : technicians.get(0);

        WorkOrder workOrder = WorkOrder.builder()
                .alert(alert)
                .assignedTechnician(assignedTechnician)
                .status(WorkOrderStatus.OPEN)
                .notes("Auto-generated work order from CRITICAL alert on Sensor: " + alert.getSensor().getName())
                .build();

        workOrder = workOrderRepository.save(workOrder);
        
        if (assignedTechnician != null) {
            log.info("Work Order {} auto-assigned to Technician {}", workOrder.getId(), assignedTechnician.getEmail());
        } else {
            log.warn("Work Order {} created but NO TECHNICIAN is available to assign!", workOrder.getId());
        }

        return workOrder;
    }

    @Transactional
    public WorkOrderResponse completeWorkOrder(UUID workOrderId, String notes) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("WorkOrder not found with id: " + workOrderId));
        
        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setCompletedAt(LocalDateTime.now());
        if (notes != null && !notes.isEmpty()) {
            workOrder.setNotes(workOrder.getNotes() + "\nResolution Notes: " + notes);
        }
        
        workOrder = workOrderRepository.save(workOrder);
        log.info("Work Order {} completed.", workOrderId);
        
        return mapToResponse(workOrder);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getOpenWorkOrders() {
        return workOrderRepository.findByStatus(WorkOrderStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private WorkOrderResponse mapToResponse(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .alertId(workOrder.getAlert().getId())
                .assignedTechnicianId(workOrder.getAssignedTechnician() != null ? workOrder.getAssignedTechnician().getId() : null)
                .status(workOrder.getStatus().name())
                .notes(workOrder.getNotes())
                .createdAt(workOrder.getCreatedAt())
                .completedAt(workOrder.getCompletedAt())
                .build();
    }
}
