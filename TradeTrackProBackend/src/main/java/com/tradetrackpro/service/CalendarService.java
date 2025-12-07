package com.tradetrackpro.service;

import com.tradetrackpro.dto.CalendarDayDto;
import com.tradetrackpro.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final TradeRepository tradeRepository;

    public List<CalendarDayDto> getCalendarData(int year, int month, Long userId) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return tradeRepository.getCalendarDataPerDay(start, end, userId);
    }
}
