package com.soutra.microfinance.api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutra.microfinance.config.JwtAuthenticationFilter;
import com.soutra.microfinance.config.JwtService;
import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.config.PublicApiRateLimitProperties;
import com.soutra.microfinance.config.PublicApiRateLimitingFilter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.soutra.microfinance.dto.request.client.LoginRequestDTO;
import com.soutra.microfinance.dto.request.client.VerificationOtpRequestDTO;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.UtilisateurMapper;
import com.soutra.microfinance.service.client.AuthenticationWorkflowResult;
import com.soutra.microfinance.service.client.SessionService;
import com.soutra.microfinance.service.client.UtilisateurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableConfigurationProperties(PublicApiRateLimitProperties.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UtilisateurService utilisateurService;

    @MockitoBean
    private UtilisateurMapper utilisateurMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenBlacklistService jwtTokenBlacklistService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PublicApiRateLimitingFilter publicApiRateLimitingFilter;

    @Test
    void shouldReturnOtpChallengeWhenLoginRequires2FA() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin@test.com", "Password123!");
        AuthenticationWorkflowResult result = new AuthenticationWorkflowResult(
                null, true, "challenge-123", "Un code de verification a ete envoye"
        );

        when(utilisateurService.authentifier(anyString(), anyString())).thenReturn(result);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otpRequis").value(true))
                .andExpect(jsonPath("$.challengeId").value("challenge-123"));
    }

    @Test
    void shouldReturnTokensWhenLoginSucceeds() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin@test.com", "Password123!");
        Utilisateur utilisateur = buildUtilisateur();
        AuthenticationWorkflowResult result = new AuthenticationWorkflowResult(
                utilisateur, false, null, "Authentification reussie"
        );

        when(utilisateurService.authentifier(anyString(), anyString())).thenReturn(result);
        when(jwtService.generateToken(any(Utilisateur.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(Utilisateur.class))).thenReturn("refresh-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void shouldReturn400WhenLoginFieldsAreBlank() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn204WhenForgotPasswordRequestValid() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("loginOuEmail", "client@example.com");

        mockMvc.perform(post("/api/v1/auth/mot-de-passe/oublie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn204WhenForgotPasswordUserUnknown() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("loginOuEmail", "inconnu@example.com");

        mockMvc.perform(post("/api/v1/auth/mot-de-passe/oublie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenForgotPasswordEmailBlank() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("loginOuEmail", "");

        mockMvc.perform(post("/api/v1/auth/mot-de-passe/oublie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn204WhenResetPasswordRequestValid() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("token", "valid-token-abc");
        body.put("nouveauMotDePasse", "NouveauMotDePasse1!");
        body.put("confirmationMotDePasse", "NouveauMotDePasse1!");

        mockMvc.perform(post("/api/v1/auth/mot-de-passe/reinitialiser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenResetPasswordFieldsBlank() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("token", "");
        body.put("nouveauMotDePasse", "");

        mockMvc.perform(post("/api/v1/auth/mot-de-passe/reinitialiser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    private Utilisateur buildUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(1L);
        utilisateur.setLogin("admin@test.com");
        utilisateur.setPassword("encoded");
        utilisateur.setActif(true);
        utilisateur.setRoles(new HashSet<>());
        Client client = new Client();
        client.setIdClient(1L);
        StatutClient statut = new StatutClient();
        statut.setLibelleStatut("ACTIF");
        client.setStatutClient(statut);
        utilisateur.setClient(client);
        return utilisateur;
    }
}
