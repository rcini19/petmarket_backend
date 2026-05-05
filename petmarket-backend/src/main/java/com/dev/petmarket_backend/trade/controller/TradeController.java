package com.dev.petmarket_backend.trade.controller;

import com.dev.petmarket_backend.common.dto.ErrorResponse;
import com.dev.petmarket_backend.trade.dto.TradeRequest;
import com.dev.petmarket_backend.trade.dto.TradeResponse;
import com.dev.petmarket_backend.trade.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/trades", "/trades"})
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<?> createTrade(Authentication authentication,
                                         @Valid @RequestBody TradeRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(tradeService.createTrade(authentication.getName(), request));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptTrade(Authentication authentication, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(tradeService.acceptTrade(authentication.getName(), id));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectTrade(Authentication authentication, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(tradeService.rejectTrade(authentication.getName(), id));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getTrades(Authentication authentication) {
        try {
            return ResponseEntity.ok(tradeService.getTrades(authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
