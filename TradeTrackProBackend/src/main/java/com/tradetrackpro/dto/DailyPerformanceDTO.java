package com.tradetrackpro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyPerformanceDTO {
    private LocalDate date;
    private BigDecimal pnl;
    private int wins;
    private int losses;
    private int totalTrades;

    public DailyPerformanceDTO() {}

    public DailyPerformanceDTO(LocalDate date, BigDecimal pnl, int wins, int losses, int totalTrades) {
        this.date = date;
        this.pnl = pnl;
        this.wins = wins;
        this.losses = losses;
        this.totalTrades = totalTrades;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getPnl() { return pnl; }
    public void setPnl(BigDecimal pnl) { this.pnl = pnl; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
    public int getTotalTrades() { return totalTrades; }
    public void setTotalTrades(int totalTrades) { this.totalTrades = totalTrades; }
}
