package com.faultstream.domain.dashboard;
import com.faultstream.common.response.ApiResponse;
import com.faultstream.domain.dashboard.dto.DashboardTerminalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/terminal")
    public ResponseEntity<ApiResponse<DashboardTerminalResponse>> getTerminalSnapshot() {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getTerminalSnapshot(),
                "Dashboard terminal verileri getirildi"));
    }
}
