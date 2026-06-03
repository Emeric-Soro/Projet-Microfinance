package com.soutra.microfinance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PublicApiRateLimitingFilter publicApiRateLimitingFilter;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactivé car le JWT nous protège déjà
                .cors(Customizer.withDefaults())
                // H2 Console s'affiche dans une frame, on autorise la même origine.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> {
                    auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 1. Les routes 100% publiques (Login et création web)
                        .requestMatchers(HttpMethod.POST, "/api/v1/utilisateurs/login", "/api/v1/utilisateurs/login/otp", "/api/v1/utilisateurs").permitAll()
                        // 1.b Routes d'authentification /api/v1/auth/ publiques
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/mobile/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/mobile/auth/login/otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/mobile/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/mobile/auth/mot-de-passe/oublie").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/mobile/auth/mot-de-passe/reinitialiser").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/mot-de-passe/oublie").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/mot-de-passe/reinitialiser").permitAll()
                        // 1.c Simulation credit publique
                        .requestMatchers(HttpMethod.POST, "/api/v1/credits/simulation").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/mobile/credits/simulation").permitAll()
                        // 1.d Health check publique
                        .requestMatchers("/api/v1/health").permitAll()
                        .requestMatchers("/api/v1/health/db").permitAll()
                        .requestMatchers("/api/v1/health/cache").permitAll()
                        // 1.e Autorise l'inscription d'un nouveau client sans JWT.
                        .requestMatchers(HttpMethod.POST, "/api/v1/clients").permitAll()
                        // 2. Actuator public limite aux sondes non sensibles.
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll();

                    // H2 console accessible uniquement en profil dev
                    if (isDevProfile()) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }

                    // Swagger/OpenAPI accessible uniquement en profil dev
                    if (isDevProfile()) {
                        auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
                    }

                    // 3. Toutes les autres routes necessitent un Token valide (MUST be last)
                    auth.anyRequest().authenticated();
                })
                // On dit à Spring de ne pas créer de session serveur (STATELESS) car on utilise des tokens
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                    "Unauthorized", "Authentification requise", request.getRequestURI());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                                    "Forbidden", "Acces refuse", request.getRequestURI());
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(publicApiRateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                // On place notre Vigile JWT AVANT le filtre de vérification par défaut de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private boolean isDevProfile() {
        return Arrays.asList(activeProfile.split(",")).contains("dev");
    }

    private static void writeJsonError(HttpServletResponse response, int status,
                                       String error, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
