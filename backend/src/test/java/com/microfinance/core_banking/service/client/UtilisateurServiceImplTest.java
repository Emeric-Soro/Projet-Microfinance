package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.config.AuthSecurityProperties;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.HistoriqueMotDePasseUtilisateur;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.HistoriqueMotDePasseUtilisateurRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.service.communication.NotificationService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UtilisateurServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private HistoriqueMotDePasseUtilisateurRepository historiqueMotDePasseUtilisateurRepository;

    @Mock
    private RoleUtilisateurRepository roleUtilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthSecurityProperties authSecurityProperties;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private UtilisateurServiceImpl utilisateurService;

    @BeforeEach
    void setUp() {
        when(authSecurityProperties.getCredentialsValidityDays()).thenReturn(90);
        when(authSecurityProperties.getMaxFailedAttempts()).thenReturn(3);
        when(authSecurityProperties.getLockDuration()).thenReturn(Duration.ofMinutes(15));
        when(authSecurityProperties.getOtpValidity()).thenReturn(Duration.ofMinutes(5));
        when(authSecurityProperties.getOtpLength()).thenReturn(6);
        when(authSecurityProperties.getMaxOtpAttempts()).thenReturn(3);
        when(authSecurityProperties.getPasswordHistoryDepth()).thenReturn(5);
    }

    @Test
    void shouldCreateUserAccountWhenActivationDataMatchesClient() {
        Client client = buildClient();
        when(clientRepository.findByCodeClient("CLI-20260427-1234")).thenReturn(Optional.of(client));
        when(utilisateurRepository.existsByClient_IdClient(7L)).thenReturn(false);
        when(utilisateurRepository.existsByLogin("client@example.com")).thenReturn(false);
        when(passwordEncoder.encode("MotDePasse1!")).thenReturn("hash");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur utilisateur = utilisateurService.creerCompteWeb(
                "cli-20260427-1234",
                "Client@example.com",
                LocalDate.of(1990, 5, 12),
                "MotDePasse1!"
        );

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(captor.capture());

        Utilisateur saved = captor.getValue();
        assertThat(saved.getClient()).isEqualTo(client);
        assertThat(saved.getLogin()).isEqualTo("client@example.com");
        assertThat(saved.getPassword()).isEqualTo("hash");
        assertThat(saved.getActif()).isTrue();
        assertThat(saved.getSecondFacteurActive()).isTrue();
        assertThat(saved.getIdentifiantsExpirentLe()).isAfter(saved.getMotDePasseModifieLe());
        assertThat(utilisateur.getLogin()).isEqualTo("client@example.com");
    }

    @Test
    void shouldLockAccountAfterConfiguredFailedAttempts() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setNombreEchecsConnexion(2);

        when(utilisateurRepository.findByLogin("client@example.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("MauvaisMotDePasse1!", "hash")).thenReturn(false);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> utilisateurService.authentifier("client@example.com", "MauvaisMotDePasse1!"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Identifiants invalides");

        assertThat(utilisateur.getCompteVerrouilleJusquAu()).isNotNull();
        assertThat(utilisateur.getNombreEchecsConnexion()).isZero();
        verify(notificationService).envoyerAlerteConnexionSuspecte(7L);
    }

    @Test
    void shouldGenerateOtpChallengeAfterPasswordValidation() {
        Utilisateur utilisateur = buildUtilisateur();

        when(utilisateurRepository.findByLogin("client@example.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("MotDePasse1!", "hash")).thenReturn(true);
        when(passwordEncoder.encode(any(String.class))).thenReturn("otp-hash");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthenticationWorkflowResult resultat = utilisateurService.authentifier("client@example.com", "MotDePasse1!");

        assertThat(resultat.otpRequired()).isTrue();
        assertThat(resultat.challengeId()).isNotBlank();
        assertThat(utilisateur.getOtpHash()).isEqualTo("otp-hash");
        assertThat(utilisateur.getOtpTentativesRestantes()).isEqualTo(3);
        verify(notificationService).envoyerCodeAuthentification(eq(7L), any(String.class));
    }

    @Test
    void shouldValidateOtpAndOpenSession() {
        Utilisateur utilisateur = buildUtilisateur();
        utilisateur.setOtpChallengeId("challenge-123");
        utilisateur.setOtpHash("otp-hash");
        utilisateur.setOtpExpireLe(LocalDateTime.now().plusMinutes(5));
        utilisateur.setOtpTentativesRestantes(3);

        when(utilisateurRepository.findByLogin("client@example.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("123456", "otp-hash")).thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur resultat = utilisateurService.verifierSecondFacteur("client@example.com", "challenge-123", "123456");

        assertThat(resultat.getOtpChallengeId()).isNull();
        assertThat(resultat.getOtpHash()).isNull();
        assertThat(resultat.getOtpExpireLe()).isNull();
        assertThat(resultat.getDerniereConnexionReussie()).isNotNull();
    }

    @Test
    void shouldChangeOwnPasswordAndArchivePreviousHash() {
        Utilisateur utilisateur = buildUtilisateur();

        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(utilisateur);
        when(utilisateurRepository.findById(11L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("MotDePasse1!", "hash")).thenReturn(true);
        when(passwordEncoder.matches("NouveauMot2@", "hash")).thenReturn(false);
        when(historiqueMotDePasseUtilisateurRepository.findByUtilisateur_IdUserOrderByDateChangementDesc(11L))
                .thenReturn(List.of());
        when(passwordEncoder.encode("NouveauMot2@")).thenReturn("new-hash");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur resultat = utilisateurService.changerMotDePasse(
                11L,
                "MotDePasse1!",
                "NouveauMot2@",
                "Rotation periodique"
        );

        ArgumentCaptor<HistoriqueMotDePasseUtilisateur> historiqueCaptor = ArgumentCaptor.forClass(HistoriqueMotDePasseUtilisateur.class);
        verify(historiqueMotDePasseUtilisateurRepository).save(historiqueCaptor.capture());

        assertThat(historiqueCaptor.getValue().getHashMotDePasse()).isEqualTo("hash");
        assertThat(resultat.getPassword()).isEqualTo("new-hash");
        assertThat(resultat.getNombreEchecsConnexion()).isZero();
        assertThat(resultat.getOtpHash()).isNull();
    }

    @Test
    void shouldRejectPasswordReuseFromRecentHistory() {
        Utilisateur utilisateur = buildUtilisateur();

        HistoriqueMotDePasseUtilisateur historique = new HistoriqueMotDePasseUtilisateur();
        historique.setHashMotDePasse("old-hash");

        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(utilisateur);
        when(utilisateurRepository.findById(11L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("MotDePasse1!", "hash")).thenReturn(true);
        when(passwordEncoder.matches("AncienMot3#", "hash")).thenReturn(false);
        when(historiqueMotDePasseUtilisateurRepository.findByUtilisateur_IdUserOrderByDateChangementDesc(11L))
                .thenReturn(List.of(historique));
        when(passwordEncoder.matches("AncienMot3#", "old-hash")).thenReturn(true);

        assertThatThrownBy(() -> utilisateurService.changerMotDePasse(
                11L,
                "MotDePasse1!",
                "AncienMot3#",
                "Tentative de reutilisation"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Le nouveau mot de passe ne peut pas reutiliser un mot de passe recent");
    }

    @Test
    void shouldAllowAdminToResetAnotherUserPasswordWithoutCurrentPassword() {
        Utilisateur administrateur = buildUtilisateur();
        administrateur.setIdUser(99L);
        administrateur.setRoles(Set.of(buildRole("ADMIN")));

        Utilisateur cible = buildUtilisateur();

        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(administrateur);
        when(utilisateurRepository.findById(11L)).thenReturn(Optional.of(cible));
        when(passwordEncoder.matches("AdminReset4$", "hash")).thenReturn(false);
        when(historiqueMotDePasseUtilisateurRepository.findByUtilisateur_IdUserOrderByDateChangementDesc(11L))
                .thenReturn(List.of());
        when(passwordEncoder.encode("AdminReset4$")).thenReturn("reset-hash");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur resultat = utilisateurService.changerMotDePasse(
                11L,
                null,
                "AdminReset4$",
                "Reset superviseur"
        );

        assertThat(resultat.getPassword()).isEqualTo("reset-hash");
    }

    private Client buildClient() {
        StatutClient statutClient = new StatutClient();
        statutClient.setLibelleStatut("ACTIF");

        Client client = new Client();
        client.setIdClient(7L);
        client.setCodeClient("CLI-20260427-1234");
        client.setEmail("client@example.com");
        client.setTelephone("+221770000000");
        client.setDateNaissance(LocalDate.of(1990, 5, 12));
        client.setStatutClient(statutClient);
        return client;
    }

    private Utilisateur buildUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(11L);
        utilisateur.setLogin("client@example.com");
        utilisateur.setPassword("hash");
        utilisateur.setClient(buildClient());
        utilisateur.setActif(true);
        utilisateur.setSecondFacteurActive(true);
        utilisateur.setMotDePasseModifieLe(LocalDateTime.now().minusDays(1));
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(30));
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setRoles(Set.of(buildRole("CLIENT")));
        return utilisateur;
    }

    private RoleUtilisateur buildRole(String codeRole) {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setCodeRoleUtilisateur(codeRole);
        role.setIntituleRole(codeRole);
        return role;
    }
}
