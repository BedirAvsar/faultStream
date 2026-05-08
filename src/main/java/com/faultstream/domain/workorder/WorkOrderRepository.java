package com.faultstream.domain.workorder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
    @EntityGraph(attributePaths = {"equipment", "alert", "assignedTo", "createdBy"})
    List<WorkOrder> findAllByOrderByCreatedAtDesc();
    @EntityGraph(attributePaths = {"equipment", "alert", "assignedTo", "createdBy"})
    Optional<WorkOrder> findById(UUID id);
    @EntityGraph(attributePaths = {"equipment", "alert", "assignedTo", "createdBy"})
    Optional<WorkOrder> findByAlert_IdAndStatusNot(UUID alertId, WorkOrderStatus status);
}
