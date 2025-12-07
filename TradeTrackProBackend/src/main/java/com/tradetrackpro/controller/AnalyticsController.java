package com.tradetrackpro.controller;

import com.tradetrackpro.dto.MonthlyStatsDTO;
import com.tradetrackpro.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/current-month")
    public ResponseEntity<MonthlyStatsDTO> getCurrentMonthStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        MonthlyStatsDTO stats = analyticsService.getMonthlyStats(userId);
        return ResponseEntity.ok(stats);
    }
}
