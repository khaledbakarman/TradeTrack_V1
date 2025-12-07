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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getEntryPrice() { return entryPrice; }
    public void setEntryPrice(BigDecimal entryPrice) { this.entryPrice = entryPrice; }

    public BigDecimal getExitPrice() { return exitPrice; }
    public void setExitPrice(BigDecimal exitPrice) { this.exitPrice = exitPrice; }

    public BigDecimal getProfitLoss() { return profitLoss; }
    public void setProfitLoss(BigDecimal profitLoss) { this.profitLoss = profitLoss; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getPositionType() { return positionType; }
    public void setPositionType(String positionType) { this.positionType = positionType; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public Double getTp() { return tp; }
    public void setTp(Double tp) { this.tp = tp; }

    public Double getSl() { return sl; }
    public void setSl(Double sl) { this.sl = sl; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
