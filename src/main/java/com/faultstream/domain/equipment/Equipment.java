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
    @Id // Bu alan birincil (primary) anahtar
    @GeneratedValue(strategy = GenerationType.UUID) // ID yi otomatik ve benzersiz olarak üretir
    private UUID id;

    @Column(nullable = false) // Veritabanında bu alan boş geçilemez
    private String name;

    @Enumerated(EnumType.STRING) // Enum değerlerini string olarak saklar
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
    @Column(updatable = false) // Bu alan sonradan değiştirilemez
    private LocalDateTime createdAt = LocalDateTime.now();
}

// ============================================================================================
// PEKİ NEDEN BUNLARI YAPTIK?
// 3 temel sebep var:
// 1. Veri Güvenliği: Düşünelim fabrikada bir cihazın seri nosu yanlışlıkla iki
// kere girildi.
// Eğer unique = true demezsem, tamam gel abi der.
// Bir arıza durumunda ise hangi cihazın arızalandığını bilemeyiz. Çünkü 2 kayıt
// var. :)
// 2. ORM: Eğer bu Annotaionları koymasaydım her seferinde SQL sorgusu yazmak
// zorunda kalırdım.
// Neden ben hamallık yapayım ki Spring yapsın değil mi? :)
// 3. İzlenebilirlik: Bu sayede hangi cihazın ne zaman sisteme girdiğini söyler.
// Dolaysıyla birileri bize 1 nisan yapamasın diye orayı mühürledik.
// ============================================================================================