package com.faultstream.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /**
     * Secret key for signing JWT tokens.
     */
    private String secret;
    
    /**
     * JWT token expiration time in milliseconds.
     */
    private long expiration;
}
