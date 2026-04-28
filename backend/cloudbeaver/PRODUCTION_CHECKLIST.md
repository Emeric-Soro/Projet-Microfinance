# Checklist Production (Sans Secrets)

Cette checklist sert de validation avant mise en production de l'environnement Oracle XE + CloudBeaver.

## 1) Secrets et configuration

- [ ] Aucun secret en clair dans le repo (`.env`, `.md`, scripts, logs)
- [ ] Fichier `.env` local non versionne
- [ ] Secrets injectes via un gestionnaire de secrets (Vault, Docker secrets, CI/CD secrets)
- [ ] Rotation des mots de passe effectuee avant go-live
- [ ] Variables sensibles presentes avec valeurs non faibles :
  - `ORACLE_PASSWORD`
  - `DB_USERNAME`
  - `DB_PASSWORD`
  - `CLOUDBEAVER_ADMIN_NAME`
  - `CLOUDBEAVER_ADMIN_PASSWORD`
  - `JWT_SECRET_KEY`

## 2) Durcissement CloudBeaver

- [ ] Compte admin par defaut remplace par un compte nominatif
- [ ] Mot de passe admin fort et unique
- [ ] Acces CloudBeaver restreint (VPN, IP allowlist, reverse proxy)
- [ ] HTTPS actif (TLS termine au proxy ou en frontal)
- [ ] Option de stockage global des credentials desactivee pour les users standards
- [ ] Connexions inutiles supprimees
- [ ] Logs CloudBeaver centralises et conserves

## 3) Durcissement Oracle

- [ ] Compte applicatif a privileges minimaux (least privilege)
- [ ] Comptes systeme non utilises desactives/verrouilles si applicable
- [ ] Sauvegarde Oracle testee (restore valide)
- [ ] Politique de retention des backups definie
- [ ] Supervision de l'espace disque et tablespaces activee

## 4) Reseau et exposition

- [ ] Ports non exposes publiquement sans controle
- [ ] Pare-feu/NSG: acces limite aux hotes autorises
- [ ] Segmentation reseau appliquee (DB non accessible depuis Internet)
- [ ] Reverse proxy configure avec timeout, rate limiting, headers de securite

## 5) Images et dependances

- [ ] Tags d'images Docker pinnes (pas de `latest`)
- [ ] Scan de vulnerabilites des images effectue
- [ ] Correctifs critiques appliques avant release
- [ ] Procedure de mise a jour documentee

## 6) Observabilite et exploitation

- [ ] Healthchecks en place (conteneurs et endpoints)
- [ ] Alertes configurees (service down, erreurs auth, espace disque)
- [ ] Dashboards minimaux disponibles (disponibilite, erreurs, latence)
- [ ] Journalisation horodatee en UTC

## 7) Continuite et reprise

- [ ] Runbook incident documente (auth KO, DB KO, rollback)
- [ ] Test de reprise apres incident (redemarrage complet) valide
- [ ] Procedure de rollback version Docker testee

## 8) Validation finale avant go-live

- [ ] `docker compose config` valide sans erreurs
- [ ] Demarrage propre: `docker compose up -d`
- [ ] Etat OK: `docker compose ps`
- [ ] Test DB applicatif valide (requete simple)
- [ ] Test connexion CloudBeaver valide avec compte non admin de test
- [ ] Aucun secret affiche dans les logs de demarrage

## 9) Commandes utiles (sans divulgation de secrets)

```powershell
docker compose config
docker compose up -d
docker compose ps
docker logs --tail 200 core_banking_cloudbeaver
docker logs --tail 200 core_banking_oracle
```

## 10) Gabarit .env.production (exemple sans valeurs)

```dotenv
ORACLE_PASSWORD=<to-be-provided-by-secret-store>
DB_URL=jdbc:oracle:thin:@<oracle-host>:1521/XEPDB1
DB_USERNAME=<app-username>
DB_PASSWORD=<to-be-provided-by-secret-store>

CLOUDBEAVER_PORT=8978
CLOUDBEAVER_IMAGE_TAG=26.0.2
CLOUDBEAVER_SERVER_NAME=<display-name>
CLOUDBEAVER_SERVER_URL=https://<fqdn-cloudbeaver>
CLOUDBEAVER_ADMIN_NAME=<admin-username>
CLOUDBEAVER_ADMIN_PASSWORD=<to-be-provided-by-secret-store>

JWT_SECRET_KEY=<to-be-provided-by-secret-store>
JWT_EXPIRATION_MS=3600000
APP_CORS_ALLOWED_ORIGINS=https://<frontend-fqdn>
```

---

Decision go-live:

- [ ] GO
- [ ] NO-GO

Valide par: ____________________  
Date (UTC): ____________________
