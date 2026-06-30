package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.config.PasswordResetProperties;
import com.soutra.microfinance.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetProperties passwordResetProperties;

    @Mock
    private EmailTemplateEngine emailTemplateEngine;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        lenient().when(passwordResetProperties.getFrontendBaseUrl()).thenReturn("http://localhost:5173");
        lenient().when(passwordResetProperties.getResetPath()).thenReturn("/auth/reset");
        lenient().when(passwordResetProperties.getTokenValidity()).thenReturn(Duration.ofMinutes(30));

        emailService = new EmailService(
                mailSender,
                passwordResetProperties,
                emailTemplateEngine,
                "noreply@soutra-core-banking.local",
                "logging" // Use logging delivery mode for tests to prevent sending real emails
        );
    }

    @Test
    void testEnvoyerBienvenue() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        when(emailTemplateEngine.render(eq("welcome"), any())).thenReturn("<html>Welcome</html>");

        emailService.envoyerBienvenue(client);

        verify(emailTemplateEngine).render(eq("welcome"), any());
    }

    @Test
    void testEnvoyerResetMotDePasse() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("emeric.soro");
        utilisateur.setClient(client);

        when(emailTemplateEngine.render(eq("reset-password"), any())).thenReturn("<html>Reset password</html>");

        emailService.envoyerResetMotDePasse(utilisateur, "test-token");

        verify(emailTemplateEngine).render(eq("reset-password"), any());
    }

    @Test
    void testEnvoyerOtp() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("emeric.soro");
        utilisateur.setClient(client);

        when(emailTemplateEngine.render(eq("otp-2fa"), any())).thenReturn("<html>OTP Code</html>");

        emailService.envoyerOtp(utilisateur, "123456");

        verify(emailTemplateEngine).render(eq("otp-2fa"), any());
    }

    @Test
    void testEnvoyerConfirmationVirement() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compteSource = new Compte();
        compteSource.setClient(client);
        compteSource.setNumCompte("CPT-001");

        when(emailTemplateEngine.render(eq("virement-emis"), any())).thenReturn("<html>Virement émis</html>");

        emailService.envoyerConfirmationVirement(compteSource, "Jean", "CPT-001", "TX-1", BigDecimal.TEN);

        verify(emailTemplateEngine).render(eq("virement-emis"), any());
    }

    @Test
    void testEnvoyerAlerteVirementRecu() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compteDest = new Compte();
        compteDest.setClient(client);
        compteDest.setNumCompte("CPT-002");

        when(emailTemplateEngine.render(eq("virement-recu"), any())).thenReturn("<html>Virement reçu</html>");

        emailService.envoyerAlerteVirementRecu(compteDest, "Jean", BigDecimal.TEN, BigDecimal.valueOf(150));

        verify(emailTemplateEngine).render(eq("virement-recu"), any());
    }

    @Test
    void testEnvoyerStatutCredit() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Credit credit = new Credit();
        credit.setClient(client);
        credit.setReferenceCredit("CR-123");
        credit.setMontantAccorde(BigDecimal.valueOf(1000));
        credit.setTauxInteretAnnuel(BigDecimal.valueOf(12));
        credit.setDureeMois(12);

        when(emailTemplateEngine.render(eq("credit-statut"), any())).thenReturn("<html>Crédit approuvé</html>");

        emailService.envoyerStatutCredit(credit, "APPROUVEE", BigDecimal.valueOf(100), null);

        verify(emailTemplateEngine).render(eq("credit-statut"), any());
    }

    @Test
    void testEnvoyerStatutDemandeCredit() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        DemandeCredit demande = new DemandeCredit();
        demande.setClient(client);
        demande.setReferenceDemande("DC-123");
        demande.setMontantDemande(BigDecimal.valueOf(1000));
        demande.setDureeSouhaitee(12);

        when(emailTemplateEngine.render(eq("credit-statut"), any())).thenReturn("<html>Demande crédit en cours</html>");

        emailService.envoyerStatutDemandeCredit(demande, "EN_COURS", null);

        verify(emailTemplateEngine).render(eq("credit-statut"), any());
    }

    @Test
    void testEnvoyerRappelEcheance() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Credit credit = new Credit();
        credit.setClient(client);

        when(emailTemplateEngine.render(eq("credit-rappel"), any())).thenReturn("<html>Rappel échéance</html>");

        emailService.envoyerRappelEcheance(credit, BigDecimal.valueOf(90), LocalDate.now());

        verify(emailTemplateEngine).render(eq("credit-rappel"), any());
    }

    @Test
    void testEnvoyerAlerteRetard() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Credit credit = new Credit();
        credit.setClient(client);

        when(emailTemplateEngine.render(eq("credit-retard"), any())).thenReturn("<html>Alerte retard</html>");

        emailService.envoyerAlerteRetard(credit, BigDecimal.valueOf(90), LocalDate.now(), BigDecimal.valueOf(5));

        verify(emailTemplateEngine).render(eq("credit-retard"), any());
    }

    @Test
    void testEnvoyerConfirmationDepot() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compte = new Compte();
        compte.setClient(client);
        compte.setNumCompte("CPT-001");

        when(emailTemplateEngine.render(eq("depot-confirme"), any())).thenReturn("<html>Dépôt effectué</html>");

        emailService.envoyerConfirmationDepot(compte, BigDecimal.valueOf(100), BigDecimal.valueOf(1000));

        verify(emailTemplateEngine).render(eq("depot-confirme"), any());
    }

    @Test
    void testEnvoyerConfirmationRetrait() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compte = new Compte();
        compte.setClient(client);
        compte.setNumCompte("CPT-001");

        when(emailTemplateEngine.render(eq("retrait-confirme"), any())).thenReturn("<html>Retrait effectué</html>");

        emailService.envoyerConfirmationRetrait(compte, BigDecimal.valueOf(100), BigDecimal.valueOf(1000));

        verify(emailTemplateEngine).render(eq("retrait-confirme"), any());
    }

    @Test
    void testEnvoyerAlerteConnexionSuspecte() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("emeric.soro");
        utilisateur.setClient(client);

        when(emailTemplateEngine.render(eq("connexion-suspecte"), any())).thenReturn("<html>Suspect login alert</html>");

        emailService.envoyerAlerteConnexionSuspecte(utilisateur, "127.0.0.1", "Abidjan", 5);

        verify(emailTemplateEngine).render(eq("connexion-suspecte"), any());
    }

    @Test
    void testEnvoyerConfirmationChangementMotDePasse() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("emeric.soro");
        utilisateur.setClient(client);

        when(emailTemplateEngine.render(eq("password-changed"), any())).thenReturn("<html>Password changed</html>");

        emailService.envoyerConfirmationChangementMotDePasse(utilisateur);

        verify(emailTemplateEngine).render(eq("password-changed"), any());
    }

    @Test
    void testEnvoyerReleveCompte() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compte = new Compte();
        compte.setClient(client);
        compte.setNumCompte("CPT-001");

        when(emailTemplateEngine.render(eq("releve-compte"), any())).thenReturn("<html>Relevé de compte</html>");

        emailService.envoyerReleveCompte(compte, LocalDate.now(), LocalDate.now(), new byte[]{});

        verify(emailTemplateEngine).render(eq("releve-compte"), any());
    }

    @Test
    void testEnvoyerNotificationBlocage() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compte = new Compte();
        compte.setClient(client);
        compte.setNumCompte("CPT-001");

        when(emailTemplateEngine.render(eq("compte-bloque"), any())).thenReturn("<html>Compte bloqué</html>");

        emailService.envoyerNotificationBlocage(compte, "Fraude suspectée");

        verify(emailTemplateEngine).render(eq("compte-bloque"), any());
    }

    @Test
    void testEnvoyerAlerteCarte() {
        Client client = new Client();
        client.setEmail("test@soutra.ci");
        client.setNom("Soro");
        client.setPrenom("Emeric");

        Compte compte = new Compte();
        compte.setClient(client);

        CarteVisa carte = new CarteVisa();
        carte.setCompte(compte);
        carte.setNumeroCarte("4111222233334444");

        when(emailTemplateEngine.render(eq("carte-alerte"), any())).thenReturn("<html>Carte alerte</html>");

        emailService.envoyerAlerteCarte(carte, "Expiration imminente", "Votre carte expire bientôt.");

        verify(emailTemplateEngine).render(eq("carte-alerte"), any());
    }
}
