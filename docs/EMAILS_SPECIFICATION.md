# Spécification des Emails - Soutra Core Banking

> **Date** : 30 Juin 2026  
> **Statut** : Planning  
> **Auteur** : Sisyphus  
> **Objectif** : Centraliser tous les emails du système avant implémentation

---

## Table des matières

1. [Configuration MailHog (Tests)](#1-configuration-mailhog-tests)
2. [Emails Existants (Implémentés)](#2-emails-existants-implémentés)
3. [Emails Planifiés (À implémenter)](#3-emails-planifiés-à-implémenter)
4. [Structure des Templates](#4-structure-des-templates)
5. [Matrice de déclenchement](#5-matrice-de-déclenchement)
6. [Plan d'implémentation](#6-plan-dimplémentation)

---

## 1. Configuration MailHog (Tests)

### 1.1 Ajout dans `docker-compose.yml`

```yaml
services:
  mailhog:
    image: mailhog/mailhog:latest
    container_name: soutra-mailhog
    ports:
      - "1025:1025"   # SMTP (pour l'envoi)
      - "8025:8025"   # Web UI (pour consulter les emails)
    environment:
      - MH_STORAGE=maildir
      - MH_MAILDIR_PATH=/maildir
    volumes:
      - mailhog-data:/maildir
    networks:
      - backend-network

volumes:
  mailhog-data:
```

### 1.2 Configuration `application-dev.properties`

```properties
# ==========================================
# SMTP / EMAIL (Développement via MailHog)
# ==========================================
spring.mail.host=mailhog
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.starttls.required=false
app.mail.from=noreply@soutra-core-banking.local
app.mail.delivery-mode=smtp
```

### 1.3 Variables d'environnement (`.env`)

```env
# MailHog (développement)
SMTP_HOST=mailhog
SMTP_PORT=1025
SMTP_AUTH=false
SMTP_STARTTLS=false
SMTP_STARTTLS_REQUIRED=false
APP_MAIL_DELIVERY_MODE=smtp
APP_MAIL_FROM=noreply@soutra-core-banking.local
```

### 1.4 Accès Web UI

- **URL** : http://localhost:8025
- **Fonctionnalités** :
  - Liste de tous les emails envoyés
  - Prévisualisation HTML
  - Voir les headers
  - Recherche par destinataire/sujet
  - Suppression individuelle ou globale

---

## 2. Emails Existants (Implémentés)

### 2.1 Réinitialisation de mot de passe

| Propriété    | Valeur                                              |
| ------------ | --------------------------------------------------- |
| **ID**         | `EMAIL_RESET_PASSWORD`                                |
| **Statut**     | ✅ Implémenté                                        |
| **Service**    | `EmailService.envoyerResetMotDePasse()`               |
| **Déclencheur** | `POST /api/v1/auth/mot-de-passe/oublie`               |
| **Destinataire** | `utilisateur.getClient().getEmail()`                  |
| **Sujet**      | `Réinitialisation de votre mot de passe - Soutra`     |
| **Validité**   | 30 minutes (configurable via `app.password-reset.*`) |

**Template HTML :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Réinitialisation de votre mot de passe</h2>
  <p>Bonjour {prenom},</p>
  <p>Une demande de réinitialisation de mot de passe a été effectuée pour votre compte.</p>
  <p>Si vous êtes à l'origine de cette demande, cliquez sur le bouton ci-dessous :</p>
  <p style="margin:24px 0;">
    <a href="{urlReset}" 
       style="background:#0f766e;color:#ffffff;padding:12px 24px;text-decoration:none;border-radius:6px;">
      Réinitialiser mon mot de passe
    </a>
  </p>
  <p>Ou copiez ce lien dans votre navigateur :<br/>
    <span style="word-break:break-all;">{urlReset}</span>
  </p>
  <p><strong>Ce lien expire dans {tokenValidity} minutes.</strong></p>
  <p>Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.</p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

**Template Texte :**
```
Bonjour {prenom},

Une demande de réinitialisation de mot de passe a été effectuée pour votre compte.
Si vous êtes à l'origine de cette demande, cliquez sur le lien ci-dessous :

{urlReset}

Ce lien expire dans {tokenValidity} minutes.
Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.

L'équipe Soutra Core Banking
```

---

### 2.2 Code OTP (2FA)

| Propriété    | Valeur                                   |
| ------------ | ---------------------------------------- |
| **ID**         | `EMAIL_OTP_2FA`                            |
| **Statut**     | ✅ Implémenté                             |
| **Service**    | `EmailService.envoyerOtp()`                |
| **Déclencheur** | `POST /api/v1/auth/login` (si 2FA activé)  |
| **Destinataire** | `utilisateur.getClient().getEmail()`       |
| **Sujet**      | `Votre code de validation OTP - Soutra`    |
| **Validité**   | 5 minutes (configurable)                 |

**Template HTML :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Votre code de validation OTP</h2>
  <p>Bonjour {prenom},</p>
  <p>Pour finaliser votre authentification, veuillez saisir le code de sécurité à 6 chiffres ci-dessous :</p>
  <p style="margin:24px 0; font-size: 24px; font-weight: bold; letter-spacing: 4px; color: #0f766e;">
    {codeOtp}
  </p>
  <p>Ce code expire dans quelques minutes. Ne le partagez jamais.</p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

**Template Texte :**
```
Bonjour {prenom},

Pour finaliser votre authentification, veuillez saisir le code de sécurité à 6 chiffres ci-dessous :

{codeOtp}

Ce code expire dans quelques minutes. Ne le partagez jamais.

L'équipe Soutra Core Banking
```

---

## 3. Emails Planifiés (À implémenter)

### 3.1 Bienvenue / Inscription

| Propriété    | Valeur                               |
| ------------ | ------------------------------------ |
| **ID**         | `EMAIL_WELCOME`                        |
| **Statut**     | ❌ À implémenter                       |
| **Service**    | `EmailService.envoyerBienvenue()`      |
| **Déclencheur** | `POST /api/v1/auth/register` (succès)  |
| **Destinataire** | `client.getEmail()`                    |
| **Sujet**      | `Bienvenue chez Soutra Core Banking`   |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Bienvenue chez Soutra Core Banking !</h2>
  <p>Bonjour {prenom},</p>
  <p>Votre compte a été créé avec succès. Vous pouvez maintenant :</p>
  <ul>
    <li>Consulter vos comptes et soldes</li>
    <li>Effectuer des virements</li>
    <li>Gérer vos épargnes</li>
    <li>Faire des demandes de crédit</li>
  </ul>
  <p style="margin:24px 0;">
    <a href="{loginUrl}" 
       style="background:#0f766e;color:#ffffff;padding:12px 24px;text-decoration:none;border-radius:6px;">
      Accéder à mon espace
    </a>
  </p>
  <p>Si vous avez des questions, contactez notre support.</p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.2 Confirmation de virement émis

| Propriété    | Valeur                                            |
| ------------ | ------------------------------------------------- |
| **ID**         | `EMAIL_VIREMENT_EMIS`                               |
| **Statut**     | ❌ À implémenter                                    |
| **Service**    | `EmailService.envoyerConfirmationVirement()`        |
| **Déclencheur** | `TransactionServiceImpl.effectuerVirement()` (succès) |
| **Destinataire** | `compteSource.getClient().getEmail()`               |
| **Sujet**      | `Confirmation de virement - Soutra Core Banking`    |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Virement effectué avec succès</h2>
  <p>Bonjour {prenom},</p>
  <p>Votre virement a été traité avec succès.</p>
  <table style="width:100%;border-collapse:collapse;margin:20px 0;">
    <tr style="background:#f3f4f6;">
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Montant</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{montant} FCFA</td>
    </tr>
    <tr>
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Bénéficiaire</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{nomBeneficiaire}</td>
    </tr>
    <tr style="background:#f3f4f6;">
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Compte débité</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{numCompteSource}</td>
    </tr>
    <tr>
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Référence</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{reference}</td>
    </tr>
    <tr style="background:#f3f4f6;">
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Date</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{dateTransaction}</td>
    </tr>
  </table>
  <p>Si vous n'êtes pas à l'origine de cette opération, contactez immédiatement notre support.</p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.3 Alerte virement reçu

| Propriété    | Valeur                                          |
| ------------ | ----------------------------------------------- |
| **ID**         | `EMAIL_VIREMENT_RECU`                             |
| **Statut**     | ❌ À implémenter                                  |
| **Service**    | `EmailService.envoyerAlerteVirementRecu()`        |
| **Déclencheur** | `VirementEffectueEvent` (via `NotificationEventListener`) |
| **Destinataire** | `compteDestination.getClient().getEmail()`        |
| **Sujet**      | `Vous avez reçu un virement - Soutra Core Banking` |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Virement reçu</h2>
  <p>Bonjour {prenom},</p>
  <p>Vous avez reçu un virement sur votre compte.</p>
  <div style="background:#f0fdf4;border-left:4px solid #22c55e;padding:16px;margin:20px 0;">
    <p style="font-size:24px;font-weight:bold;color:#16a34a;margin:0;">+{montant} FCFA</p>
    <p style="color:#6b7280;margin:8px 0 0 0;">Compte : {numCompte}</p>
  </div>
  <p>Expéditeur : {nomExpediteur}</p>
  <p>Votre nouveau solde : {nouveauSolde} FCFA</p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.4 Demande de crédit - Statut mis à jour

| Propriété    | Valeur                                          |
| ------------ | ----------------------------------------------- |
| **ID**         | `EMAIL_CREDIT_STATUT`                             |
| **Statut**     | ❌ À implémenter                                  |
| **Service**    | `EmailService.envoyerStatutCredit()`              |
| **Déclencheur** | Changement de statut de demande de crédit         |
| **Destinataire** | `credit.getClient().getEmail()`                   |
| **Sujet**      | `Mise à jour de votre demande de crédit - Soutra` |

**Sous-types :**

| Statut       | Sujet                                      | Contenu principal                |
| ------------ | ------------------------------------------ | -------------------------------- |
| `EN_COURS`     | Demande de crédit en cours de traitement   | Récapitulatif de la demande      |
| `APPROUVEE`    | Votre demande de crédit a été approuvée ✅  | Montant, taux, échéancier        |
| `REJETEE`      | Votre demande de crédit n'a pas été retenue | Motif du rejet                   |
| `DECAISSEMENT` | Décaissement de votre crédit effectué      | Montant versé, compte crédité    |

**Template HTML (APPROUVEE - proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Demande de crédit approuvée ✅</h2>
  <p>Bonjour {prenom},</p>
  <p>Bonne nouvelle ! Votre demande de crédit a été approuvée.</p>
  <div style="background:#f0fdf4;border-left:4px solid #22c55e;padding:16px;margin:20px 0;">
    <p><strong>Montant accordé :</strong> {montant} FCFA</p>
    <p><strong>Taux d'intérêt :</strong> {taux}%</p>
    <p><strong>Durée :</strong> {duree} mois</p>
    <p><strong>Mensualité :</strong> {mensualite} FCFA</p>
  </div>
  <p style="margin:24px 0;">
    <a href="{creditDetailUrl}" 
       style="background:#0f766e;color:#ffffff;padding:12px 24px;text-decoration:none;border-radius:6px;">
      Voir les détails
    </a>
  </p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.5 Rappel d'échéance de crédit

| Propriété    | Valeur                                           |
| ------------ | ------------------------------------------------ |
| **ID**         | `EMAIL_CREDIT_RAPPEL_ECHEANCE`                     |
| **Statut**     | ❌ À implémenter                                   |
| **Service**    | `EmailService.envoyerRappelEcheance()`             |
| **Déclencheur** | Cron job (3 jours avant échéance)                  |
| **Destinataire** | `credit.getClient().getEmail()`                    |
| **Sujet**      | `Rappel : Échéance de crédit dans 3 jours - Soutra` |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#f59e0b;">⚠️ Rappel d'échéance</h2>
  <p>Bonjour {prenom},</p>
  <p>Nous vous rappelons que votre échéance de crédit approche.</p>
  <div style="background:#fef3c7;border-left:4px solid #f59e0b;padding:16px;margin:20px 0;">
    <p><strong>Montant dû :</strong> {montantDue} FCFA</p>
    <p><strong>Date d'échéance :</strong> {dateEcheance}</p>
    <p><strong>Compte à débiter :</strong> {numCompte}</p>
  </div>
  <p>Veuillez vous assurer que votre compte dispose d'un solde suffisant.</p>
  <p style="margin:24px 0;">
    <a href="{creditDetailUrl}" 
       style="background:#0f766e;color:#ffffff;padding:12px 24px;text-decoration:none;border-radius:6px;">
      Voir mon crédit
    </a>
  </p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.6 Alerte retard de paiement

| Propriété    | Valeur                                         |
| ------------ | ---------------------------------------------- |
| **ID**         | `EMAIL_CREDIT_RETARD`                            |
| **Statut**     | ❌ À implémenter                                 |
| **Service**    | `EmailService.envoyerAlerteRetard()`             |
| **Déclencheur** | Cron job (après date d'échéance, paiement manquant) |
| **Destinataire** | `credit.getClient().getEmail()`                  |
| **Sujet**      | `⚠️ Retard de paiement - Soutra Core Banking`   |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#dc2626;">⚠️ Retard de paiement</h2>
  <p>Bonjour {prenom},</p>
  <p>Nous constatons un retard de paiement sur votre crédit.</p>
  <div style="background:#fef2f2;border-left:4px solid #dc2626;padding:16px;margin:20px 0;">
    <p><strong>Montant impayé :</strong> {montantImpaye} FCFA</p>
    <p><strong>Date d'échéance dépassée :</strong> {dateEcheance}</p>
    <p><strong>Pénalité de retard :</strong> {penalite} FCFA</p>
  </div>
  <p>Nous vous invitons à régulariser votre situation dans les plus brefs délais.</p>
  <p>Contactez notre service crédit si vous rencontrez des difficultés.</p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.7 Confirmation de dépôt

| Propriété    | Valeur                                    |
| ------------ | ----------------------------------------- |
| **ID**         | `EMAIL_DEPOT_CONFIRME`                      |
| **Statut**     | ❌ À implémenter                            |
| **Service**    | `EmailService.envoyerConfirmationDepot()`   |
| **Déclencheur** | `TransactionServiceImpl` (dépôt réussi)    |
| **Destinataire** | `compte.getClient().getEmail()`             |
| **Sujet**      | `Confirmation de dépôt - Soutra Core Banking` |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#0f766e;">Dépôt confirmé</h2>
  <p>Bonjour {prenom},</p>
  <p>Votre dépôt a été enregistré avec succès.</p>
  <table style="width:100%;border-collapse:collapse;margin:20px 0;">
    <tr style="background:#f3f4f6;">
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Montant</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">+{montant} FCFA</td>
    </tr>
    <tr>
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Compte</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{numCompte}</td>
    </tr>
    <tr style="background:#f3f4f6;">
      <td style="padding:12px;border:1px solid #e5e7eb;font-weight:bold;">Nouveau solde</td>
      <td style="padding:12px;border:1px solid #e5e7eb;">{nouveauSolde} FCFA</td>
    </tr>
  </table>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.8 Confirmation de retrait

| Propriété    | Valeur                                      |
| ------------ | ------------------------------------------- |
| **ID**         | `EMAIL_RETRAIT_CONFIRME`                      |
| **Statut**     | ❌ À implémenter                              |
| **Service**    | `EmailService.envoyerConfirmationRetrait()`   |
| **Déclencheur** | `TransactionServiceImpl` (retrait réussi)    |
| **Destinataire** | `compte.getClient().getEmail()`               |
| **Sujet**      | `Confirmation de retrait - Soutra Core Banking` |

---

### 3.9 Alerte connexion suspecte

| Propriété    | Valeur                                             |
| ------------ | -------------------------------------------------- |
| **ID**         | `EMAIL_CONNEXION_SUSPECTE`                           |
| **Statut**     | ❌ À implémenter                                     |
| **Service**    | `EmailService.envoyerAlerteConnexionSuspecte()`      |
| **Déclencheur** | Échec d'authentification répété                      |
| **Destinataire** | `utilisateur.getClient().getEmail()`                 |
| **Sujet**      | `🔒 Alerte sécurité : connexion suspecte - Soutra`   |

**Template HTML (proposé) :**
```html
<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;color:#1f2937;">
  <h2 style="color:#dc2626;">🔒 Alerte sécurité</h2>
  <p>Bonjour {prenom},</p>
  <p>Une connexion suspecte a été détectée sur votre compte.</p>
  <div style="background:#fef2f2;border-left:4px solid #dc2626;padding:16px;margin:20px 0;">
    <p><strong>Date :</strong> {dateTentative}</p>
    <p><strong>Adresse IP :</strong> {ipAddress}</p>
    <p><strong>Localisation :</strong> {localisation}</p>
    <p><strong>Tentatives échouées :</strong> {nbTentatives}</p>
  </div>
  <p>Si ce n'est pas vous, nous vous recommandons de :</p>
  <ol>
    <li>Changer immédiatement votre mot de passe</li>
    <li>Activer l'authentification à deux facteurs</li>
    <li>Contacter notre support</li>
  </ol>
  <p style="margin:24px 0;">
    <a href="{changePasswordUrl}" 
       style="background:#dc2626;color:#ffffff;padding:12px 24px;text-decoration:none;border-radius:6px;">
      Changer mon mot de passe
    </a>
  </p>
  <hr/>
  <p style="color:#6b7280;font-size:12px;">L'équipe Soutra Core Banking</p>
</body>
</html>
```

---

### 3.10 Confirmation de changement de mot de passe

| Propriété    | Valeur                                                |
| ------------ | ----------------------------------------------------- |
| **ID**         | `EMAIL_PASSWORD_CHANGED`                                |
| **Statut**     | ❌ À implémenter                                        |
| **Service**    | `EmailService.envoyerConfirmationChangementMotDePasse()` |
| **Déclencheur** | Succès de réinitialisation du mot de passe              |
| **Destinataire** | `utilisateur.getClient().getEmail()`                    |
| **Sujet**      | `Mot de passe modifié avec succès - Soutra Core Banking` |

---

### 3.11 Relevé de compte périodique

| Propriété    | Valeur                                       |
| ------------ | -------------------------------------------- |
| **ID**         | `EMAIL_RELEVE_COMPTE`                          |
| **Statut**     | ❌ À implémenter                               |
| **Service**    | `EmailService.envoyerReleveCompte()`           |
| **Déclencheur** | Cron job (fin de mois) ou demande client       |
| **Destinataire** | `compte.getClient().getEmail()`                |
| **Sujet**      | `Votre relevé de compte {mois} - Soutra`       |
| **Pièce jointe** | PDF du relevé                                  |

---

### 3.12 Notification de blocage de compte

| Propriété    | Valeur                                          |
| ------------ | ----------------------------------------------- |
| **ID**         | `EMAIL_COMPTE_BLOQUE`                             |
| **Statut**     | ❌ À implémenter                                  |
| **Service**    | `EmailService.envoyerNotificationBlocage()`       |
| **Déclencheur** | Blocage administratif du compte                   |
| **Destinataire** | `compte.getClient().getEmail()`                   |
| **Sujet**      | `🔒 Votre compte a été temporairement bloqué - Soutra` |

---

### 3.13 Alerte carte bancaire

| Propriété    | Valeur                                        |
| ------------ | --------------------------------------------- |
| **ID**         | `EMAIL_CARTE_ALERTE`                            |
| **Statut**     | ❌ À implémenter                                |
| **Service**    | `EmailService.envoyerAlerteCarte()`             |
| **Déclencheur** | Activité suspecte sur carte, expiration proche |
| **Destinataire** | `carte.getClient().getEmail()`                  |
| **Sujet**      | `Alerte carte bancaire - Soutra Core Banking`   |

---

## 4. Structure des Templates

### 4.1 Conventions de nommage

```
EMAIL_{DOMAINE}_{ACTION}
```

Exemples :
- `EMAIL_AUTH_RESET_PASSWORD`
- `EMAIL_AUTH_OTP_2FA`
- `EMAIL_CREDIT_APPROUVEE`
- `EMAIL_TRANSACTION_VIREMENT_EMIS`

### 4.2 Variables disponibles

| Variable         | Description                           | Source                              |
| ---------------- | ------------------------------------- | ----------------------------------- |
| `{prenom}`         | Prénom du client                      | `client.getPrenom()`                  |
| `{nom}`            | Nom du client                         | `client.getNom()`                     |
| `{email}`          | Email du client                       | `client.getEmail()`                   |
| `{codeClient}`     | Code unique du client                 | `client.getCodeClient()`              |
| `{montant}`        | Montant formaté                       | `NumberFormat`                        |
| `{numCompte}`      | Numéro de compte                      | `compte.getNumCompte()`               |
| `{solde}`          | Solde actuel                          | `compte.getSolde()`                   |
| `{dateTransaction}` | Date formatée                        | `LocalDateTime.format()`              |
| `{reference}`      | Référence transaction                 | `transaction.getReference()`          |
| `{loginUrl}`       | URL de connexion                      | Config `app.password-reset.*`         |
| `{supportEmail}`   | Email de support                      | Config                               |
| `{supportPhone}`   | Téléphone de support                  | Config                               |

### 4.3 Style visuel commun

```css
/* Couleurs principales */
--primary: #0f766e;      /* Teal - boutons CTA */
--success: #22c55e;      /* Green - confirmations */
--warning: #f59e0b;      /* Amber - rappels */
--danger: #dc2626;       /* Red - alertes */
--text: #1f2937;         /* Gray-800 - texte principal */
--text-light: #6b7280;   /* Gray-500 - texte secondaire */
--bg-light: #f3f4f6;     /* Gray-100 - arrière-plan tableau */

/* Typographie */
font-family: Arial, sans-serif;
font-size: 16px;
line-height: 1.5;

/* Bouton CTA */
background: #0f766e;
color: #ffffff;
padding: 12px 24px;
text-decoration: none;
border-radius: 6px;
display: inline-block;
```

---

## 5. Matrice de déclenchement

| Événement                           | Email                           | SMS | Push | In-App |
| ----------------------------------- | ------------------------------- | --- | ---- | ------ |
| Inscription                         | `EMAIL_WELCOME`                   | ✅  | ❌   | ✅     |
| Réinitialisation MDP                | `EMAIL_RESET_PASSWORD`            | ❌  | ❌   | ❌     |
| OTP 2FA                             | `EMAIL_OTP_2FA`                   | ✅  | ❌   | ❌     |
| Virement émis                       | `EMAIL_VIREMENT_EMIS`             | ❌  | ❌   | ✅     |
| Virement reçu                       | `EMAIL_VIREMENT_RECU`             | ✅  | ❌   | ✅     |
| Dépôt                               | `EMAIL_DEPOT_CONFIRME`            | ❌  | ❌   | ✅     |
| Retrait                             | `EMAIL_RETRAIT_CONFIRME`          | ❌  | ❌   | ✅     |
| Demande crédit                      | `EMAIL_CREDIT_STATUT` (EN_COURS)  | ❌  | ❌   | ✅     |
| Crédit approuvé                     | `EMAIL_CREDIT_STATUT` (APPROUVEE) | ❌  | ❌   | ✅     |
| Crédit rejeté                       | `EMAIL_CREDIT_STATUT` (REJETEE)   | ❌  | ❌   | ✅     |
| Rappel échéance                     | `EMAIL_CREDIT_RAPPEL_ECHEANCE`    | ✅  | ❌   | ✅     |
| Retard paiement                     | `EMAIL_CREDIT_RETARD`             | ✅  | ❌   | ✅     |
| Connexion suspecte                  | `EMAIL_CONNEXION_SUSPECTE`        | ✅  | ❌   | ✅     |
| Changement MDP                      | `EMAIL_PASSWORD_CHANGED`          | ❌  | ❌   | ❌     |
| Compte bloqué                       | `EMAIL_COMPTE_BLOQUE`             | ✅  | ❌   | ✅     |
| Alerte carte                        | `EMAIL_CARTE_ALERTE`              | ✅  | ❌   | ✅     |

---

## 6. Plan d'implémentation

### Phase 1 : Infrastructure (Semaine 1)

- [ ] Configurer MailHog dans `docker-compose.yml`
- [ ] Ajouter les propriétés MailHog dans `application-dev.properties`
- [ ] Créer un `EmailTemplateEngine` pour gérer les templates
- [ ] Refactorer `EmailService` pour utiliser le moteur de templates
- [ ] Ajouter les templates dans `src/main/resources/templates/email/`

### Phase 2 : Emails critiques (Semaine 2)

- [ ] `EMAIL_WELCOME` - Bienvenue
- [ ] `EMAIL_VIREMENT_EMIS` - Confirmation virement émis
- [ ] `EMAIL_VIREMENT_RECU` - Alerte virement reçu
- [ ] `EMAIL_CONNEXION_SUSPECTE` - Alerte sécurité

### Phase 3 : Emails transactionnels (Semaine 3)

- [ ] `EMAIL_DEPOT_CONFIRME` - Confirmation dépôt
- [ ] `EMAIL_RETRAIT_CONFIRME` - Confirmation retrait
- [ ] `EMAIL_PASSWORD_CHANGED` - Confirmation changement MDP

### Phase 4 : Emails crédit (Semaine 4)

- [ ] `EMAIL_CREDIT_STATUT` - Tous les statuts de crédit
- [ ] `EMAIL_CREDIT_RAPPEL_ECHEANCE` - Rappel échéance
- [ ] `EMAIL_CREDIT_RETARD` - Alerte retard

### Phase 5 : Emails avancés (Semaine 5+)

- [ ] `EMAIL_RELEVE_COMPTE` - Relevé périodique (avec PDF)
- [ ] `EMAIL_COMPTE_BLOQUE` - Notification blocage
- [ ] `EMAIL_CARTE_ALERTE` - Alertes carte

---

## 7. Architecture proposée

```
src/main/resources/
└── templates/
    └── email/
        ├── fragments/
        │   ├── header.html      <!-- En-tête commun -->
        │   ├── footer.html      <!-- Pied de page commun -->
        │   └── button.html      <!-- Bouton CTA réutilisable -->
        ├── welcome.html
        ├── reset-password.html
        ├── otp-2fa.html
        ├── virement-emis.html
        ├── virement-recu.html
        ├── depot-confirme.html
        ├── retrait-confirme.html
        ├── credit-statut.html
        ├── credit-rappel.html
        ├── credit-retard.html
        ├── connexion-suspecte.html
        ├── password-changed.html
        ├── releve-compte.html
        ├── compte-bloque.html
        └── carte-alerte.html
```

### Classe Java proposée

```java
@Service
public class EmailTemplateEngine {
    
    private final TemplateEngine templateEngine;
    
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process("email/" + templateName, context);
    }
}
```

---

## 8. Checklist de validation

Pour chaque email implémenté :

- [ ] Template HTML créé dans `templates/email/`
- [ ] Template texte créé (fallback)
- [ ] Variables documentées
- [ ] Test avec MailHog
- [ ] Responsive (mobile-friendly)
- [ ] Accessible (alt text, semantic HTML)
- [ ] Lien de désabonnement (si applicable)
- [ ] Prévisualisation dans les clients de messagerie (Gmail, Outlook, Apple Mail)
