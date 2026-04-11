package com.faultstream.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {
    private boolean enabled;
    private long intervalMs;
}
