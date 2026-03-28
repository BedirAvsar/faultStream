package com.faultstream.security;

import com.faultstream.common.exception.ResourceNotFoundException;
import com.faultstream.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Şimdi @RequiredArgsConstructor ı ve UserReposity i alacağım ve Constructor a montajlayacağım
// Enjekte etmek terimi daha doğru ancak Makinacı genlerim ağır basıyor :D
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // Şimdii amacım şu eposta geldiğinde onu durdurup "Hey bu epostaya sahip biri
    // var mı?" sorusunu sormak istiyorum
    // İşte o muhteşem method bu kaynakça ile
    // (https://stackoverflow.com/questions/59417122/how-to-handle-usernamenotfoundexception-spring-security)
    // @Override
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Artık postgres'e sorgu gönderebiliyorum
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sistemde böyle bir eposta adresine sahip kullanıcı bulunamadı: " + email));
    }
}