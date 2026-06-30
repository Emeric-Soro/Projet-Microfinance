package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.config.PasswordResetProperties;
import com.soutra.microfinance.entity.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Service d'envoi d'emails transactionnels.
 * - En mode logging (defaut), ne fait qu'ecrire le mail dans les logs.
 * - En mode SMTP reel, envoie via JavaMailSender (host configure dans application.properties).
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private static final String DELIVERY_MODE_LOGGING = "logging";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/06/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final PasswordResetProperties passwordResetProperties;
    private final EmailTemplateEngine emailTemplateEngine;
    private final String fromAddress;
    private final String deliveryMode;

    public EmailService(
            JavaMailSender mailSender,
            PasswordResetProperties passwordResetProperties,
            EmailTemplateEngine emailTemplateEngine,
            @Value("${app.mail.from}") String fromAddress,
            @Value("${app.mail.delivery-mode:logging}") String deliveryMode
    ) {
        this.mailSender = mailSender;
        this.passwordResetProperties = passwordResetProperties;
        this.emailTemplateEngine = emailTemplateEngine;
        this.fromAddress = fromAddress;
        this.deliveryMode = deliveryMode;
    }

    /**
     * Envoie un email de bienvenue suite à une inscription.
     */
    public void envoyerBienvenue(Client client) {
        if (client == null || client.getEmail() == null || client.getEmail().isBlank()) {
            return;
        }

        String prenom = client.getPrenom() != null ? client.getPrenom() : client.getNom();
        String sujet = "Bienvenue chez Soutra Core Banking";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("loginUrl", passwordResetProperties.getFrontendBaseUrl());

        String html = emailTemplateEngine.render("welcome", variables);
        String texte = "Bonjour " + prenom + ",\n\nBienvenue chez Soutra Core Banking !\nVotre compte a été créé avec succès.";

        envoyerMimeMessage(client.getEmail(), sujet, texte, html);
    }

    /**
     * Envoie un email contenant le lien de reinitialisation de mot de passe.
     */
    public void envoyerResetMotDePasse(Utilisateur utilisateur, String tokenClair) {
        if (utilisateur == null || utilisateur.getClient() == null) {
            return;
        }

        String emailDestinataire = utilisateur.getClient().getEmail();
        if (emailDestinataire == null || emailDestinataire.isBlank()) {
            LOGGER.warn("Impossible d'envoyer le mail de reset : pas d'email pour l'utilisateur {}", utilisateur.getLogin());
            return;
        }

        String urlReset = construireUrlReset(tokenClair);
        String sujet = "Réinitialisation de votre mot de passe - Soutra Core Banking";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", safePrenom(utilisateur));
        variables.put("urlReset", urlReset);
        variables.put("tokenValidity", passwordResetProperties.getTokenValidity().toMinutes());

        String contenuHtml = emailTemplateEngine.render("reset-password", variables);
        String contenuTexte = construireCorpsTexte(utilisateur, urlReset);

        envoyerMimeMessage(emailDestinataire, sujet, contenuTexte, contenuHtml);
    }

    /**
     * Envoie un e-mail contenant le code de sécurité OTP (2FA).
     */
    public void envoyerOtp(Utilisateur utilisateur, String codeOtp) {
        if (utilisateur == null || utilisateur.getClient() == null) {
            return;
        }

        String emailDestinataire = utilisateur.getClient().getEmail();
        if (emailDestinataire == null || emailDestinataire.isBlank()) {
            LOGGER.warn("Impossible d'envoyer l'OTP : pas d'email pour l'utilisateur {}", utilisateur.getLogin());
            return;
        }

        String sujet = "Votre code de validation OTP - Soutra Core Banking";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", safePrenom(utilisateur));
        variables.put("codeOtp", codeOtp);

        String contenuHtml = emailTemplateEngine.render("otp-2fa", variables);
        String contenuTexte = "Bonjour " + safePrenom(utilisateur) + ",\n\n"
                + "Pour finaliser votre authentification, veuillez saisir le code de sécurité à 6 chiffres ci-dessous :\n\n"
                + codeOtp + "\n\n"
                + "Ce code expire dans quelques minutes. Ne le partagez jamais.\n\n"
                + "L'equipe Soutra Core Banking";

        envoyerMimeMessage(emailDestinataire, sujet, contenuTexte, contenuHtml);
    }

    /**
     * Envoie une confirmation de virement émis.
     */
    public void envoyerConfirmationVirement(Compte compteSource, String nomBeneficiaire, String numCompteSource, String reference, BigDecimal montant) {
        if (compteSource == null || compteSource.getClient() == null || compteSource.getClient().getEmail() == null) {
            return;
        }

        String email = compteSource.getClient().getEmail();
        String prenom = safePrenomClient(compteSource.getClient());
        String sujet = "Confirmation de virement - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("montant", formaterMontant(montant));
        variables.put("nomBeneficiaire", nomBeneficiaire);
        variables.put("numCompteSource", numCompteSource);
        variables.put("reference", reference);
        variables.put("dateTransaction", LocalDateTime.now().format(DATE_ONLY_FORMATTER));

        String html = emailTemplateEngine.render("virement-emis", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre virement de " + montant + " FCFA vers " + nomBeneficiaire + " a été traité avec succès.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une alerte de virement reçu.
     */
    public void envoyerAlerteVirementRecu(Compte compteDest, String nomExpediteur, BigDecimal montant, BigDecimal nouveauSolde) {
        if (compteDest == null || compteDest.getClient() == null || compteDest.getClient().getEmail() == null) {
            return;
        }

        String email = compteDest.getClient().getEmail();
        String prenom = safePrenomClient(compteDest.getClient());
        String sujet = "Vous avez reçu un virement - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("montant", formaterMontant(montant));
        variables.put("nomExpediteur", nomExpediteur);
        variables.put("numCompte", compteDest.getNumCompte());
        variables.put("nouveauSolde", formaterMontant(nouveauSolde));

        String html = emailTemplateEngine.render("virement-recu", variables);
        String texte = "Bonjour " + prenom + ",\n\nVous avez reçu un virement de " + montant + " FCFA de la part de " + nomExpediteur + ".";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie le statut mis à jour d'un crédit.
     */
    public void envoyerStatutCredit(Credit credit, String statut, BigDecimal mensualite, String motifRejet) {
        if (credit == null || credit.getClient() == null || credit.getClient().getEmail() == null) {
            return;
        }

        Client client = credit.getClient();
        String email = client.getEmail();
        String prenom = safePrenomClient(client);
        String sujet = "Mise à jour de votre demande de crédit - Soutra";

        Map<String, Object> variables = new HashMap<>();
        variables.put("statut", statut);
        variables.put("prenom", prenom);
        variables.put("reference", credit.getReferenceCredit() != null ? credit.getReferenceCredit() : "");
        variables.put("montant", formaterMontant(credit.getMontantAccorde()));
        variables.put("taux", credit.getTauxInteretAnnuel());
        variables.put("duree", credit.getDureeMois());
        variables.put("mensualite", formaterMontant(mensualite));
        variables.put("motifRejet", motifRejet != null ? motifRejet : "");
        variables.put("numCompte", credit.getCompteDecaissement() != null ? credit.getCompteDecaissement().getNumCompte() : "");
        variables.put("creditDetailUrl", passwordResetProperties.getFrontendBaseUrl() + "/credits");

        String html = emailTemplateEngine.render("credit-statut", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre demande de crédit a pour nouveau statut : " + statut;

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie le statut mis à jour d'une demande de crédit (avant création de l'entité Crédit).
     */
    public void envoyerStatutDemandeCredit(DemandeCredit demande, String statut, String motifRejet) {
        if (demande == null || demande.getClient() == null || demande.getClient().getEmail() == null) {
            return;
        }

        Client client = demande.getClient();
        String email = client.getEmail();
        String prenom = safePrenomClient(client);
        String sujet = "Mise à jour de votre demande de crédit - Soutra";

        Map<String, Object> variables = new HashMap<>();
        variables.put("statut", statut);
        variables.put("prenom", prenom);
        variables.put("reference", demande.getReferenceDemande() != null ? demande.getReferenceDemande() : "");
        variables.put("montant", formaterMontant(demande.getMontantDemande()));
        variables.put("taux", demande.getProduitCredit() != null ? demande.getProduitCredit().getTauxInteretAnnuel() : BigDecimal.ZERO);
        variables.put("duree", demande.getDureeSouhaitee());
        variables.put("mensualite", "0");
        variables.put("motifRejet", motifRejet != null ? motifRejet : "");
        variables.put("numCompte", "");
        variables.put("creditDetailUrl", passwordResetProperties.getFrontendBaseUrl() + "/credits");

        String html = emailTemplateEngine.render("credit-statut", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre demande de crédit a pour nouveau statut : " + statut;

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie un rappel d'échéance de crédit (3 jours avant).
     */
    public void envoyerRappelEcheance(Credit credit, BigDecimal montantDue, LocalDate dateEcheance) {
        if (credit == null || credit.getClient() == null || credit.getClient().getEmail() == null) {
            return;
        }

        Client client = credit.getClient();
        String email = client.getEmail();
        String prenom = safePrenomClient(client);
        String sujet = "Rappel : Échéance de crédit dans 3 jours - Soutra";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("montantDue", formaterMontant(montantDue));
        variables.put("dateEcheance", dateEcheance.format(DATE_ONLY_FORMATTER));
        variables.put("numCompte", credit.getCompteDecaissement() != null ? credit.getCompteDecaissement().getNumCompte() : "votre compte courant");
        variables.put("creditDetailUrl", passwordResetProperties.getFrontendBaseUrl() + "/credits");

        String html = emailTemplateEngine.render("credit-rappel", variables);
        String texte = "Bonjour " + prenom + ",\n\nNous vous rappelons que votre échéance de crédit d'un montant de " + montantDue + " FCFA approche.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une alerte de retard de paiement de crédit.
     */
    public void envoyerAlerteRetard(Credit credit, BigDecimal montantImpaye, LocalDate dateEcheance, BigDecimal penalite) {
        if (credit == null || credit.getClient() == null || credit.getClient().getEmail() == null) {
            return;
        }

        Client client = credit.getClient();
        String email = client.getEmail();
        String prenom = safePrenomClient(client);
        String sujet = "⚠️ Retard de paiement - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("montantImpaye", formaterMontant(montantImpaye));
        variables.put("dateEcheance", dateEcheance.format(DATE_ONLY_FORMATTER));
        variables.put("penalite", formaterMontant(penalite));

        String html = emailTemplateEngine.render("credit-retard", variables);
        String texte = "Bonjour " + prenom + ",\n\nNous constatons un retard de paiement d'un montant de " + montantImpaye + " FCFA sur votre crédit.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une confirmation de dépôt.
     */
    public void envoyerConfirmationDepot(Compte compte, BigDecimal montant, BigDecimal nouveauSolde) {
        if (compte == null || compte.getClient() == null || compte.getClient().getEmail() == null) {
            return;
        }

        String email = compte.getClient().getEmail();
        String prenom = safePrenomClient(compte.getClient());
        String sujet = "Confirmation de dépôt - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("montant", formaterMontant(montant));
        variables.put("numCompte", compte.getNumCompte());
        variables.put("nouveauSolde", formaterMontant(nouveauSolde));

        String html = emailTemplateEngine.render("depot-confirme", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre dépôt de " + montant + " FCFA sur le compte " + compte.getNumCompte() + " a bien été enregistré.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une confirmation de retrait.
     */
    public void envoyerConfirmationRetrait(Compte compte, BigDecimal montant, BigDecimal nouveauSolde) {
        if (compte == null || compte.getClient() == null || compte.getClient().getEmail() == null) {
            return;
        }

        String email = compte.getClient().getEmail();
        String prenom = safePrenomClient(compte.getClient());
        String sujet = "Confirmation de retrait - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("montant", formaterMontant(montant));
        variables.put("numCompte", compte.getNumCompte());
        variables.put("nouveauSolde", formaterMontant(nouveauSolde));

        String html = emailTemplateEngine.render("retrait-confirme", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre retrait de " + montant + " FCFA sur le compte " + compte.getNumCompte() + " a bien été exécuté.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une alerte de connexion suspecte.
     */
    public void envoyerAlerteConnexionSuspecte(Utilisateur utilisateur, String ipAddress, String localisation, int nbTentatives) {
        if (utilisateur == null || utilisateur.getClient() == null || utilisateur.getClient().getEmail() == null) {
            return;
        }

        String email = utilisateur.getClient().getEmail();
        String prenom = safePrenom(utilisateur);
        String sujet = "🔒 Alerte sécurité : connexion suspecte - Soutra";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("dateTentative", LocalDateTime.now().format(DATE_FORMATTER));
        variables.put("ipAddress", ipAddress);
        variables.put("localisation", localisation);
        variables.put("nbTentatives", nbTentatives);
        variables.put("changePasswordUrl", passwordResetProperties.getFrontendBaseUrl() + "/auth/forgot-password");

        String html = emailTemplateEngine.render("connexion-suspecte", variables);
        String texte = "Bonjour " + prenom + ",\n\nUne connexion suspecte a été détectée sur votre compte.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une confirmation de changement de mot de passe.
     */
    public void envoyerConfirmationChangementMotDePasse(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getClient() == null || utilisateur.getClient().getEmail() == null) {
            return;
        }

        String email = utilisateur.getClient().getEmail();
        String prenom = safePrenom(utilisateur);
        String sujet = "Mot de passe modifié avec succès - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("dateModification", LocalDateTime.now().format(DATE_ONLY_FORMATTER));

        String html = emailTemplateEngine.render("password-changed", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre mot de passe a bien été modifié.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie le relevé de compte périodique avec pièce jointe PDF.
     */
    public void envoyerReleveCompte(Compte compte, LocalDate du, LocalDate au, byte[] pdfContent) {
        if (compte == null || compte.getClient() == null || compte.getClient().getEmail() == null) {
            return;
        }

        String email = compte.getClient().getEmail();
        String prenom = safePrenomClient(compte.getClient());
        String sujet = String.format("Votre relevé de compte %s - Soutra", du.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRANCE)));

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("dateDebut", du.format(DATE_ONLY_FORMATTER));
        variables.put("dateFin", au.format(DATE_ONLY_FORMATTER));
        variables.put("numCompte", compte.getNumCompte());
        variables.put("soldeFin", formaterMontant(compte.getSolde()));

        String html = emailTemplateEngine.render("releve-compte", variables);
        String texte = "Bonjour " + prenom + ",\n\nVeuillez trouver ci-joint votre relevé de compte périodique.";
        String pjName = "Releve_Compte_" + compte.getNumCompte() + "_" + du.format(DATE_ONLY_FORMATTER) + ".pdf";

        envoyerMimeMessageAvecPieceJointe(email, sujet, texte, html, pjName, pdfContent);
    }

    /**
     * Envoie une notification de blocage de compte.
     */
    public void envoyerNotificationBlocage(Compte compte, String motif) {
        if (compte == null || compte.getClient() == null || compte.getClient().getEmail() == null) {
            return;
        }

        String email = compte.getClient().getEmail();
        String prenom = safePrenomClient(compte.getClient());
        String sujet = "🔒 Votre compte a été temporairement bloqué - Soutra";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("numCompte", compte.getNumCompte());
        variables.put("motif", motif != null ? motif : "Raison administrative");

        String html = emailTemplateEngine.render("compte-bloque", variables);
        String texte = "Bonjour " + prenom + ",\n\nVotre compte " + compte.getNumCompte() + " a été temporairement bloqué.";

        envoyerMimeMessage(email, sujet, texte, html);
    }

    /**
     * Envoie une alerte de carte bancaire.
     */
    public void envoyerAlerteCarte(CarteVisa carte, String alerteType, String details) {
        if (carte == null || carte.getCompte() == null || carte.getCompte().getClient() == null || carte.getCompte().getClient().getEmail() == null) {
            return;
        }

        String email = carte.getCompte().getClient().getEmail();
        String prenom = safePrenomClient(carte.getCompte().getClient());
        String sujet = "Alerte carte bancaire - Soutra Core Banking";

        Map<String, Object> variables = new HashMap<>();
        variables.put("prenom", prenom);
        variables.put("numeroCarte", masquerNumeroCarte(carte.getNumeroCarte()));
        variables.put("alerteType", alerteType);
        variables.put("details", details);

        String html = emailTemplateEngine.render("carte-alerte", variables);
        String texte = "Bonjour " + prenom + ",\n\nUne alerte concernant votre carte Visa a été émise : " + alerteType;

        envoyerMimeMessage(email, sujet, texte, html);
    }

    private void envoyerMimeMessage(String to, String subject, String text, String html) {
        if (DELIVERY_MODE_LOGGING.equalsIgnoreCase(deliveryMode) || mailSender == null) {
            LOGGER.info("[EMAIL LOG] To={} | Subject={}", to, subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, html);
            mailSender.send(message);
            LOGGER.info("Email '{}' envoyé à {}", subject, to);
        } catch (MessagingException ex) {
            LOGGER.error("Echec de l'envoi de l'email '{}' a {}", subject, to, ex);
        }
    }

    private void envoyerMimeMessageAvecPieceJointe(String to, String subject, String text, String html, String attachmentName, byte[] attachmentBytes) {
        if (DELIVERY_MODE_LOGGING.equalsIgnoreCase(deliveryMode) || mailSender == null) {
            LOGGER.info("[EMAIL LOG] (Avec pièce jointe: {}) To={} | Subject={}", attachmentName, to, subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, html);
            helper.addAttachment(attachmentName, new ByteArrayResource(attachmentBytes));
            mailSender.send(message);
            LOGGER.info("Email '{}' avec pièce jointe '{}' envoyé à {}", subject, attachmentName, to);
        } catch (Exception ex) {
            LOGGER.error("Echec de l'envoi de l'email avec PJ '{}' a {}", subject, to, ex);
        }
    }

    private String construireUrlReset(String tokenClair) {
        return passwordResetProperties.getFrontendBaseUrl()
                + passwordResetProperties.getResetPath()
                + "?token=" + URLEncoder.encode(tokenClair, StandardCharsets.UTF_8);
    }

    private String construireCorpsTexte(Utilisateur utilisateur, String urlReset) {
        return "Bonjour "
                + safePrenom(utilisateur)
                + ",\n\n"
                + "Une demande de reinitialisation de mot de passe a ete effectuee for votre compte.\n"
                + "Si vous etes a l'origine de cette demande, cliquez sur le lien ci-dessous :\n\n"
                + urlReset + "\n\n"
                + "Ce lien expire dans " + passwordResetProperties.getTokenValidity().toMinutes() + " minutes.\n"
                + "Si vous n'etes pas a l'origine de cette demande, ignorez ce message.\n\n"
                + "L'equipe Soutra Core Banking";
    }

    private String safePrenom(Utilisateur utilisateur) {
        if (utilisateur.getClient() != null && utilisateur.getClient().getPrenom() != null) {
            return utilisateur.getClient().getPrenom();
        }
        return utilisateur.getLogin();
    }

    private String safePrenomClient(Client client) {
        return client.getPrenom() != null ? client.getPrenom() : client.getNom();
    }

    private String formaterMontant(BigDecimal montant) {
        if (montant == null) return "0";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRANCE);
        return nf.format(montant);
    }

    private String masquerNumeroCarte(String numeroCarte) {
        if (numeroCarte == null || numeroCarte.length() < 16) {
            return "4XXX XXXX XXXX XXXX";
        }
        return numeroCarte.substring(0, 4) + " XXXX XXXX " + numeroCarte.substring(12);
    }
}
