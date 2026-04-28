package com.microfinance.core_banking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PublicApiRateLimitingFilter publicApiRateLimitingFilter;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactivé car le JWT nous protège déjà
                .cors(Customizer.withDefaults())
                // H2 Console s'affiche dans une frame, on autorise la même origine.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 1. Les routes 100% publiques (Login et création web)
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs/login", "/api/utilisateurs/login/otp", "/api/utilisateurs").permitAll()
                        // 1.b Autorise l'inscription d'un nouveau client sans JWT.
                        .requestMatchers(HttpMethod.POST, "/api/clients").permitAll()
                        // 1.c Autorise l'acces a la console H2.
                        .requestMatchers("/h2-console/**").permitAll()
                        // 2. Swagger public pour qu'on puisse tester
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 3. Toutes les autres routes (transactions, comptes, etc.) nécessitent un Token valide
                        .anyRequest().authenticated()
                )
                // On dit à Spring de ne pas créer de session serveur (STATELESS) car on utilise des tokens
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(publicApiRateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                // On place notre Vigile JWT AVANT le filtre de vérification par défaut de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
