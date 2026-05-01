package com.microfinance.core_banking.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Recupere l'en-tete Authorization pour detecter un token JWT Bearer.
        final String authHeader = request.getHeader("Authorization");

        // Si l'en-tete est absent ou mal forme, on laisse simplement continuer la chaine des filtres.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrait la valeur brute du JWT (sans le prefixe "Bearer ").
        final String jwt = authHeader.substring(7);
        try {
            if (jwtTokenBlacklistService.isBlacklisted(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT revoque");
                return;
            }

            final String username = jwtService.extractUsername(jwt);

            // N'authentifie que si un username est present et qu'aucune auth n'existe deja dans le contexte.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Verifie la signature + expiration du token avant de creer l'authentification Spring Security.
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Attache les details de la requete HTTP courante (ip, session, etc.) au token d'authentification.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Place l'utilisateur authentifie dans le SecurityContext pour le reste de la requete.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT invalide");
            return;
        }

        // Continue toujours la chaine de filtres (endpoint suivant / autre filtre).
        filterChain.doFilter(request, response);
    }
}
