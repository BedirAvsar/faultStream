package com.faultstream.domain.maintenance;
import com.faultstream.domain.user.User;
import com.faultstream.domain.workorder.WorkOrder;
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
@Table(name = "maintenance_logs")
public class MaintenanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;
    @Column(nullable = false)
    private String actionTaken;
    private String partsUsed;
    private Double laborHours;
    private String notes;
    @Builder.Default
    private LocalDateTime loggedAt = LocalDateTime.now();
}
