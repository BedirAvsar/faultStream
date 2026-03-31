/* ====================== NOTLAR ====================== */

/**
 * Sistemin beyninini test ediyorum
 * Mockito ile veritabanı ve dış servisleri taklit ediyorum
 *  Neden Postgresql kullanmadım?
 * Çünkü test ortamında veritabanı kurmak hem yavaş hem de gereksiz
 */

package com.faultstream.domain.equipment;

import com.faultstream.domain.equipment.dto.CreateEquipmentRequest;
import com.faultstream.domain.equipment.dto.EquipmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private Equipment equipment;
    private CreateEquipmentRequest createRequest;
    private UUID equipmentId;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();

        // Veritabanında duran örnek makine
        equipment = Equipment.builder()
                .id(equipmentId)
                .name("Test Sensörlü Pompa")
                .type(EquipmentType.PUMP)
                .status(EquipmentStatus.ACTIVE)
                .location("B Blok")
                .build();

        // Kullanıcının gönderdiği yeni kayıt isteği
        createRequest = new CreateEquipmentRequest();
        createRequest.setName("Test Sensörlü Pompa");
        createRequest.setType(EquipmentType.PUMP);
        createRequest.setLocation("B Blok");
    }

    // SENARYO 1: Başarılı Kayıt
    @Test
    void createEquipment_ShouldReturnResponse_WhenValidRequest() {
        // Arrange: Repository'e "Biri kaydet diyince bu ekipmanı dön" diyoruz
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        // Act: Servisi çalıştır (Aksiyon)
        EquipmentResponse response = equipmentService.createEquipment(createRequest);

        // Assert: Sonuçları doğrula (Teyit)
        assertNotNull(response);
        assertEquals("Test Sensörlü Pompa", response.getName());
        assertEquals(EquipmentType.PUMP, response.getType());

        // Repo gerçekten 1 kere çağrılmış mı kontrol et
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    // SENARYO 2: ID ile Başarılı Bulma
    @Test
    void getEquipmentById_ShouldReturnEquipment_WhenIdExists() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));

        EquipmentResponse response = equipmentService.getEquipmentById(equipmentId);

        assertNotNull(response);
        assertEquals(equipmentId, response.getId());
        verify(equipmentRepository, times(1)).findById(equipmentId);
    }

    // SENARYO 3: Yanlış ID ile Hata Fırlatma
    @Test
    void getEquipmentById_ShouldThrowException_WhenIdDoesNotExist() {
        UUID wrongId = UUID.randomUUID();
        when(equipmentRepository.findById(wrongId)).thenReturn(Optional.empty());

        // Hata fırlatmasını bekliyoruz
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> equipmentService.getEquipmentById(wrongId));

        assertEquals("Ekipman bulunamadı!", exception.getMessage());
    }

    // SENARYO 4: Tümünü Başarıyla Çağırma
    @Test
    void getAllEquipments_ShouldReturnList() {
        when(equipmentRepository.findAll()).thenReturn(List.of(equipment));

        List<EquipmentResponse> responseList = equipmentService.getAllEquipments();

        assertFalse(responseList.isEmpty());
        assertEquals(1, responseList.size());
        assertEquals("Test Sensörlü Pompa", responseList.get(0).getName());
    }
}
