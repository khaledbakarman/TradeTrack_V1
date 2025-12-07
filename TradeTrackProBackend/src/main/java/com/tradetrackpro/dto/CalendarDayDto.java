package com.tradetrackpro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CalendarDayDto {
    private LocalDate date;
    private BigDecimal pnl;
    private Long wins; // Count returns Long in JPQL
    private Long losses; // Count returns Long in JPQL

    // Constructor matching JPQL
    public CalendarDayDto(LocalDate date, BigDecimal pnl, Long wins, Long losses) {
        this.date = date;
        this.pnl = pnl;
        this.wins = wins != null ? wins : 0L;
        this.losses = losses != null ? losses : 0L;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getPnl() {
        return pnl != null ? pnl : BigDecimal.ZERO;
    }

    public int getWins() {
        return wins.intValue();
    }

    public int getLosses() {
        return losses.intValue();
    }
}
