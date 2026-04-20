package com.faultstream.domain.workorder;
import com.faultstream.common.exception.ResourceNotFoundException;
import com.faultstream.domain.alert.Alert;
import com.faultstream.domain.alert.AlertSeverity;
import com.faultstream.domain.user.User;
import com.faultstream.domain.user.UserRepository;
import com.faultstream.domain.user.UserRole;
import com.faultstream.domain.workorder.dto.WorkOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAllWorkOrders() {
        return workOrderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrderById(UUID id) {
        return workOrderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Work order bulunamadi"));
    }

    @Transactional
    public WorkOrder createAutomatedWorkOrder(Alert alert) {
        Optional<WorkOrder> existing = workOrderRepository.findByAlert_IdAndStatusNot(alert.getId(), WorkOrderStatus.CLOSED);
        if (existing.isPresent()) {
            return existing.get();
        }

        User assignedTechnician = userRepository.findFirstByRoleAndIsActiveTrueOrderByCreatedAtAsc(UserRole.TECHNICIAN).orElse(null);
        User creator = userRepository.findFirstByRoleAndIsActiveTrueOrderByCreatedAtAsc(UserRole.ENGINEER)
                .or(() -> userRepository.findFirstByRoleAndIsActiveTrueOrderByCreatedAtAsc(UserRole.ADMIN))
                .orElse(null);

        WorkOrder workOrder = WorkOrder.builder()
                .equipment(alert.getEquipment())
                .alert(alert)
                .title("Investigate " + alert.getEquipment().getName() + " / " + alert.getSensor().getName())
                .description(alert.getMessage())
                .type(WorkOrderType.CORRECTIVE)
                .priority(alert.getSeverity() == null ? WorkOrderPriority.HIGH : WorkOrderPriority.valueOf(alert.getSeverity().name()))
                .status(assignedTechnician != null ? WorkOrderStatus.IN_PROGRESS : WorkOrderStatus.OPEN)
                .assignedTo(assignedTechnician)
                .createdBy(creator)
                .dueDate(LocalDateTime.now().plusHours(alert.getSeverity() == AlertSeverity.CRITICAL ? 4 : 12))
                .build();
        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public WorkOrderResponse assignWorkOrder(UUID workOrderId, UUID technicianId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order bulunamadi"));
        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician bulunamadi"));
        workOrder.setAssignedTo(technician);
        if (workOrder.getStatus() == WorkOrderStatus.OPEN) {
            workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        }
        return mapToResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public WorkOrderResponse completeWorkOrder(UUID workOrderId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Work order bulunamadi"));
        workOrder.setStatus(WorkOrderStatus.CLOSED);
        workOrder.setClosedAt(LocalDateTime.now());
        return mapToResponse(workOrderRepository.save(workOrder));
    }

    public WorkOrderResponse mapToResponse(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .equipmentId(workOrder.getEquipment() != null ? workOrder.getEquipment().getId() : null)
                .equipmentName(workOrder.getEquipment() != null ? workOrder.getEquipment().getName() : null)
                .alertId(workOrder.getAlert() != null ? workOrder.getAlert().getId() : null)
                .title(workOrder.getTitle())
                .description(workOrder.getDescription())
                .type(workOrder.getType())
                .priority(workOrder.getPriority())
                .status(workOrder.getStatus())
                .assignedToId(workOrder.getAssignedTo() != null ? workOrder.getAssignedTo().getId() : null)
                .assignedToName(workOrder.getAssignedTo() != null ? workOrder.getAssignedTo().getFullName() : null)
                .createdById(workOrder.getCreatedBy() != null ? workOrder.getCreatedBy().getId() : null)
                .createdByName(workOrder.getCreatedBy() != null ? workOrder.getCreatedBy().getFullName() : null)
                .dueDate(workOrder.getDueDate())
                .closedAt(workOrder.getClosedAt())
                .createdAt(workOrder.getCreatedAt())
                .build();
    }
}
