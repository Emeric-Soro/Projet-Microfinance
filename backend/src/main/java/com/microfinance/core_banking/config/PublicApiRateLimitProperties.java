package com.microfinance.core_banking.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.security.rate-limit")
public class PublicApiRateLimitProperties {

    @Positive
    private long cacheMaxEntries = 10000;

    @NotNull
    private Duration cacheTtl = Duration.ofMinutes(30);

    @Valid
    private Endpoint clientSignup = new Endpoint(5, Duration.ofMinutes(1));

    @Valid
    private Endpoint userSignup = new Endpoint(3, Duration.ofMinutes(1));

    @Valid
    private Endpoint login = new Endpoint(10, Duration.ofMinutes(1));

    @Getter
    @Setter
    public static class Endpoint {

        @Positive
        private int maxRequests;

        @NotNull
        private Duration window;

        public Endpoint() {
        }

        public Endpoint(int maxRequests, Duration window) {
            this.maxRequests = maxRequests;
            this.window = window;
        }
    }
}
