package com.faultstream.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Lombok sayesinde getter, setter, constructor, toString gibi metotları otomatik oluşturuyorum
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    // T (generic) kullanma sebebim her türlü veriyi kabul etmesi

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Statik yardımcı metod ekliyorum çünkü işlem ameleliği yapmak istemiyorum :)
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // Frontend tarafında hata durumlarında kullanmak için
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}

/*
 * REST API bu tarz bir cevap istiyorum:
 * {
 * "success": true,
 * "message": "Ekipman başarıyla oluşturuldu.",
 * "data": { "id": "123", "name": "Kompresör" },
 * "timestamp": "2026-03-25T15:30:00"
 * }
 */