package com.tradetrackpro.dto;

import java.math.BigDecimal;
import java.util.Map;

public class WeeklyAnalyticsResponse {
    private Map<String, BigDecimal> profitByDay;

    public WeeklyAnalyticsResponse() {}

    public WeeklyAnalyticsResponse(Map<String, BigDecimal> profitByDay) {
        this.profitByDay = profitByDay;
    }

    public Map<String, BigDecimal> getProfitByDay() {
        return profitByDay;
    }

    public void setProfitByDay(Map<String, BigDecimal> profitByDay) {
        this.profitByDay = profitByDay;
    }
}
