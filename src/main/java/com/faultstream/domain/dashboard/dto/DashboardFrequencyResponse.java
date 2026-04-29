package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
@Data
@Builder
public class DashboardFrequencyResponse implements Serializable {
    private String name;
    private long faults;
}
