package com.microfinance.core_banking.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenBlacklistServiceTest {

    @Mock
    private JwtService jwtService;

    @Test
    void shouldBlacklistTokenUntilExpiration() {
        JwtTokenBlacklistService service = new JwtTokenBlacklistService(jwtService);
        String token = "valid-token";

        when(jwtService.extractJti(token)).thenReturn("jti-valid");
        when(jwtService.extractExpiration(token)).thenReturn(Date.from(Instant.now().plusSeconds(120)));

        service.blacklist(token);

        assertTrue(service.isBlacklisted(token));
    }

    @Test
    void shouldNotConsiderExpiredBlacklistedTokenAsRevoked() {
        JwtTokenBlacklistService service = new JwtTokenBlacklistService(jwtService);
        String token = "expired-token";

        when(jwtService.extractJti(token)).thenReturn("jti-expired");
        when(jwtService.extractExpiration(token)).thenReturn(Date.from(Instant.now().minusSeconds(60)));

        service.blacklist(token);

        assertFalse(service.isBlacklisted(token));
    }
}
