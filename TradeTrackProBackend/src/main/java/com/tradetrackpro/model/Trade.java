package com.tradetrackpro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private BigDecimal entryPrice;

    private BigDecimal exitPrice;

    private BigDecimal profitLoss;

    @Column(length = 2000)
    private String notes;

    private LocalDate tradeDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;

    private String positionType; // BUY or SELL

    private String outcome;      // WIN, LOSS, or BREAKEVEN

    private Double tp;
    private Double sl;
    private String result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
