package com.habitalis.controller;

import com.habitalis.dto.stats.DashboardStatsResponse;
import com.habitalis.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> obtenerDashboard(Principal principal) {
        return ResponseEntity.ok(statsService.obtenerResumen(principal.getName()));
    }
}