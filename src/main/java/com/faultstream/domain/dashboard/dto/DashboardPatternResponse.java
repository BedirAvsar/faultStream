package com.faultstream.domain.dashboard.dto;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
@Data
@Builder
public class DashboardPatternResponse implements Serializable {
    private String name;
    private long value;
    private String color;
}
