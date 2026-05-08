package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
@Data
@Builder
public class DashboardStatsResponse implements Serializable {
    private long activeNodes;
    private long recordedAnomalies;
    private double systemIntegrity;
    private String status;
}
