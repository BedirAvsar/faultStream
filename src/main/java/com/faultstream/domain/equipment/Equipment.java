package com.faultstream.domain.equipment;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipments")
public class Equipment {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID) 
    private UUID id;
    @Column(nullable = false) 
    private String name;
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private EquipmentType type;
    @Column(nullable = false)
    private String location;
    private String building;
    private String floor;
    private String manufacturer;
    private String model;
    @Column(unique = true)
    private String serialNumber;
    private LocalDate installDate;
    private LocalDate lastMaintenanceDate;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EquipmentStatus status = EquipmentStatus.ACTIVE;
    @Builder.Default
    @Column(updatable = false) 
    private LocalDateTime createdAt = LocalDateTime.now();
}
