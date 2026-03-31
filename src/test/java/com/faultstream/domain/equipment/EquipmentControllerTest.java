/* ====================== NOTLAR ====================== */

/**
 * Amacım dışarıdan gelen HTTP isteklerini test etmek
 * Neden @WebMvcTest kullandım?
 * Çünkü sadece Controller katmanını test etmek istiyorum tüm projenin ayağa kalkmasını istemiyorum
 * Neden @AutoConfigureMockMvc kullandım?
 * Projemde JWT var eğer bunu yazmazsam SecurityConfig yüzünden 404 hatası alırım
 * Neden @MockitoBean kullandım?
 * Eski sürümlerde @MockBean kullanılıyormuş ancak Spring Boot 3.4 ile bu değişmiş tam olarak 21 Kasım 2024 tarihinde
 * 
 * Tüm bunları nereden biliyor ve neden açıklıyorum?
 * Zamanımın çoğusu bu kodları ve sistemleri araştırmakla geçiyor
 * Elbette 2026 yılında bu sektörün gerçeği olan yapay zekayı kullanıyorum
 * Ancak herşeyi yapay zekaya bırakmak yerine mantığını anlamaya çalışıyorum
 * Mimariyi kuruyor ve kararlar alıyorum nedenini sorguluyorum
 * Tüm kodları ellerimle yazıyorum (bunu gönderdiğim commitler arası sürelerden anlayabilirsiniz)
 * Aramızda bir Senior-Developer ilişkisi var
 * Aldığım tüm kararları ve araştırmalarımı not alıyorum
 * Saygılarımla Bedir Avşar :)
*/

package com.faultstream.domain.equipment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// MockMvc için gerekli kütüphaneler
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
// JWT Security ayarlarını testten hariç tutmak için addFilters = false ekledim
class EquipmentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    // JSON dönüşümleri için ObjectMapper'ı (POST isteği hazırlığı) getirdik.
    @Autowired
    private ObjectMapper objectMapper;

    // Controller içine koyduğum beyin kısmıydı ancak test olduğu sahtesini veriyorum
    @MockitoBean
    private EquipmentService equipmentService;

    // HATA BURADAYDI: Sınıf seviyesinde tanımlanmayı unutmuştu
    private EquipmentResponse equipmentResponse;

    // Hazırlık metodum
    @BeforeEach
    void setUp() {
        // Obje kurulumları
        equipmentResponse = EquipmentResponse.builder()
        .id(UUID.randomUUID())
        .name("Test Kompresörü")
        .type(EquipmentType.COMPRESSOR)
        .status(EquipmentStatus.ACTIVE)
        .location("A Blok")
        .build();
    }

    // İlk test: Tüm makineleri listeleme
    @Test
    void getAllEquipments_ShouldReturnList_WhenEquipmentsExist() throws Exception {
        
        when(equipmentService.getAllEquipments()).thenReturn(List.of(equipmentResponse));
    
        mockMvc.perform(get("/api/v1/equipments"))
               .andExpect(status().isOk()) // HTTP 200 OK Bekliyorum
               .andExpect(jsonPath("$.success").value(true)) // Bizim ApiResponse'un success alanı True mu?
               .andExpect(jsonPath("$.data[0].name").value("Test Kompresörü")) // İçindeki isme kadar kontrol!
               .andExpect(jsonPath("$.data[0].type").value("COMPRESSOR"));
    }
}
