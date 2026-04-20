package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class DashboardEventResponse {
    private String id;
    private String time;
    private String equipment;
    private String faultCode;
    private String severity;
    private String color;
    private double value;
    private String unit;
}
