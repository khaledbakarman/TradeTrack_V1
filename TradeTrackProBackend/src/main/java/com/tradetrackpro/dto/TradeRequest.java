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
    private java.time.LocalDate tradeDate;
    private Double quantity;
    private String positionType;
    private String outcome;

}
