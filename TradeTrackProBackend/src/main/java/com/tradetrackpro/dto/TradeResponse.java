package com.tradetrackpro.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TradeResponse {
    private Long id;
    private Long userId;
    private String symbol;
    private BigDecimal entryPrice;
    private BigDecimal exitPrice;
    private BigDecimal profitLoss;
    private String notes;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tradeDate;
    private BigDecimal quantity;
    private String positionType;
    private String outcome;
    private Double tp;
    private Double sl;
    private String result;

    public TradeResponse() {}

    public TradeResponse(Long id, Long userId, String symbol, BigDecimal entryPrice, BigDecimal exitPrice, BigDecimal profitLoss, String notes, LocalDate tradeDate, BigDecimal quantity, String positionType, String outcome, Double tp, Double sl, String result) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.profitLoss = profitLoss;
        this.notes = notes;
        this.tradeDate = tradeDate;
        this.quantity = quantity;
        this.positionType = positionType;
        this.outcome = outcome;
        this.tp = tp;
        this.sl = sl;
        this.result = result;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
}
