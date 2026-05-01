package com.dev.petmarket_backend.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmailBasedRateLimiter {
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SIZE_MS = 60_000; // 1 minute
    private static final long CLEANUP_INTERVAL_MS = 120_000; // 2 minutes

    private final Map<String, AttemptTracker> attemptMap = new ConcurrentHashMap<>();
    private volatile long lastCleanup = System.currentTimeMillis();

    /**
     * Check if an email has exceeded rate limit
     * @param email The user's email (will be normalized to lowercase)
     * @return true if within limit, false if limit exceeded
     */
    public boolean isAllowed(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        cleanup();

        long now = System.currentTimeMillis();
        AttemptTracker tracker = attemptMap.computeIfAbsent(normalizedEmail, k -> new AttemptTracker());

        // If window has expired, reset
        if (now - tracker.windowStartTime > WINDOW_SIZE_MS) {
            tracker.windowStartTime = now;
            tracker.attemptCount = 0;
        }

        // Increment and check
        tracker.attemptCount++;
        return tracker.attemptCount <= MAX_ATTEMPTS;
    }

    /**
     * Get remaining attempts for an email
     * @param email The user's email
     * @return Remaining attempts (0 if limit exceeded)
     */
    public int getRemainingAttempts(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        long now = System.currentTimeMillis();

        AttemptTracker tracker = attemptMap.get(normalizedEmail);
        if (tracker == null) {
            return MAX_ATTEMPTS;
        }

        // If window expired, full attempts available
        if (now - tracker.windowStartTime > WINDOW_SIZE_MS) {
            return MAX_ATTEMPTS;
        }

        int remaining = MAX_ATTEMPTS - tracker.attemptCount;
        return Math.max(0, remaining);
    }

    /**
     * Get time until rate limit resets (in seconds)
     * @param email The user's email
     * @return Seconds until reset, or 0 if not rate limited
     */
    public long getSecondsUntilReset(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        AttemptTracker tracker = attemptMap.get(normalizedEmail);

        if (tracker == null || tracker.attemptCount <= MAX_ATTEMPTS) {
            return 0;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - tracker.windowStartTime;
        long remaining = WINDOW_SIZE_MS - elapsed;

        if (remaining <= 0) {
            return 0;
        }

        return remaining / 1000; // Convert to seconds
    }

    /**
     * Reset attempts for an email (useful for successful login)
     * @param email The user's email
     */
    public void reset(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        attemptMap.remove(normalizedEmail);
    }

    /**
     * Cleanup old entries to prevent memory leaks
     */
    private void cleanup() {
        long now = System.currentTimeMillis();
        if (now - lastCleanup < CLEANUP_INTERVAL_MS) {
            return;
        }

        attemptMap.entrySet().removeIf(entry -> {
            long age = now - entry.getValue().windowStartTime;
            return age > CLEANUP_INTERVAL_MS;
        });

        lastCleanup = now;
    }

    /**
     * Internal tracker for attempt counts
     */
    private static class AttemptTracker {
        volatile int attemptCount = 0;
        volatile long windowStartTime = System.currentTimeMillis();
    }
}
