package com.microfinance.core_banking.config;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtTokenBlacklistService {

    private final JwtService jwtService;
    private final Map<String, Instant> revokedTokensByJti = new ConcurrentHashMap<>();

    public JwtTokenBlacklistService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void blacklist(String token) {
        String jti = jwtService.extractJti(token);
        Instant expiration = jwtService.extractExpiration(token).toInstant();
        revokedTokensByJti.put(jti, expiration);
        purgeExpired();
    }

    public boolean isBlacklisted(String token) {
        purgeExpired();
        String jti = jwtService.extractJti(token);
        Instant expiration = revokedTokensByJti.get(jti);
        if (expiration == null) {
            return false;
        }
        if (expiration.isBefore(Instant.now())) {
            revokedTokensByJti.remove(jti);
            return false;
        }
        return true;
    }

    private void purgeExpired() {
        Instant now = Instant.now();
        revokedTokensByJti.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
