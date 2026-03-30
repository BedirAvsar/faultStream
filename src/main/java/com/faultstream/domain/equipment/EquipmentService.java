package com.faultstream.domain.equipment;

import com.faultstream.domain.equipment.dto.CreateEquipmentRequest;
import com.faultstream.domain.equipment.dto.EquipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentRequest request) {
        Equipment equipment = Equipment.builder()
                .name(request.getName())
                .type(request.getType())
                .location(request.getLocation())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .serialNumber(request.getSerialNumber())
                .build();

        Equipment savedEquipment = equipmentRepository.save(equipment);
        return mapToResponse(savedEquipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllEquipments() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(UUID id) {
        return equipmentRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Ekipman bulunamadı!"));
    }

    private EquipmentResponse mapToResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .type(equipment.getType())
                .location(equipment.getLocation())
                .serialNumber(equipment.getSerialNumber())
                .status(equipment.getStatus())
                .build();
    }
}
