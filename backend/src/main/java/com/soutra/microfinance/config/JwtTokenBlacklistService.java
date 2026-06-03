package com.soutra.microfinance.config;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenBlacklistService {

    private final JwtService jwtService;
    private final JwtTokenBlacklistStore blacklistStore;

    @Autowired
    public JwtTokenBlacklistService(JwtService jwtService, JwtTokenBlacklistStore blacklistStore) {
        this.jwtService = jwtService;
        this.blacklistStore = blacklistStore;
    }

    public JwtTokenBlacklistService(JwtService jwtService) {
        this(jwtService, new InMemoryJwtTokenBlacklistStore());
    }

    public void blacklist(String token) {
        String jti = jwtService.extractJti(token);
        Instant expiration = jwtService.extractExpiration(token).toInstant();
        blacklistStore.revoke(jti, expiration);
    }

    public boolean isBlacklisted(String token) {
        String jti = jwtService.extractJti(token);
        return blacklistStore.contains(jti);
    }
}
