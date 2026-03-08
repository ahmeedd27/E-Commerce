package com.ahmed.E_CommerceApp.Config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component

public class LoginRateLimiter {

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> confirmBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    // ─── Login: 5 attempts, refill 1 per minute ───────────────────
    public boolean isLoginAllowed(String ip) {
        return loginBuckets
                .computeIfAbsent(ip, k -> buildBucket(5, 1, Duration.ofMinutes(1)))
                .tryConsume(1);
    }

    // ─── Register: 3 attempts, refill 1 per hour ──────────────────
    // Stricter — no reason to create many accounts quickly
    public boolean isRegisterAllowed(String ip) {
        return registerBuckets
                .computeIfAbsent(ip, k -> buildBucket(3, 1, Duration.ofHours(1)))
                .tryConsume(1);
    }

    // ─── Confirm email: 5 attempts, refill 1 per 10 minutes ───────
    // Strict — 6-digit code is brute-forceable without this
    public boolean isConfirmEmailAllowed(String ip) {
        return confirmBuckets
                .computeIfAbsent(ip, k -> buildBucket(5, 1, Duration.ofMinutes(10)))
                .tryConsume(1);
    }

    // ─── Shared builder ───────────────────────────────────────────
    private Bucket buildBucket(int capacity, int refillAmount, Duration refillPeriod) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(refillAmount, refillPeriod)
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // ─── IP extraction ────────────────────────────────────────────
    public String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}