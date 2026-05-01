package com.dev.petmarket_backend.login.controller;

import com.dev.petmarket_backend.common.dto.ErrorResponse;
import com.dev.petmarket_backend.common.exception.RateLimitExceededException;
import com.dev.petmarket_backend.common.util.EmailBasedRateLimiter;
import com.dev.petmarket_backend.login.dto.LoginRequest;
import com.dev.petmarket_backend.login.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/auth", "/auth"})
public class LoginController {

    private final LoginService loginService;
    private final EmailBasedRateLimiter rateLimiter;

    public LoginController(LoginService loginService, EmailBasedRateLimiter rateLimiter) {
        this.loginService = loginService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Check rate limit
            if (!rateLimiter.isAllowed(request.getEmail())) {
                long secondsUntilReset = rateLimiter.getSecondsUntilReset(request.getEmail());
                throw new RateLimitExceededException(secondsUntilReset);
            }

            // Attempt login
            var response = loginService.login(request);

            // Reset rate limit on successful login
            rateLimiter.reset(request.getEmail());

            return ResponseEntity.ok(response);
        } catch (RateLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
