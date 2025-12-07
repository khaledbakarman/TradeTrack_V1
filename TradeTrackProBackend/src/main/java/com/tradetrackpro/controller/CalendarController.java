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
        System.out.println("Calendar API: Fetching for userId=" + userId + " year=" + year + " month=" + month);
        
        if (userId == null) {
            System.out.println("Calendar API: UserId is NULL in request attribute!");
            return ResponseEntity.badRequest().build();
        }
        
        List<CalendarDayDto> calendarData = calendarService.getCalendarData(year, month, userId);
        System.out.println("Calendar API: Found " + calendarData.size() + " days with data.");
        return ResponseEntity.ok(calendarData);
    }
}
