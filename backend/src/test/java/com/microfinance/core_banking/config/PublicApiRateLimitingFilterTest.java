package com.microfinance.core_banking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class PublicApiRateLimitingFilterTest {

    @Test
    void shouldReturnTooManyRequestsAfterConfiguredThreshold() throws Exception {
        PublicApiRateLimitProperties properties = buildProperties();
        properties.getUserSignup().setMaxRequests(3);

        PublicApiRateLimitingFilter filter = new PublicApiRateLimitingFilter(
                new ObjectMapper().findAndRegisterModules(),
                properties
        );

        for (int attempt = 0; attempt < 3; attempt++) {
            MockHttpServletResponse response = executePost(filter, "/api/utilisateurs", "203.0.113.10");
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getHeader("X-Rate-Limit-Remaining")).isNotBlank();
        }

        MockHttpServletResponse blockedResponse = executePost(filter, "/api/utilisateurs", "203.0.113.10");

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
        assertThat(blockedResponse.getHeader("Retry-After")).isNotBlank();
        assertThat(blockedResponse.getContentAsString())
                .contains("Trop de requetes")
                .contains("/api/utilisateurs");
    }

    @Test
    void shouldKeepSeparateCountersPerClientIp() throws Exception {
        PublicApiRateLimitProperties properties = buildProperties();
        properties.getLogin().setMaxRequests(1);

        PublicApiRateLimitingFilter filter = new PublicApiRateLimitingFilter(
                new ObjectMapper().findAndRegisterModules(),
                properties
        );

        MockHttpServletResponse firstClient = executePost(filter, "/api/utilisateurs/login", "198.51.100.1");
        MockHttpServletResponse blockedFirstClient = executePost(filter, "/api/utilisateurs/login", "198.51.100.1");
        MockHttpServletResponse secondClient = executePost(filter, "/api/utilisateurs/login", "198.51.100.2");

        assertThat(firstClient.getStatus()).isEqualTo(200);
        assertThat(blockedFirstClient.getStatus()).isEqualTo(429);
        assertThat(secondClient.getStatus()).isEqualTo(200);
    }

    private PublicApiRateLimitProperties buildProperties() {
        PublicApiRateLimitProperties properties = new PublicApiRateLimitProperties();
        properties.setCacheMaxEntries(100);
        properties.setCacheTtl(Duration.ofMinutes(5));
        properties.getClientSignup().setWindow(Duration.ofMinutes(1));
        properties.getUserSignup().setWindow(Duration.ofMinutes(1));
        properties.getLogin().setWindow(Duration.ofMinutes(1));
        return properties;
    }

    private MockHttpServletResponse executePost(
            PublicApiRateLimitingFilter filter,
            String path,
            String remoteAddress
    ) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setRemoteAddr(remoteAddress);

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }
}
