package com.soutra.microfinance.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@ConditionalOnProperty(name = "app.security.jwt.blacklist-store", havingValue = "redis")
public class RedisJwtTokenBlacklistStore implements JwtTokenBlacklistStore {

    private static final String PREFIX = "soutra:jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public RedisJwtTokenBlacklistStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void revoke(String jti, Instant expiration) {
        Duration ttl = Duration.between(Instant.now(), expiration);
        if (ttl.isPositive()) {
            redisTemplate.opsForValue().set(PREFIX + jti, "1", ttl);
        }
    }

    @Override
    public boolean contains(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }
}
