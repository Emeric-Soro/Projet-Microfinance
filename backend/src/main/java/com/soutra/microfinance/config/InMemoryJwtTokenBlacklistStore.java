package com.soutra.microfinance.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.security.jwt.blacklist-store", havingValue = "memory", matchIfMissing = true)
public class InMemoryJwtTokenBlacklistStore implements JwtTokenBlacklistStore {

    private final Map<String, Instant> revokedTokensByJti = new ConcurrentHashMap<>();

    @Override
    public void revoke(String jti, Instant expiration) {
        revokedTokensByJti.put(jti, expiration);
        purgeExpired();
    }

    @Override
    public boolean contains(String jti) {
        purgeExpired();
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
