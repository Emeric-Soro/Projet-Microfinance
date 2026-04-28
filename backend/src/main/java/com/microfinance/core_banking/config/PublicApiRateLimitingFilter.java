package com.microfinance.core_banking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PublicApiRateLimitingFilter extends OncePerRequestFilter {

    private static final String HEADER_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_REAL_IP = "X-Real-IP";

    private final ObjectMapper objectMapper;
    private final Cache<String, WindowCounter> rateLimitCache;
    private final PublicApiRateLimitProperties properties;

    public PublicApiRateLimitingFilter(
            ObjectMapper objectMapper,
            PublicApiRateLimitProperties properties
    ) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.rateLimitCache = Caffeine.newBuilder()
                .maximumSize(properties.getCacheMaxEntries())
                .expireAfterAccess(properties.getCacheTtl())
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || resolveEndpoint(request.getRequestURI()).isEmpty();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        EndpointDescriptor endpoint = resolveEndpoint(request.getRequestURI()).orElse(null);
        if (endpoint == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String cacheKey = endpoint.cacheKeyPrefix() + ":" + resolveClientIp(request);
        WindowCounter counter = rateLimitCache.get(cacheKey, key -> new WindowCounter());
        RateLimitDecision decision = counter.tryConsume(endpoint.config(), Instant.now());

        response.setHeader("X-Rate-Limit-Limit", String.valueOf(endpoint.config().getMaxRequests()));
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(decision.remainingRequests()));

        if (!decision.allowed()) {
            response.setHeader("Retry-After", String.valueOf(Math.max(1L, decision.retryAfterSeconds())));
            writeTooManyRequestsResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<EndpointDescriptor> resolveEndpoint(String requestUri) {
        return switch (requestUri) {
            case "/api/clients" -> Optional.of(new EndpointDescriptor(
                    "client-signup",
                    properties.getClientSignup()
            ));
            case "/api/utilisateurs" -> Optional.of(new EndpointDescriptor(
                    "user-signup",
                    properties.getUserSignup()
            ));
            case "/api/utilisateurs/login" -> Optional.of(new EndpointDescriptor(
                    "login",
                    properties.getLogin()
            ));
            default -> Optional.empty();
        };
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(HEADER_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader(HEADER_REAL_IP);
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private void writeTooManyRequestsResponse(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDTO payload = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                "Trop de requetes ont ete recues sur ce point d'entree public. Veuillez reessayer plus tard.",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getWriter(), payload);
    }

    private record EndpointDescriptor(
            String cacheKeyPrefix,
            PublicApiRateLimitProperties.Endpoint config
    ) {
    }

    private record RateLimitDecision(
            boolean allowed,
            long remainingRequests,
            long retryAfterSeconds
    ) {
    }

    private static final class WindowCounter {

        private Instant windowStartedAt = Instant.EPOCH;
        private int requestCount = 0;

        synchronized RateLimitDecision tryConsume(
                PublicApiRateLimitProperties.Endpoint config,
                Instant now
        ) {
            Duration window = config.getWindow();
            Instant windowEndsAt = windowStartedAt.plus(window);

            if (requestCount == 0 || !now.isBefore(windowEndsAt)) {
                windowStartedAt = now;
                requestCount = 0;
                windowEndsAt = windowStartedAt.plus(window);
            }

            if (requestCount >= config.getMaxRequests()) {
                return new RateLimitDecision(false, 0, secondsUntil(windowEndsAt, now));
            }

            requestCount++;
            return new RateLimitDecision(
                    true,
                    config.getMaxRequests() - requestCount,
                    secondsUntil(windowEndsAt, now)
            );
        }

        private long secondsUntil(Instant deadline, Instant now) {
            long milliseconds = Math.max(0L, Duration.between(now, deadline).toMillis());
            return (milliseconds + 999L) / 1000L;
        }
    }
}
