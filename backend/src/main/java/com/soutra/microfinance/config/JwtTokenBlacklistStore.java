package com.soutra.microfinance.config;

import java.time.Instant;

public interface JwtTokenBlacklistStore {

    void revoke(String jti, Instant expiration);

    boolean contains(String jti);
}
