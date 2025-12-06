package com.tradetrackpro.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class TradeResponse {
    Long id;
    Long userId;
    String symbol;
    BigDecimal entryPrice;
    BigDecimal exitPrice;
    BigDecimal profitLoss;
    String notes;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate tradeDate;
    BigDecimal quantity;
    String positionType;
    String outcome;
    Double tp;
    Double sl;
    String result;
}
