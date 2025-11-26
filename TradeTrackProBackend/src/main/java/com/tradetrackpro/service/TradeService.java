package com.tradetrackpro.service;

import com.tradetrackpro.dto.TradeRequest;
import com.tradetrackpro.dto.TradeResponse;
import com.tradetrackpro.model.Trade;
import com.tradetrackpro.model.User;
import com.tradetrackpro.repository.TradeRepository;
import com.tradetrackpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;

    // Creates a new trade for the given user and returns the saved record as a response DTO
    public TradeResponse createTrade(TradeRequest request) {
        validateRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Trade trade = Trade.builder()
                .user(user)
                .symbol(request.getSymbol())
                .entryPrice(asBigDecimal(request.getEntryPrice()))
                .exitPrice(asBigDecimal(request.getExitPrice()))
                .profitLoss(asBigDecimal(request.getProfitLoss()))
                .notes(request.getNotes())
                .build();

        Trade saved = tradeRepository.save(trade);
        return toResponse(saved);
    }

    // Returns all trades that belong to the supplied user id
    public List<TradeResponse> getTradesByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return tradeRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Returns a single trade by id or throws if it does not exist
    public TradeResponse getTradeById(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found"));
        return toResponse(trade);
    }

    // Updates an existing trade using the supplied request payload
    public TradeResponse updateTrade(Long tradeId, TradeRequest request) {
        validateRequest(request);

        Trade existing = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existing.setUser(user);
        existing.setSymbol(request.getSymbol());
        existing.setEntryPrice(asBigDecimal(request.getEntryPrice()));
        existing.setExitPrice(asBigDecimal(request.getExitPrice()));
        existing.setProfitLoss(asBigDecimal(request.getProfitLoss()));
        existing.setNotes(request.getNotes());

        Trade updated = tradeRepository.save(existing);
        return toResponse(updated);
    }

    // Deletes a trade by id; no response body needed
    public void deleteTrade(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found"));
        tradeRepository.delete(trade);
    }

    private TradeResponse toResponse(Trade trade) {
        return TradeResponse.builder()
                .id(trade.getId())
                .userId(trade.getUser().getId())
                .symbol(trade.getSymbol())
                .entryPrice(trade.getEntryPrice())
                .exitPrice(trade.getExitPrice())
                .profitLoss(trade.getProfitLoss())
                .notes(trade.getNotes())
                .tradeDate(trade.getTradeDate())
                .build();
    }

    private void validateRequest(TradeRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (request.getSymbol() == null || request.getSymbol().isBlank()) {
            throw new IllegalArgumentException("symbol is required");
        }
        if (request.getEntryPrice() == null || request.getEntryPrice() <= 0) {
            throw new IllegalArgumentException("entryPrice must be greater than 0");
        }
    }

    private BigDecimal asBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
}
