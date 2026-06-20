package com.soutra.microfinance.service.client;

import com.soutra.microfinance.config.AuthSecurityProperties;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.RoleUtilisateurRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.service.communication.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthWorkflowTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RoleUtilisateurRepository roleUtilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthSecurityProperties authSecurityProperties;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UtilisateurServiceImpl utilisateurService;

    @Test
    void shouldReturnOtpChallengeWhen2FAIsActive() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setSecondFacteurActive(true);

        when(utilisateurRepository.findByLogin("admin@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("Password123!", utilisateur.getPassword())).thenReturn(true);
        when(authSecurityProperties.getOtpValidity()).thenReturn(Duration.ofMinutes(5));
        when(authSecurityProperties.getMaxOtpAttempts()).thenReturn(3);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        AuthenticationWorkflowResult result = utilisateurService.authentifier("admin@test.com", "Password123!");

        assertThat(result.otpRequired()).isTrue();
        assertThat(result.challengeId()).isNotBlank();
        verify(notificationService).envoyerCodeAuthentification(anyLong(), anyString());
    }

    @Test
    void shouldReturnTokensWhen2FAIsDisabled() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setSecondFacteurActive(false);

        when(utilisateurRepository.findByLogin("admin@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("Password123!", utilisateur.getPassword())).thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        AuthenticationWorkflowResult result = utilisateurService.authentifier("admin@test.com", "Password123!");

        assertThat(result.otpRequired()).isFalse();
        assertThat(result.utilisateur()).isNotNull();
        verify(notificationService, never()).envoyerCodeAuthentification(anyLong(), anyString());
    }

    @Test
    void shouldLockAccountAfterMaxFailedAttempts() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setNombreEchecsConnexion(4);

        when(utilisateurRepository.findByLogin("admin@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("WrongPassword", utilisateur.getPassword())).thenReturn(false);
        when(authSecurityProperties.getMaxFailedAttempts()).thenReturn(5);
        when(authSecurityProperties.getLockDuration()).thenReturn(Duration.ofMinutes(15));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        assertThatThrownBy(() -> utilisateurService.authentifier("admin@test.com", "WrongPassword"))
                .isInstanceOf(BadCredentialsException.class);

        assertThat(utilisateur.getCompteVerrouilleJusquAu()).isNotNull();
        verify(notificationService).envoyerAlerteConnexionSuspecte(anyLong());
    }

    @Test
    void shouldRejectInvalidOtp() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setSecondFacteurActive(true);
        utilisateur.setOtpChallengeId("challenge-123");
        utilisateur.setOtpHash("$2a$10$encodedOtp");
        utilisateur.setOtpExpireLe(LocalDateTime.now().plusMinutes(5));
        utilisateur.setOtpTentativesRestantes(3);

        when(utilisateurRepository.findByLogin("admin@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("000000", utilisateur.getOtpHash())).thenReturn(false);
        when(authSecurityProperties.getLockDuration()).thenReturn(Duration.ofMinutes(15));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        assertThatThrownBy(() -> utilisateurService.verifierSecondFacteur("admin@test.com", "challenge-123", "000000"))
                .isInstanceOf(BadCredentialsException.class);

        assertThat(utilisateur.getOtpTentativesRestantes()).isEqualTo(2);
    }

    @Test
    void shouldAcceptValidOtp() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setSecondFacteurActive(true);
        utilisateur.setOtpChallengeId("challenge-123");
        utilisateur.setOtpHash("$2a$10$encodedOtp");
        utilisateur.setOtpExpireLe(LocalDateTime.now().plusMinutes(5));
        utilisateur.setOtpTentativesRestantes(3);

        when(utilisateurRepository.findByLogin("admin@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("123456", utilisateur.getOtpHash())).thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur result = utilisateurService.verifierSecondFacteur("admin@test.com", "challenge-123", "123456");

        assertThat(result.getDerniereConnexionReussie()).isNotNull();
        assertThat(result.getOtpChallengeId()).isNull();
        assertThat(result.getOtpHash()).isNull();
    }

    @Test
    void shouldRejectLoginWhenAccountIsLocked() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setCompteVerrouilleJusquAu(LocalDateTime.now().plusMinutes(10));

        when(utilisateurRepository.findByLogin("admin@test.com")).thenReturn(Optional.of(utilisateur));

        assertThatThrownBy(() -> utilisateurService.authentifier("admin@test.com", "Password123!"))
                .isInstanceOf(org.springframework.security.authentication.LockedException.class);
    }

    private Utilisateur buildUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(1L);
        utilisateur.setLogin("admin@test.com");
        utilisateur.setPassword("$2a$10$encodedPassword");
        utilisateur.setActif(true);
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setCompteExpireLe(null);
        utilisateur.setCompteVerrouilleJusquAu(null);
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(90));
        Client client = new Client();
        client.setIdClient(1L);
        StatutClient statut = new StatutClient();
        statut.setLibelleStatut("ACTIF");
        client.setStatutClient(statut);
        client.setTelephone("+221770000000");
        utilisateur.setClient(client);
        return utilisateur;
    }
}
