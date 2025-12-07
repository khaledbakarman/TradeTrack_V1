package com.tradetrackpro.service;

import com.tradetrackpro.dto.MonthlyStatsDTO;
import com.tradetrackpro.model.Trade;
import com.tradetrackpro.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TradeRepository tradeRepository;

    public MonthlyStatsDTO getMonthlyStats(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<Trade> trades = tradeRepository.findAllByUserIdAndTradeDateBetween(userId, startDate, endDate);

        if (trades.isEmpty()) {
            return new MonthlyStatsDTO(0, 0.0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        int totalTrades = trades.size();
        
        long wins = trades.stream()
                .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) > 0)
                .count();

        double winRate = (double) wins / totalTrades * 100;

        BigDecimal netProfit = trades.stream()
                .map(t -> t.getProfitLoss() != null ? t.getProfitLoss() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Avg Win
        List<BigDecimal> winningTrades = trades.stream()
                .map(Trade::getProfitLoss)
                .filter(pl -> pl != null && pl.compareTo(BigDecimal.ZERO) > 0)
                .toList();
        
        BigDecimal avgWin = winningTrades.isEmpty() ? BigDecimal.ZERO : 
                winningTrades.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(winningTrades.size()), 2, RoundingMode.HALF_UP);

        // Avg Loss
        List<BigDecimal> losingTrades = trades.stream()
                .map(Trade::getProfitLoss)
                .filter(pl -> pl != null && pl.compareTo(BigDecimal.ZERO) < 0)
                .toList();

        BigDecimal avgLoss = losingTrades.isEmpty() ? BigDecimal.ZERO : 
                losingTrades.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(losingTrades.size()), 2, RoundingMode.HALF_UP);

        // Best Trade
        BigDecimal bestTrade = trades.stream()
                .map(t -> t.getProfitLoss() != null ? t.getProfitLoss() : BigDecimal.ZERO)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        // Worst Trade
        BigDecimal worstTrade = trades.stream()
                .map(t -> t.getProfitLoss() != null ? t.getProfitLoss() : BigDecimal.ZERO)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        return new MonthlyStatsDTO(
                totalTrades,
                winRate,
                netProfit,
                avgWin,
                avgLoss,
                bestTrade,
                worstTrade
        );
    }

    public List<com.tradetrackpro.dto.DailyPerformanceDTO> getMonthlyPerformance(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Trade> trades = tradeRepository.findAllByUserIdAndTradeDateBetween(userId, startDate, endDate);

        return trades.stream()
                .filter(t -> t.getTradeDate() != null)
                .collect(java.util.stream.Collectors.groupingBy(Trade::getTradeDate))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Trade> dailyTrades = entry.getValue();

                    BigDecimal pnl = dailyTrades.stream()
                            .map(t -> t.getProfitLoss() != null ? t.getProfitLoss() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    int wins = (int) dailyTrades.stream()
                            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) > 0)
                            .count();

                    int losses = (int) dailyTrades.stream()
                            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) < 0)
                            .count();

                    return new com.tradetrackpro.dto.DailyPerformanceDTO(
                            date, pnl, wins, losses, dailyTrades.size()
                    );
                })
                .sorted(Comparator.comparing(com.tradetrackpro.dto.DailyPerformanceDTO::getDate))
                .toList();
    }
}
