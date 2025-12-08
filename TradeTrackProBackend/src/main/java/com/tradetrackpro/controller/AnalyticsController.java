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

    @GetMapping("/weekly")
    public ResponseEntity<com.tradetrackpro.dto.WeeklyAnalyticsResponse> getWeeklyAnalytics(
            HttpServletRequest request,
            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        Long userId = (Long) request.getAttribute("userId");
        com.tradetrackpro.dto.WeeklyAnalyticsResponse response = analyticsService.getWeeklyAnalytics(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly-debug")
    public ResponseEntity<com.tradetrackpro.dto.WeeklyAnalyticsResponse> getWeeklyAnalyticsDebug(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        java.time.LocalDate start = java.time.LocalDate.now().with(java.time.DayOfWeek.SUNDAY);
        java.time.LocalDate end = start.plusDays(6);
        com.tradetrackpro.dto.WeeklyAnalyticsResponse response = analyticsService.getWeeklyAnalytics(userId, start, end);
        return ResponseEntity.ok(response);
    }
}
