package com.tradetrackpro.repository;

import com.tradetrackpro.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserId(Long userId);
    List<Trade> findAllByUserIdAndTradeDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT t FROM Trade t 
        WHERE t.user.id = :userId
          AND (:symbol IS NULL OR t.symbol = :symbol)
          AND (:startDate IS NULL OR t.tradeDate >= :startDate)
          AND (:endDate IS NULL OR t.tradeDate <= :endDate)
          AND (:result IS NULL OR 
                (:result = 'WIN' AND t.profitLoss > 0) OR
                (:result = 'LOSS' AND t.profitLoss < 0))
    """)
    Page<Trade> filterTrades(
            @Param("userId") Long userId,
            @Param("symbol") String symbol,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("result") String result,
            Pageable pageable
    );

    @Query(value = """
        SELECT new com.tradetrackpro.dto.CalendarDayDto(
            t.tradeDate,
            SUM(t.profitLoss),
            SUM(CASE WHEN t.outcome = 'WIN' THEN 1 ELSE 0 END),
            SUM(CASE WHEN t.outcome = 'LOSS' THEN 1 ELSE 0 END)
        )
        FROM Trade t
        WHERE t.user.id = :userId 
        AND t.tradeDate BETWEEN :start AND :end
        GROUP BY t.tradeDate
        ORDER BY t.tradeDate
        """)
    List<com.tradetrackpro.dto.CalendarDayDto> getCalendarDataPerDay(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("userId") Long userId
    );
}
