package com.faultstream.domain.user;
import com.faultstream.common.exception.ConflictException;
import com.faultstream.common.exception.FeatureDisabledException;
import com.faultstream.config.AuthProperties;
import com.faultstream.domain.user.dto.AuthResponse;
import com.faultstream.domain.user.dto.RegisterRequest;
import com.faultstream.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthProperties authProperties;

    public AuthResponse register(RegisterRequest request) {
        if (!authProperties.isPublicRegistrationEnabled()) {
            throw new FeatureDisabledException("Public registration is disabled");
        }

        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Bu e-posta adresi zaten kayitli");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .fullName(request.getFullName())
                .email(normalizedEmail)
                .password(encodedPassword)
                .role(UserRole.ENGINEER)
                .department(request.getDepartment())
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, password));
        User user = userRepository.findByEmail(normalizedEmail).orElseThrow();
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
