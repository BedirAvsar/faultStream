package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class DashboardStatsResponse {
    private long activeNodes;
    private long recordedAnomalies;
    private double systemIntegrity;
    private String status;
}
