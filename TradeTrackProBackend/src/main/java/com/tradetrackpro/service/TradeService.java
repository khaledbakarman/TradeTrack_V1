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

        Trade trade = new Trade();
        trade.setUser(user);
        trade.setSymbol(request.getSymbol());
        trade.setEntryPrice(asBigDecimal(request.getEntryPrice()));
        trade.setExitPrice(asBigDecimal(request.getExitPrice()));
        BigDecimal pl = asBigDecimal(request.getProfitLoss());
        if (pl != null) {
            if ("LOSS".equalsIgnoreCase(request.getOutcome())) {
                pl = pl.abs().negate();
            } else if ("WIN".equalsIgnoreCase(request.getOutcome())) {
                pl = pl.abs();
            }
        }
        trade.setProfitLoss(pl);
        trade.setNotes(request.getNotes());
        trade.setTradeDate(request.getTradeDate());
        trade.setQuantity(asBigDecimal(request.getQuantity()));
        trade.setPositionType(request.getPositionType());
        trade.setOutcome(request.getOutcome());
        trade.setTp(request.getTp());
        trade.setSl(request.getSl());
        trade.setResult(request.getResult());

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

    // Returns the latest 4 trades for the recent trades widget
    public List<TradeResponse> getRecentTrades(Long userId) {
        return tradeRepository.findTop4ByUserIdOrderByTradeDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public org.springframework.data.domain.Page<Trade> getFilteredTrades(Long userId, String symbol, java.time.LocalDate startDate, java.time.LocalDate endDate, String result, org.springframework.data.domain.Pageable pageable) {
         return tradeRepository.filterTrades(userId, symbol, startDate, endDate, result, pageable);
    }

    public List<Trade> getFilteredTradesForExport(Long userId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return tradeRepository.filterTrades(userId, null, startDate, endDate, null, org.springframework.data.domain.Pageable.unpaged()).getContent();
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
        BigDecimal pl = asBigDecimal(request.getProfitLoss());
        if (pl != null) {
            if ("LOSS".equalsIgnoreCase(request.getOutcome())) {
                pl = pl.abs().negate();
            } else if ("WIN".equalsIgnoreCase(request.getOutcome())) {
                pl = pl.abs();
            }
        }
        existing.setProfitLoss(pl);
        existing.setNotes(request.getNotes());
        existing.setTradeDate(request.getTradeDate());
        existing.setQuantity(asBigDecimal(request.getQuantity()));
        existing.setPositionType(request.getPositionType());
        existing.setOutcome(request.getOutcome());
        existing.setTp(request.getTp());
        existing.setSl(request.getSl());
        existing.setResult(request.getResult());

        Trade updated = tradeRepository.save(existing);
        return toResponse(updated);
    }

    // Deletes a trade by id, ensuring it belongs to the specified user
    public void deleteTrade(Long tradeId, Long userId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found"));

        if (!trade.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this trade");
        }

        tradeRepository.delete(trade);
    }

    private TradeResponse toResponse(Trade trade) {
        return new TradeResponse(
                trade.getId(),
                trade.getUser().getId(),
                trade.getSymbol(),
                trade.getEntryPrice(),
                trade.getExitPrice(),
                trade.getProfitLoss(),
                trade.getNotes(),
                trade.getTradeDate(),
                trade.getQuantity(),
                trade.getPositionType(),
                trade.getOutcome(),
                trade.getTp(),
                trade.getSl(),
                trade.getResult()
        );
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
