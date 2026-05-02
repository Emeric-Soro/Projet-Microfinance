package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.support.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenSecurityTest {

    @Test
    void adminTokenContainsAdminRole() {
        String token = JwtTokenProvider.generateTokenWithRole("ADMIN");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void expiredTokenIsRejected() {
        String expiredToken = JwtTokenProvider.generateExpiredToken();
        assertNotNull(expiredToken);

        assertThrows(Exception.class, () -> {
            Jwts.parserBuilder()
                .setSigningKey(JwtTokenProvider.getSecretKey())
                .build()
                .parseClaimsJws(expiredToken);
        });
    }

    @Test
    void tokenWithPermissionsContainsExpectedClaims() {
        String token = JwtTokenProvider.generateTokenWithPermissions("CREDIT_MANAGE", "CREDIT_VIEW");

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(JwtTokenProvider.getSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        assertThat(claims.getSubject()).isEqualTo("perm_user");
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getIssuedAt()).isNotNull();
    }

    @Test
    void differentRolesProduceDifferentTokens() {
        String adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
        String userToken = JwtTokenProvider.generateTokenWithRole("CLIENT");

        assertNotEquals(adminToken, userToken);
    }

    @Test
    void validTokenHasExpectedStructure() {
        String token = JwtTokenProvider.generateTokenWithRole("GUICHETIER");

        assertThat(token).matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(JwtTokenProvider.getSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        assertThat(claims.getSubject()).isEqualTo("test_guichetier");
        assertThat(claims.get("roles")).asList().contains("GUICHETIER");
    }
}
