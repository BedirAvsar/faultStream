package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class DashboardPatternResponse {
    private String name;
    private long value;
    private String color;
}
