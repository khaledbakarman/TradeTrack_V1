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

    @Query("""
        SELECT t FROM Trade t 
        WHERE t.user.id = :userId
          AND (:symbol IS NULL OR t.symbol = :symbol)
          AND (:startDate IS NULL OR t.tradeDate >= :startDate)
          AND (:endDate IS NULL OR t.tradeDate <= :endDate)
          AND (:result IS NULL OR 
                (:result = 'win' AND t.profitLoss > 0) OR
                (:result = 'loss' AND t.profitLoss < 0))
    """)
    Page<Trade> filterTrades(
            @Param("userId") Long userId,
            @Param("symbol") String symbol,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("result") String result,
            Pageable pageable);
}
