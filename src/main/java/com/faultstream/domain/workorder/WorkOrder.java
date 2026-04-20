package com.faultstream.domain.workorder;
import com.faultstream.domain.alert.Alert;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_orders")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private Alert alert;
    @Column(nullable = false)
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderType type;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WorkOrderPriority priority = WorkOrderPriority.MEDIUM;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WorkOrderStatus status = WorkOrderStatus.OPEN;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    private LocalDateTime dueDate;
    private LocalDateTime closedAt;
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
