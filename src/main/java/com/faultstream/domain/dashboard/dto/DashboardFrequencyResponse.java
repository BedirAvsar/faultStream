package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class DashboardFrequencyResponse {
    private String name;
    private long faults;
}
