package com.faultstream.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "faultstream.demo")
public class DemoDataProperties {
    private boolean seedDataEnabled;
}
