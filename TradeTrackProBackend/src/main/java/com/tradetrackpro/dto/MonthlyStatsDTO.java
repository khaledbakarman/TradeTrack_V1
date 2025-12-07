package com.tradetrackpro.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class MonthlyStatsDTO {
    private int totalTrades;
    private double winRate;
    private BigDecimal netProfit;
    private BigDecimal avgWin;
    private BigDecimal avgLoss;
    private BigDecimal bestTrade;
    private BigDecimal worstTrade;

    public MonthlyStatsDTO() {}

    public MonthlyStatsDTO(int totalTrades, double winRate, BigDecimal netProfit, BigDecimal avgWin, BigDecimal avgLoss, BigDecimal bestTrade, BigDecimal worstTrade) {
        this.totalTrades = totalTrades;
        this.winRate = winRate;
        this.netProfit = netProfit;
        this.avgWin = avgWin;
        this.avgLoss = avgLoss;
        this.bestTrade = bestTrade;
        this.worstTrade = worstTrade;
    }

    public int getTotalTrades() { return totalTrades; }
    public void setTotalTrades(int totalTrades) { this.totalTrades = totalTrades; }
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
    public BigDecimal getAvgWin() { return avgWin; }
    public void setAvgWin(BigDecimal avgWin) { this.avgWin = avgWin; }
    public BigDecimal getAvgLoss() { return avgLoss; }
    public void setAvgLoss(BigDecimal avgLoss) { this.avgLoss = avgLoss; }
    public BigDecimal getBestTrade() { return bestTrade; }
    public void setBestTrade(BigDecimal bestTrade) { this.bestTrade = bestTrade; }
    public BigDecimal getWorstTrade() { return worstTrade; }
    public void setWorstTrade(BigDecimal worstTrade) { this.worstTrade = worstTrade; }
}
