package com.tradetrackpro.controller;

import com.tradetrackpro.dto.TradeRequest;
import com.tradetrackpro.dto.TradeResponse;
import com.tradetrackpro.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import com.tradetrackpro.model.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // Handles POST /api/trades to create a new trade linked to a user
    @PostMapping
    public ResponseEntity<?> createTrade(@Valid @RequestBody TradeRequest request, HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");
            if (userId != null) {
                 request.setUserId(userId);
            }
            TradeResponse response = tradeService.createTrade(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }



    // Handles GET /api/trades to return all trades for the authenticated user
    @GetMapping
    public ResponseEntity<?> getTradesByUser(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("getTradesByUser - userId: " + userId);
            
            List<TradeResponse> trades = tradeService.getTradesByUser(userId);
            System.out.println("Found trades: " + trades.size());
            return ResponseEntity.ok(trades);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Handles GET /api/trades/{id} to fetch a single trade by its id
    @GetMapping("/{id}")
    public ResponseEntity<?> getTradeById(@PathVariable Long id) {
        try {
            TradeResponse response = tradeService.getTradeById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Handles PUT /api/trades/{id} to update an existing trade
    // Handles PUT /api/trades/{id} to update an existing trade
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrade(@PathVariable Long id,
                                         @Valid @RequestBody TradeRequest request,
                                         HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");
            if (userId != null) {
                request.setUserId(userId);
            }
            TradeResponse response = tradeService.updateTrade(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Handles DELETE /api/trades/{id} to remove a trade permanently
    // Handles DELETE /api/trades/{id} to remove a trade permanently
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrade(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("Deleting trade " + id + " for user " + userId);
            
            tradeService.deleteTrade(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
