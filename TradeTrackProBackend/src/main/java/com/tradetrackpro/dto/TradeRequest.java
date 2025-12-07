package com.tradetrackpro.dto;

import lombok.Data;

@Data
public class TradeRequest {

    private String symbol;
    private Double entryPrice;
    private Double exitPrice;
    private Double profitLoss;
    private String notes;
    private Long userId;  // sent from frontend
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate tradeDate;
    private Double quantity;
    private String positionType;
    private String outcome;
    private Double tp;
    private Double sl;
    private String result;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Double getEntryPrice() { return entryPrice; }
    public void setEntryPrice(Double entryPrice) { this.entryPrice = entryPrice; }

    public Double getExitPrice() { return exitPrice; }
    public void setExitPrice(Double exitPrice) { this.exitPrice = exitPrice; }

    public Double getProfitLoss() { return profitLoss; }
    public void setProfitLoss(Double profitLoss) { this.profitLoss = profitLoss; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public java.time.LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(java.time.LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

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
