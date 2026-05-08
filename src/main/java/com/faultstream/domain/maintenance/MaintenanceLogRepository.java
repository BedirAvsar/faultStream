package com.faultstream.domain.maintenance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, UUID> {
    @EntityGraph(attributePaths = {"workOrder", "workOrder.equipment", "technician"})
    List<MaintenanceLog> findAllByOrderByLoggedAtDesc();
    @EntityGraph(attributePaths = {"workOrder", "workOrder.equipment", "technician"})
    List<MaintenanceLog> findByWorkOrder_IdOrderByLoggedAtDesc(UUID workOrderId);
    @EntityGraph(attributePaths = {"workOrder", "workOrder.equipment", "technician"})
    Optional<MaintenanceLog> findById(UUID id);
}
