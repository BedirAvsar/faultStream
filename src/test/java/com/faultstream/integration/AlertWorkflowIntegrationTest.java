package com.faultstream.integration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faultstream.domain.alert.Alert;
import com.faultstream.domain.alert.AlertRepository;
import com.faultstream.domain.equipment.Equipment;
import com.faultstream.domain.equipment.EquipmentRepository;
import com.faultstream.domain.equipment.EquipmentType;
import com.faultstream.domain.maintenance.MaintenanceLogRepository;
import com.faultstream.domain.sensor.Sensor;
import com.faultstream.domain.sensor.SensorReadingRepository;
import com.faultstream.domain.sensor.SensorRepository;
import com.faultstream.domain.sensor.SensorType;
import com.faultstream.domain.sensor.stream.SensorReadingEvent;
import com.faultstream.domain.sensor.stream.SensorReadingIngestionService;
import com.faultstream.domain.user.User;
import com.faultstream.domain.user.UserRepository;
import com.faultstream.domain.user.UserRole;
import com.faultstream.domain.workorder.WorkOrder;
import com.faultstream.domain.workorder.WorkOrderRepository;
import com.faultstream.domain.workorder.WorkOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Map;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AlertWorkflowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SensorReadingIngestionService ingestionService;
    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;
    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private SensorReadingRepository sensorReadingRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User engineer;
    private User technician;
    private Equipment equipment;
    private Sensor sensor;

    @BeforeEach
    void setUp() {
        maintenanceLogRepository.deleteAll();
        workOrderRepository.deleteAll();
        alertRepository.deleteAll();
        sensorReadingRepository.deleteAll();
        sensorRepository.deleteAll();
        equipmentRepository.deleteAll();
        userRepository.deleteAll();

        engineer = userRepository.save(User.builder()
                .email("engineer@test.local")
                .password(passwordEncoder.encode("password123"))
                .fullName("Integration Engineer")
                .role(UserRole.ENGINEER)
                .department("Reliability")
                .build());
        technician = userRepository.save(User.builder()
                .email("technician@test.local")
                .password(passwordEncoder.encode("password123"))
                .fullName("Integration Technician")
                .role(UserRole.TECHNICIAN)
                .department("Maintenance")
                .build());

        equipment = equipmentRepository.save(Equipment.builder()
                .name("TRB-01")
                .type(EquipmentType.COMPRESSOR)
                .location("Turbine Hall")
                .build());
        sensor = sensorRepository.save(Sensor.builder()
                .equipment(equipment)
                .name("Bearing Temp")
                .type(SensorType.TEMPERATURE)
                .unit("C")
                .thresholdMin(55.0)
                .thresholdMax(92.0)
                .build());
    }

    @Test
    void criticalReading_ShouldCreateAlertAndWorkOrderAndExposeEndpoints() throws Exception {
        ingestionService.ingest(SensorReadingEvent.builder()
                .sensorId(sensor.getId())
                .value(130.0)
                .recordedAt(LocalDateTime.now().toString())
                .source("integration-test")
                .build());

        assertEquals(1, alertRepository.count());
        assertEquals(1, workOrderRepository.count());

        Alert alert = alertRepository.findAll().get(0);
        WorkOrder workOrder = workOrderRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].severity", is("CRITICAL")));

        mockMvc.perform(get("/api/v1/work-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title").value(workOrder.getTitle()));

        mockMvc.perform(post("/api/v1/alerts/{id}/resolve", alert.getId())
                        .param("acknowledgedBy", engineer.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.acknowledged").value(true))
                .andExpect(jsonPath("$.data.acknowledgedById").value(engineer.getId().toString()));
    }

    @Test
    void maintenanceLogFlow_ShouldAssignCompleteAndLogWork() throws Exception {
        ingestionService.ingest(SensorReadingEvent.builder()
                .sensorId(sensor.getId())
                .value(129.0)
                .recordedAt(LocalDateTime.now().toString())
                .source("integration-test")
                .build());

        WorkOrder workOrder = workOrderRepository.findAll().get(0);

        mockMvc.perform(put("/api/v1/work-orders/{id}/assign", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("technicianId", technician.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.assignedToId").value(technician.getId().toString()));

        mockMvc.perform(put("/api/v1/work-orders/{id}/complete", workOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"));

        mockMvc.perform(post("/api/v1/maintenance-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "workOrderId", workOrder.getId(),
                                "technicianId", technician.getId(),
                                "actionTaken", "Replaced bearing and recalibrated motor",
                                "partsUsed", "Bearing kit",
                                "laborHours", 2.5,
                                "notes", "Equipment returned to nominal state"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.workOrderId").value(workOrder.getId().toString()))
                .andExpect(jsonPath("$.data.technicianId").value(technician.getId().toString()))
                .andExpect(jsonPath("$.data.actionTaken").value("Replaced bearing and recalibrated motor"));

        mockMvc.perform(get("/api/v1/maintenance-logs/work-order/{workOrderId}", workOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        WorkOrder updatedWorkOrder = workOrderRepository.findById(workOrder.getId()).orElseThrow();
        Equipment updatedEquipment = equipmentRepository.findById(equipment.getId()).orElseThrow();
        assertEquals(WorkOrderStatus.CLOSED, updatedWorkOrder.getStatus());
        assertNotNull(updatedEquipment.getLastMaintenanceDate());
        assertEquals(1, maintenanceLogRepository.count());
    }

    @Test
    void dashboardTerminal_ShouldReflectLiveReadings() throws Exception {
        ingestionService.ingest(SensorReadingEvent.builder()
                .sensorId(sensor.getId())
                .value(128.0)
                .recordedAt(LocalDateTime.now().toString())
                .source("integration-test")
                .build());

        mockMvc.perform(get("/api/v1/dashboard/terminal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stats.activeNodes").value(1))
                .andExpect(jsonPath("$.data.stats.recordedAnomalies").value(1))
                .andExpect(jsonPath("$.data.stream", hasSize(1)))
                .andExpect(jsonPath("$.data.stream[0].equipment").value("TRB-01"))
                .andExpect(jsonPath("$.data.stream[0].severity").value("CRIT"));
    }

    @Test
    void assignWorkOrder_ShouldRejectNonTechnicianUsers() throws Exception {
        ingestionService.ingest(SensorReadingEvent.builder()
                .sensorId(sensor.getId())
                .value(127.0)
                .recordedAt(LocalDateTime.now().toString())
                .source("integration-test")
                .build());

        WorkOrder workOrder = workOrderRepository.findAll().get(0);

        mockMvc.perform(put("/api/v1/work-orders/{id}/assign", workOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("technicianId", engineer.getId()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Atanan kullanici technician rolunde olmali"));
    }
}
