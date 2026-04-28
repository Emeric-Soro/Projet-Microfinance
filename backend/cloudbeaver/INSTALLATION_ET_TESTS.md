# CloudBeaver + Oracle XE : Initialisation, Identifiants, Tests

Ce document couvre tout le flux de A a Z pour l'environnement local, avec les identifiants utilises dans ce projet.

## 1) Prerequis

- Docker Desktop demarre
- Ports libres :
  - `1521` (Oracle XE)
  - `8978` (CloudBeaver)
- Dossier de travail : `C:\wamp64\www\Projet-Microfinance\backend`

## 2) Identifiants et variables importantes

Source : [backend/.env](C:/wamp64/www/Projet-Microfinance/backend/.env)

- Oracle SYS (admin Oracle) :
  - utilisateur : `sys` (interne Oracle)
  - mot de passe : `oracle_admin_pass` (variable `ORACLE_PASSWORD`)
- Oracle application (Core Banking) :
  - utilisateur : `core_banking_user` (variable `DB_USERNAME`)
  - mot de passe : `core_banking_pass` (variable `DB_PASSWORD`)
  - URL JDBC locale : `jdbc:oracle:thin:@127.0.0.1:1521/XEPDB1`
- CloudBeaver (admin UI) :
  - utilisateur : `cbadmin` (variable `CLOUDBEAVER_ADMIN_NAME`)
  - mot de passe : `cbadmin_local_2026` (variable `CLOUDBEAVER_ADMIN_PASSWORD`)
  - URL UI : `http://localhost:8978`

## 3) Architecture de demarrage

Fichiers utilises :

- Compose : [backend/docker-compose.yml](C:/wamp64/www/Projet-Microfinance/backend/docker-compose.yml)
- Datasource initiale : [backend/cloudbeaver/initial-data-sources.conf](C:/wamp64/www/Projet-Microfinance/backend/cloudbeaver/initial-data-sources.conf)
- Bootstrap CloudBeaver : [backend/cloudbeaver/bootstrap.sh](C:/wamp64/www/Projet-Microfinance/backend/cloudbeaver/bootstrap.sh)
- Datasource globale versionnee : [backend/cloudbeaver/global-configuration/.dbeaver/data-sources.json](C:/wamp64/www/Projet-Microfinance/backend/cloudbeaver/global-configuration/.dbeaver/data-sources.json)

Le bootstrap fait 2 choses a chaque redemarrage CloudBeaver :

1. Reinjecte la datasource canonique
2. Nettoie les artefacts de credentials/runtime qui peuvent provoquer des ORA-01017 persistants

## 4) Initialisation complete

Depuis `C:\wamp64\www\Projet-Microfinance\backend` :

```powershell
docker compose up -d
docker compose ps
```

Resultat attendu :

- `core_banking_oracle` en statut `Up`
- `core_banking_cloudbeaver` en statut `Up`

Verification HTTP CloudBeaver :

```powershell
curl.exe -I http://localhost:8978
```

Attendu : `HTTP/1.1 200 OK`

## 5) Premiere connexion CloudBeaver

1. Ouvrir `http://localhost:8978`
2. Se connecter en admin CloudBeaver :
   - user : `cbadmin`
   - password : `cbadmin_local_2026`
3. Ouvrir la connexion `Core Banking Oracle`
4. Saisir :
   - user Oracle : `core_banking_user`
   - password Oracle : `core_banking_pass`
5. Cocher uniquement l'option session (ne pas activer le partage global des credentials)

## 6) Test fonctionnel Oracle

Test SQL minimal dans CloudBeaver :

```sql
select 1 from dual;
```

Attendu : une ligne avec la valeur `1`.

Test equivalent en ligne de commande (dans le conteneur Oracle) :

```powershell
docker exec core_banking_oracle bash -lc "echo 'select 1 from dual;' | sqlplus -L core_banking_user/core_banking_pass@localhost:1521/XEPDB1"
```

## 7) Commandes utiles d'exploitation

Redemarrer seulement CloudBeaver :

```powershell
docker compose restart cloudbeaver
```

Voir les logs CloudBeaver :

```powershell
docker logs --tail 100 core_banking_cloudbeaver
```

Arreter tout :

```powershell
docker compose down
```

Arreter tout et supprimer les volumes (reset complet Oracle + CloudBeaver) :

```powershell
docker compose down -v
```

## 8) Depannage rapide ORA-01017

Si ORA-01017 reapparait :

1. Verifier que tu entres bien `core_banking_user / core_banking_pass`
2. Ne pas activer le stockage global des credentials dans CloudBeaver
3. Redemarrer CloudBeaver :
   - `docker compose restart cloudbeaver`
4. Si besoin, reset complet :
   - `docker compose down -v`
   - `docker compose up -d`

## 9) Notes securite (local dev)

- Les secrets dans ce fichier et dans `.env` sont pour l'environnement local de dev.
- Ne pas reutiliser ces mots de passe en recette/production.
