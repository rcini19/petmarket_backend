package com.dev.petmarket_backend.common.exception;

public class RateLimitExceededException extends RuntimeException {
    private final long secondsUntilReset;

    public RateLimitExceededException(long secondsUntilReset) {
        super(String.format("Too many login attempts. Please try again in %d seconds.", secondsUntilReset));
        this.secondsUntilReset = secondsUntilReset;
    }

    public long getSecondsUntilReset() {
        return secondsUntilReset;
    }
}
