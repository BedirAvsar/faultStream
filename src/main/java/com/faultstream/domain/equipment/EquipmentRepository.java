package com.faultstream.domain.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository // Bu bir veritabanı erişim katmanıdır
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    // JpaRepository bize CRUD işlemlerini otomatik olarak sağlar
}