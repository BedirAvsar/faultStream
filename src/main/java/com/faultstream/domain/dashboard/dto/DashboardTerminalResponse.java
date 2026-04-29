package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
@Data
@Builder
public class DashboardTerminalResponse implements Serializable {
    private DashboardStatsResponse stats;
    private List<DashboardEventResponse> stream;
    private List<DashboardPatternResponse> patternAnalysis;
    private List<DashboardFrequencyResponse> frequencyDensity;
}
