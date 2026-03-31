package com.faultstream.domain.user;

import com.faultstream.domain.user.dto.AuthResponse;
import com.faultstream.domain.user.dto.RegisterRequest;
import com.faultstream.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // REGISTER ENGINE
    public AuthResponse register(RegisterRequest request) {
        // Elimdeki şifreyi kriptoluyorum
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Yeni kullanıcı oluşturuyorum
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(UserRole.ENGINEER)
                .department(request.getDepartment())
                .build();

        userRepository.save(user);

        // Token üretiyorum
        String token = jwtService.generateToken(user);

        return AuthResponse.builder().token(token).build();
    } // register metodunun sonu

    // LOGIN BUTTON
    public AuthResponse authenticate(String email, String password) {
        // Arka planda şifrenin doğruluğunu kontrol etsin
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        // Eğer hata yoksa kullanıcı valid
        User user = userRepository.findByEmail(email).orElseThrow();
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }
}
