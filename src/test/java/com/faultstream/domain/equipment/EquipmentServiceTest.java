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
@SuppressWarnings("null")
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
        equipment = Equipment.builder()
                .id(equipmentId)
                .name("Test Sensörlü Pompa")
                .type(EquipmentType.PUMP)
                .status(EquipmentStatus.ACTIVE)
                .location("B Blok")
                .build();
        createRequest = new CreateEquipmentRequest(
                "Test Sensörlü Pompa",
                EquipmentType.PUMP,
                "B Blok",
                null, null, null, null, null
        );
    }
    @Test
    void createEquipment_ShouldReturnResponse_WhenValidRequest() {
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);
        EquipmentResponse response = equipmentService.createEquipment(createRequest);
        assertNotNull(response);
        assertEquals("Test Sensörlü Pompa", response.name());
        assertEquals(EquipmentType.PUMP, response.type());
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }
    @Test
    void getEquipmentById_ShouldReturnEquipment_WhenIdExists() {
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        EquipmentResponse response = equipmentService.getEquipmentById(equipmentId);
        assertNotNull(response);
        assertEquals(equipmentId, response.id());
        verify(equipmentRepository, times(1)).findById(equipmentId);
    }
    @Test
    void getEquipmentById_ShouldThrowException_WhenIdDoesNotExist() {
        UUID wrongId = UUID.randomUUID();
        when(equipmentRepository.findById(wrongId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> equipmentService.getEquipmentById(wrongId));
        assertEquals("Ekipman bulunamadı!", exception.getMessage());
    }
    @Test
    void getAllEquipments_ShouldReturnList() {
        when(equipmentRepository.findAll()).thenReturn(List.of(equipment));
        List<EquipmentResponse> responseList = equipmentService.getAllEquipments();
        assertFalse(responseList.isEmpty());
        assertEquals(1, responseList.size());
        assertEquals("Test Sensörlü Pompa", responseList.get(0).name());
    }
}
