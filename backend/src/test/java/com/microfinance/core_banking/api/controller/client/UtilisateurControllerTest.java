package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.service.client.UtilisateurService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import com.microfinance.core_banking.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilisateurController.class)
class UtilisateurControllerTest extends AbstractControllerTest {

    @MockBean
    private UtilisateurService utilisateurService;

    private String adminToken;
    private Utilisateur sampleUser;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
        sampleUser = TestDataFactory.createSampleUtilisateur();
    }

    @Test
    void shouldLogin() throws Exception {
        Map<String, String> loginRequest = Map.of("login", "admin", "password", "pass123");
        when(utilisateurService.authentifier(eq("admin"), eq("pass123")))
            .thenReturn(Optional.of(sampleUser));

        mockMvc.perform(post(ApiTestConstants.UTILISATEURS_BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401ForInvalidLogin() throws Exception {
        Map<String, String> loginRequest = Map.of("login", "wrong", "password", "wrong");
        when(utilisateurService.authentifier(eq("wrong"), eq("wrong")))
            .thenThrow(new RuntimeException("Identifiants invalides"));

        mockMvc.perform(post(ApiTestConstants.UTILISATEURS_BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldInitiateOtp() throws Exception {
        Map<String, String> otpRequest = Map.of("login", "user@test.com");
        when(utilisateurService.initierOtp(eq("user@test.com")))
            .thenReturn(Map.of("challengeId", "CHAL-001", "message", "OTP envoye"));

        mockMvc.perform(post(ApiTestConstants.UTILISATEURS_BASE + "/login/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(otpRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.challengeId").value("CHAL-001"));
    }

    @Test
    void shouldVerifyOtp() throws Exception {
        Map<String, String> verifyRequest = Map.of(
            "challengeId", "CHAL-001",
            "otpCode", "123456"
        );
        String jwtToken = JwtTokenProvider.generateToken(sampleUser);
        when(utilisateurService.verifierOtp(eq("CHAL-001"), eq("123456")))
            .thenReturn(jwtToken);

        mockMvc.perform(post(ApiTestConstants.UTILISATEURS_BASE + "/login/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(verifyRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRegisterUser() throws Exception {
        when(utilisateurService.creerUtilisateur(any(Utilisateur.class))).thenReturn(sampleUser);

        mockMvc.perform(post(ApiTestConstants.UTILISATEURS_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sampleUser)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(utilisateurService.rechercherParId(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get(ApiTestConstants.UTILISATEURS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value(sampleUser.getLogin()));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(utilisateurService.rechercherParId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get(ApiTestConstants.UTILISATEURS_BASE + "/999")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldChangePassword() throws Exception {
        Map<String, String> pwdRequest = Map.of(
            "oldPassword", "old123",
            "newPassword", "new456"
        );
        when(utilisateurService.changerMotDePasse(eq(1L), eq("old123"), eq("new456")))
            .thenReturn(sampleUser);

        mockMvc.perform(put(ApiTestConstants.UTILISATEURS_BASE + "/1/password")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(pwdRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldAssignRole() throws Exception {
        when(utilisateurService.assignerRole(eq(1L), eq("ADMIN")))
            .thenReturn(sampleUser);

        mockMvc.perform(put(ApiTestConstants.UTILISATEURS_BASE + "/1/roles/ADMIN")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldActivateUser() throws Exception {
        when(utilisateurService.activerUtilisateur(1L)).thenReturn(sampleUser);

        mockMvc.perform(put(ApiTestConstants.UTILISATEURS_BASE + "/1/activer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldDeactivateUser() throws Exception {
        when(utilisateurService.desactiverUtilisateur(1L)).thenReturn(sampleUser);

        mockMvc.perform(put(ApiTestConstants.UTILISATEURS_BASE + "/1/desactiver")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldLogout() throws Exception {
        mockMvc.perform(post(ApiTestConstants.UTILISATEURS_BASE + "/logout")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401ForProtectedEndpoint() throws Exception {
        mockMvc.perform(get(ApiTestConstants.UTILISATEURS_BASE + "/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
