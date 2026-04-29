package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
@Data
@Builder
public class DashboardEventResponse implements Serializable {
    private String id;
    private String time;
    private String equipment;
    private String faultCode;
    private String severity;
    private String color;
    private double value;
    private String unit;
}
