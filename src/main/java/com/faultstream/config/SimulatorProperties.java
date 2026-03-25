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

    /**
     * Whether the simulator is enabled or not.
     */
    private boolean enabled;

    /**
     * The interval in milliseconds between simulator events.
     */
    private long intervalMs;
}
