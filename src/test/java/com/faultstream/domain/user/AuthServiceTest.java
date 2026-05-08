package com.faultstream.domain.user;
import com.faultstream.common.exception.ConflictException;
import com.faultstream.common.exception.FeatureDisabledException;
import com.faultstream.config.AuthProperties;
import com.faultstream.domain.user.dto.AuthResponse;
import com.faultstream.domain.user.dto.RegisterRequest;
import com.faultstream.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthProperties authProperties;
    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldFail_WhenPublicRegistrationDisabled() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Demo User")
                .email("demo@faultstream.local")
                .password("secret123")
                .department("Ops")
                .build();
        when(authProperties.isPublicRegistrationEnabled()).thenReturn(false);

        assertThrows(FeatureDisabledException.class, () -> authService.register(request));
    }

    @Test
    void register_ShouldFail_WhenEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Demo User")
                .email("Demo@FaultStream.Local ")
                .password("secret123")
                .department("Ops")
                .build();
        when(authProperties.isPublicRegistrationEnabled()).thenReturn(true);
        when(userRepository.existsByEmail("demo@faultstream.local")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    @Test
    void register_ShouldNormalizeEmail_BeforePersisting() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Demo User")
                .email("Demo@FaultStream.Local ")
                .password("secret123")
                .department("Ops")
                .build();
        when(authProperties.isPublicRegistrationEnabled()).thenReturn(true);
        when(userRepository.existsByEmail("demo@faultstream.local")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("demo@faultstream.local", userCaptor.getValue().getEmail());
        assertEquals("jwt-token", response.getToken());
        verify(passwordEncoder).encode(eq("secret123"));
    }
}
