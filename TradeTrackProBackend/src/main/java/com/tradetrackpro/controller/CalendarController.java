package com.tradetrackpro.controller;

import com.tradetrackpro.dto.CalendarDayDto;
import com.tradetrackpro.service.CalendarService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/month")
    public ResponseEntity<List<CalendarDayDto>> getMonthlyCalendar(
            HttpServletRequest request,
            @RequestParam int year,
            @RequestParam int month) {
        Long userId = (Long) request.getAttribute("userId");
        List<CalendarDayDto> calendarData = calendarService.getCalendarData(year, month, userId);
        return ResponseEntity.ok(calendarData);
    }
}
