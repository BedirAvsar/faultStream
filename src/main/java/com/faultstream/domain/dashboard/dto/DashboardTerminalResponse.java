package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
import java.util.List;
@Data
@Builder
public class DashboardTerminalResponse {
    private DashboardStatsResponse stats;
    private List<DashboardEventResponse> stream;
    private List<DashboardPatternResponse> patternAnalysis;
    private List<DashboardFrequencyResponse> frequencyDensity;
}
