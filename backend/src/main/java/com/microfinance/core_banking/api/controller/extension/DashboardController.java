package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> general() {
        return ResponseEntity.ok(dashboardService.buildGeneralDashboard());
    }
}
