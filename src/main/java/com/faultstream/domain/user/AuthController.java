package com.faultstream.domain.user;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.user.dto.AuthResponse;
import com.faultstream.domain.user.dto.LoginRequest;
import com.faultstream.domain.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Kullanıcı başarıyla kaydedildi"));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.success(response, "Giriş başarılı, İyi Çalışmalar Dilerim"));
    }
}
