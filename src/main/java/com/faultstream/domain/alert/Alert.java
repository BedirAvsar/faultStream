package com.faultstream.domain.alert;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.sensor.Sensor;
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
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
    @Column(nullable = false)
    private double value;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertThresholdExceeded thresholdExceeded;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;
    private String message;
    @Builder.Default
    @Column(name = "is_acknowledged")
    private boolean acknowledged = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    @Builder.Default
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
