package com.faultstream.domain.equipment;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.equipment.dto.CreateEquipmentRequest;
import com.faultstream.domain.equipment.dto.EquipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;
    @PostMapping
    public ResponseEntity<ApiResponse<EquipmentResponse>> createEquipment(
            @Valid @RequestBody CreateEquipmentRequest request) {
        EquipmentResponse response = equipmentService.createEquipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Ekipman başarıyla oluşturuldu"));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<EquipmentResponse>>> getAllEquipments() {
        List<EquipmentResponse> response = equipmentService.getAllEquipments();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Ekipmanlar başarıyla listelendi"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipmentResponse>> getEquipmentById(@PathVariable UUID id) {
        EquipmentResponse response = equipmentService.getEquipmentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response, "Ekipman başarıyla getirildi"));
    }
}
