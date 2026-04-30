package com.faultstream.domain.equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.faultstream.security.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.faultstream.security.JwtAuthFilter;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.faultstream.domain.equipment.dto.CreateEquipmentRequest;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faultstream.domain.equipment.dto.EquipmentResponse;
import java.util.List;
import java.util.UUID;
@WebMvcTest(controllers = EquipmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class EquipmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private EquipmentService equipmentService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;
    private EquipmentResponse equipmentResponse;
    @BeforeEach
    void setUp() {
        equipmentResponse = EquipmentResponse.builder()
                .id(UUID.randomUUID())
                .name("Test Kompresörü")
                .type(EquipmentType.COMPRESSOR)
                .status(EquipmentStatus.ACTIVE)
                .location("A Blok")
                .build();
    }
    @Test
    void getAllEquipments_ShouldReturnList_WhenEquipmentsExist() throws Exception {
        when(equipmentService.getAllEquipments()).thenReturn(List.of(equipmentResponse));
        mockMvc.perform(get("/api/v1/equipments"))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.success").value(true)) 
                .andExpect(jsonPath("$.data[0].name").value("Test Kompresörü")) 
                .andExpect(jsonPath("$.data[0].type").value("COMPRESSOR"));
    }
    @Test
    void createEquipment_ShouldReturn201_WhenValidRequest() throws Exception {
        CreateEquipmentRequest request = new CreateEquipmentRequest(
            "Yeni Pompa",
            EquipmentType.PUMP,
            "C Blok",
            null, null, null, null, null
        );
        when(equipmentService.createEquipment(any(CreateEquipmentRequest.class))).thenReturn(equipmentResponse);
        mockMvc.perform(post("/api/v1/equipments")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}
