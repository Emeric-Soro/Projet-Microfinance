package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.config.PasswordResetProperties;
import com.soutra.microfinance.entity.Utilisateur;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service d'envoi d'emails transactionnels.
 * - En mode logging (defaut), ne fait qu'ecrire le mail dans les logs.
 * - En mode SMTP reel, envoie via JavaMailSender (host configure dans application.properties).
 *
 * <p>Separe de NotificationService qui gere les SMS via le canal mobile.
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private static final String DELIVERY_MODE_LOGGING = "logging";

    private final JavaMailSender mailSender;
    private final PasswordResetProperties passwordResetProperties;
    private final String fromAddress;
    private final String deliveryMode;

    public EmailService(
            JavaMailSender mailSender,
            PasswordResetProperties passwordResetProperties,
            @Value("${app.mail.from}") String fromAddress,
            @Value("${app.mail.delivery-mode:logging}") String deliveryMode
    ) {
        this.mailSender = mailSender;
        this.passwordResetProperties = passwordResetProperties;
        this.fromAddress = fromAddress;
        this.deliveryMode = deliveryMode;
    }

    /**
     * Envoie un email contenant le lien de reinitialisation de mot de passe.
     *
     * @param utilisateur l'utilisateur qui demande la reinitialisation
     * @param tokenClair  le token en clair (a inclure dans l'URL envoyee par mail)
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
        String sujet = "Reinitialisation de votre mot de passe - Soutra Core Banking";
        String contenuHtml = construireCorpsHtml(utilisateur, urlReset);
        String contenuTexte = construireCorpsTexte(utilisateur, urlReset);

        if (DELIVERY_MODE_LOGGING.equalsIgnoreCase(deliveryMode) || mailSender == null) {
            LOGGER.info("[EMAIL LOG] To={} | Subject={} | Lien de reinitialisation={}",
                    emailDestinataire, sujet, urlReset);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(emailDestinataire);
            helper.setSubject(sujet);
            helper.setText(contenuTexte, contenuHtml);
            mailSender.send(message);
            LOGGER.info("Email de reinitialisation envoye a {}", emailDestinataire);
        } catch (MessagingException ex) {
            LOGGER.error("Echec de l'envoi de l'email de reinitialisation a {}", emailDestinataire, ex);
        }
    }

    private String construireUrlReset(String tokenClair) {
        String url = passwordResetProperties.getFrontendBaseUrl()
                + passwordResetProperties.getResetPath()
                + "?token=" + URLEncoder.encode(tokenClair, StandardCharsets.UTF_8);
        return url;
    }

    private String construireCorpsTexte(Utilisateur utilisateur, String urlReset) {
        return "Bonjour "
                + safePrenom(utilisateur)
                + ",\n\n"
                + "Une demande de reinitialisation de mot de passe a ete effectuee pour votre compte.\n"
                + "Si vous etes a l'origine de cette demande, cliquez sur le lien ci-dessous :\n\n"
                + urlReset + "\n\n"
                + "Ce lien expire dans " + passwordResetProperties.getTokenValidity().toMinutes() + " minutes.\n"
                + "Si vous n'etes pas a l'origine de cette demande, ignorez ce message.\n\n"
                + "L'equipe Soutra Core Banking";
    }

    private String construireCorpsHtml(Utilisateur utilisateur, String urlReset) {
        return "<!DOCTYPE html>"
                + "<html><body style='font-family:Arial,sans-serif;color:#1f2937;'>"
                + "<h2 style='color:#0f766e;'>Reinitialisation de votre mot de passe</h2>"
                + "<p>Bonjour " + safePrenom(utilisateur) + ",</p>"
                + "<p>Une demande de reinitialisation de mot de passe a ete effectuee pour votre compte.</p>"
                + "<p>Si vous etes a l'origine de cette demande, cliquez sur le bouton ci-dessous :</p>"
                + "<p style='margin:24px 0;'><a href='" + urlReset + "' "
                + "style='background:#0f766e;color:#ffffff;padding:12px 24px;text-decoration:none;border-radius:6px;'>"
                + "Reinitialiser mon mot de passe</a></p>"
                + "<p>Ou copiez ce lien dans votre navigateur :<br/><span style='word-break:break-all;'>"
                + urlReset + "</span></p>"
                + "<p><strong>Ce lien expire dans "
                + passwordResetProperties.getTokenValidity().toMinutes()
                + " minutes.</strong></p>"
                + "<p>Si vous n'etes pas a l'origine de cette demande, ignorez ce message.</p>"
                + "<hr/><p style='color:#6b7280;font-size:12px;'>L'equipe Soutra Core Banking</p>"
                + "</body></html>";
    }

    private String safePrenom(Utilisateur utilisateur) {
        if (utilisateur.getClient() != null && utilisateur.getClient().getPrenom() != null) {
            return utilisateur.getClient().getPrenom();
        }
        return utilisateur.getLogin();
    }
}
