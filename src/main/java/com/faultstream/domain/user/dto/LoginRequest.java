package com.faultstream.domain.user.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Email(message = "Gecerli bir e-posta adresi girin")
    @NotBlank(message = "E-posta bos olamaz")
    private String email;
    @NotBlank(message = "Sifre bos olamaz")
    private String password;
}
