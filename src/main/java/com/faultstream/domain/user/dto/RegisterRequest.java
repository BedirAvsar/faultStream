package com.faultstream.domain.user.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Ad soyad bos olamaz")
    private String fullName;
    @Email(message = "Gecerli bir e-posta adresi girin")
    @NotBlank(message = "E-posta bos olamaz")
    private String email;
    @NotBlank(message = "Sifre bos olamaz")
    @Size(min = 8, message = "Sifre en az 8 karakter olmali")
    private String password;
    private String department;
}
