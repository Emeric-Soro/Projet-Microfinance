# 🏛️ Guide d'Architecture Globale : Les 5 Niveaux (Couches)

## 🎯 Philosophie du Projet (Core Banking)
Notre application Spring Boot respecte une architecture dite **"N-Tiers"** (en couches).
La règle absolue est la **séparation des responsabilités** : chaque niveau a un rôle unique et ne doit jamais faire le travail d'un autre niveau.

Le flux de la donnée va toujours dans ce sens (de l'extérieur vers la base de données) :
`[Client / Frontend]` ➔ **1. Controller** ➔ **2. DTO** ➔ **3. Service** ➔ **4. Repository** ➔ **5. Entity** ➔ `[Base de Données]`

---

## 🧱 NIVEAU 1 : Les Entités (Entities)
**Rôle :** Représenter la structure exacte de notre base de données (Les tables et les relations).
**Dossier :** `src/main/java/com/.../entity/`

* **Règles de l'équipe :**
    * Ce sont des `Class` annotées avec `@Entity`.
    * Chaque entité doit étendre `BaseAuditEntity` pour garantir la traçabilité BCEAO (`createdAt`, `updatedAt`).
    * Interdiction absolue d'y mettre des calculs complexes ou des règles métiers. Ce ne sont que des "conteneurs" de données.
    * *Exemple : `Compte.java`, `Transaction.java`*

---

## 🗄️ NIVEAU 2 : Les Repositories (Accès aux Données)
**Rôle :** Discuter avec la base de données (H2 ou Oracle). Exécuter les requêtes SQL (SELECT, INSERT, UPDATE).
**Dossier :** `src/main/java/com/.../repository/`

* **Règles de l'équipe :**
    * Ce sont des `Interface` annotées avec `@Repository`.
    * Elles héritent obligatoirement de `JpaRepository<Entite, TypeID>`.
    * **Pagination obligatoire** (`Pageable` et `Page<T>`) pour toutes les listes volumineuses afin d'éviter de saturer la RAM du serveur.
    * Tout résultat unique (recherche par numéro de compte, email) doit retourner un `Optional<T>` pour éviter les erreurs `NullPointerException`.
    * *Exemple : `CompteRepository.java`*

---

## 🧠 NIVEAU 3 : Les Services (Le Cerveau Métier)
**Rôle :** Contenir 100% de la logique bancaire (calcul des agios, vérification des soldes avant un retrait, validation des virements).
**Dossier :** `src/main/java/com/.../service/`

* **Règles de l'équipe :**
    * Ce sont des `Class` annotées avec `@Service`.
    * C'est le **seul endroit** où l'on a le droit d'utiliser l'annotation `@Transactional` (qui annule une opération si une erreur survient au milieu d'un virement).
    * Un Service ne doit **jamais** manipuler des requêtes HTTP ou des JSON. Il ne parle qu'en Java pur.
    * *Exemple : `TransactionService.java`*

# 🧠 Spécifications de la Couche Service (Règles Métiers)

## 🎯 Philosophie de la Couche Service
La couche **Service** est le cerveau de la banque. C'est ici, et **uniquement ici**, que résident les règles de gestion (calculs, vérifications, interdictions).
* **Règle absolue :** Un Service ne doit jamais faire confiance au Frontend. Il doit toujours revérifier les données (ex: vérifier que le solde est suffisant avant un retrait, même si le bouton de retrait était grisé sur le site web).
* **Transactionnel :** Les méthodes qui modifient plusieurs tables en même temps doivent obligatoirement porter l'annotation `@Transactional`.


### 👤 MODULE 1 : CLIENT (Gestion des identités et accès)

#### `ClientService`
**Rôle :** Gérer le cycle de vie physique et légal du client (KYC - Know Your Customer).
* **Méthodes clés :**
  * `creerClient(...)` : Vérifie que l'email et le téléphone ne sont pas déjà pris, attribue le statut "NOUVEAU" par défaut, et sauvegarde.
  * `modifierStatutClient(Long idClient, String nouveauStatut)` : Permet de bloquer ou d'activer un client (ex: suite à une fraude).
  * `obtenirDetailsClient(Long idClient)` : Renvoie les informations complètes du client.

#### `UtilisateurService`
**Rôle :** Gérer les accès numériques (E-banking, Panel Admin) et la sécurité.
* **Méthodes clés :**
  * `creerCompteWeb(Long idClient, String motDePasse)` : Hache le mot de passe (BCrypt) et crée l'accès Web/Mobile pour un client existant.
  * `authentifier(String login, String motDePasseBrut)` : Vérifie les identifiants pour la connexion (Spring Security).
  * `assignerRole(Long idUser, String codeRole)` : Donne des droits (ex: passer un utilisateur en "GUICHETIER").

  
### 🏦 MODULE 2 : COMPTE (Core Banking)

#### `CompteService`
**Rôle :** Gérer l'ouverture, la fermeture et la consultation des comptes bancaires.
* **Méthodes clés :**
  * `ouvrirCompte(Long idClient, String codeTypeCompte)` : Génère un numéro de compte (RIB) unique aléatoire, met le solde à 0.00, et le relie au client.
  * `consulterSolde(String numCompte)` : Renvoie le solde actuel (très utilisé par l'application mobile).
  * `changerDecouvertAutorise(String numCompte, BigDecimal nouveauPlafond)` : Modifie la limite de découvert d'un client.
  * `cloturerCompte(String numCompte)` : Vérifie que le solde est exactement à 0 avant de changer le statut du compte en "FERME".

#### `CarteVisaService`
**Rôle :** Gérer le cycle de vie des moyens de paiement physiques.
* **Méthodes clés :**
  * `commanderCarte(String numCompte)` : Génère un numéro de carte à 16 chiffres, une date d'expiration (J+3 ans), et un CVV haché.
  * `faireOpposition(String numeroCarte)` : Désactive instantanément une carte en cas de perte/vol.


### 💸 MODULE 3 : OPERATION (Le cœur transactionnel)

#### `TransactionService`
**Rôle :** Le moteur financier. Gère les flux d'argent stricts de la BCEAO.
* **Méthodes clés (Toutes doivent être `@Transactional`) :**
  * `faireDepot(String numCompte, BigDecimal montant)` : Vérifie que le montant est > 0. Ajoute le montant au solde du compte. Crée 1 `Transaction` et 1 `LigneEcriture` (CREDIT).
  * `faireRetrait(String numCompte, BigDecimal montant)` : Vérifie que `(Solde + DecouvertAutorise) >= montant`. Soustrait le solde. Crée 1 `Transaction` et 1 `LigneEcriture` (DEBIT).
  * `faireVirement(String compteSource, String compteDest, BigDecimal montant)` :
    1. Vérifie les fonds du compte source.
    2. Débite le compte source (DEBIT).
    3. Crédite le compte destinataire (CREDIT).
    4. Crée la `Transaction` globale.
  * `historiqueOperations(String numCompte, Pageable pageable)` : Renvoie le relevé de compte paginé.

  
### 📊 MODULE 4 : TARIFICATION (Moteur de facturation)

#### `AgioService`
**Rôle :** Calculer et prélever les frais bancaires (souvent appelé par des tâches automatiques "Batch").
* **Méthodes clés :**
  * `calculerFraisTenueCompteMensuel()` : Tâche qui tourne le 1er du mois, identifie tous les comptes actifs, et génère un "Agio" en attente de prélèvement.
  * `calculerPenaliteDecouvert(String numCompte)` : Calcule les intérêts si le compte est resté en négatif.
  * `executerPrelevementsEnAttente()` : Prend tous les agios non prélevés, force un retrait sur les comptes correspondants via le `TransactionService`, et marque l'agio comme "Prélevé".


### 📱 MODULE 5 : COMMUNICATION (Alertes et E-Banking)

#### `NotificationService`
**Rôle :** Informer le client en temps réel des actions sur son compte.
* **Méthodes clés :**
  * `envoyerAlerteVirement(String numCompte, BigDecimal montant)` : Génère le texte "Vous avez reçu un virement de X FCFA" et l'enregistre en base.
  * `envoyerAlerteConnexionSuspecte(Long idClient)` : Sécurité E-banking.
  * *Note : Dans une vraie banque, ce service appellerait une API externe (comme Twilio ou Orange SMS) pour envoyer le vrai SMS sur le téléphone du client.*
---

## 💼 NIVEAU 4 : Les DTOs & Mappers (Les Valises de Transport)
**Rôle :** Sécuriser et filtrer les données envoyées ou reçues de l'extérieur. On n'envoie **jamais** une Entité brute au Frontend (pour cacher les mots de passe, les données techniques d'audit, etc.).
**Dossier :** `src/main/java/com/.../dto/` et `.../mapper/`

* **Règles de l'équipe :**
    * Les DTOs (Data Transfer Objects) peuvent être de simples `Record` (nouveauté Java) ou des `Class` classiques (via Lombok).
    * Les noms doivent être explicites : `ClientResponseDTO` (ce qu'on envoie) ou `VirementRequestDTO` (ce qu'on reçoit).
    * Les Mappers servent à convertir : `Entité ➔ DTO` ou `DTO ➔ Entité`. (Nous utiliserons *MapStruct* ou des méthodes manuelles).

# 🧳 Spécifications de la Couche DTO (Data Transfer Objects)

## 🎯 Philosophie de la Couche DTO
Les DTOs sont les "valises" qui voyagent entre l'extérieur (Web/Mobile) et l'intérieur (le Serveur).
* **Sécurité :** On ne renvoie jamais de mot de passe, de CVV de carte bancaire, ou d'ID technique inutile.
* **Validation :** Les `RequestDTO` (entrées) doivent avoir des annotations de validation stricte (`@NotBlank`, `@Positive`, etc.) pour bloquer les mauvaises données avant même qu'elles n'atteignent le Service.
* **Formatage :** Les `ResponseDTO` (sorties) transforment les objets complexes en chaînes de caractères simples (Ex: au lieu de renvoyer l'objet `TypeCompte` entier, on renvoie juste `String typeCompte = "COURANT"`).

---

## 👤 MODULE 1 : CLIENT

### 📥 Requêtes (Entrantes)
* **`CreationClientRequestDTO`** :
  * *Comportement :* Reçoit les données brutes tapées par le client dans le formulaire d'inscription.
  * *Champs :* `nom`, `prenom`, `dateNaissance`, `email` (validé avec `@Email`), `telephone`, `adresse`. (Note : Pas d'ID, ni de Code Client, car c'est le serveur qui les génère).
* **`CreationUtilisateurRequestDTO`** :
  * *Comportement :* Reçoit la demande de création d'accès Web.
  * *Champs :* `idClient`, `motDePasseBrut`.
* **`LoginRequestDTO`** :
  * *Comportement :* Utilisé pour la page de connexion.
  * *Champs :* `login`, `motDePasse`.

### 📤 Réponses (Sortantes)
* **`ClientResponseDTO`** :
  * *Comportement :* Renvoie le profil du client pour son tableau de bord.
  * *Champs :* `idClient`, `codeClient`, `nomComplet` (fusion de nom et prénom), `email`, `telephone`, `statut` (ex: "ACTIF").

---

## 🏦 MODULE 2 : COMPTE

### 📥 Requêtes (Entrantes)
* **`OuvertureCompteRequestDTO`** :
  * *Comportement :* Demande d'ouverture d'un nouveau compte.
  * *Champs :* `idClient`, `codeTypeCompte` (ex: "COURANT").
* **`ChangementDecouvertRequestDTO`** :
  * *Comportement :* Formulaire administrateur pour augmenter le découvert.
  * *Champs :* `numCompte`, `nouveauPlafond` (Doit être `@PositiveOrZero`).

### 📤 Réponses (Sortantes)
* **`CompteResponseDTO`** :
  * *Comportement :* Résumé du compte pour l'application mobile.
  * *Champs :* `numCompte`, `typeCompte` (String), `solde`, `devise`, `decouvertAutorise`, `statut` (String).
* **`CarteVisaResponseDTO`** :
  * *Comportement :* Informations de la carte bancaire.
  * *Champs :* `numeroCarteMasque` (ex: "4123 **** **** 7890"), `dateExpiration`, `statut` (Actif/Inactif). **ATTENTION : On ne renvoie JAMAIS le CVV dans un DTO !**

---

## 💸 MODULE 3 : OPERATION

### 📥 Requêtes (Entrantes)
* **`TransactionSimpleRequestDTO`** :
  * *Comportement :* Utilisé pour un dépôt ou un retrait au guichet.
  * *Champs :* `numCompte`, `montant` (Doit être `@Positive`), `idGuichetier`.
* **`VirementRequestDTO`** :
  * *Comportement :* Utilisé quand un client fait un transfert depuis son mobile.
  * *Champs :* `compteSource`, `compteDestination`, `montant`.

### 📤 Réponses (Sortantes)
* **`RecuTransactionResponseDTO`** :
  * *Comportement :* Le reçu généré immédiatement après une transaction (pour affichage ou PDF).
  * *Champs :* `referenceUnique`, `typeOperation` (ex: "VIREMENT"), `montant`, `frais`, `dateHeure`.
* **`LigneReleveResponseDTO`** :
  * *Comportement :* Une ligne dans le tableau de l'historique des opérations du client.
  * *Champs :* `dateOperation`, `libelle` (ex: "Retrait GAB"), `sens` ("DEBIT" ou "CREDIT"), `montant`.

---

## 📊 MODULE 4 : TARIFICATION

*(Note : Ce module est surtout utilisé par des tâches automatiques internes (Batch), il a très peu de DTOs entrants).*

### 📤 Réponses (Sortantes)
* **`AgioResponseDTO`** :
  * *Comportement :* Affichage des frais prélevés ou en attente pour un client.
  * *Champs :* `typeFrais` (String), `montant`, `dateCalcul`, `estPreleve` (Boolean).

# 🪄 Spécifications de la Couche Mapper (MapStruct)

## 🎯 Quel est le rôle d'un Mapper ?
Dans notre architecture, nous avons une séparation stricte :
* **Les Entités (`Entity`)** : Objets lourds, connectés à la base de données (contiennent des mots de passe, des historiques, etc.).
* **Les DTOs (`Data Transfer Object`)** : Objets légers et sécurisés, destinés à voyager sur Internet vers le Frontend.

**Le Mapper est le traducteur (ou le bagagiste).** Son unique rôle est de prendre les données d'une Entité et de les copier proprement dans un DTO (et inversement), sans exposer la logique métier.

---

## ⚙️ Pourquoi utiliser MapStruct ?
Traditionnellement, les développeurs écrivent la traduction à la main :
```java
// Méthode "Old School" (À ÉVITER)
ClientResponseDTO dto = new ClientResponseDTO();
dto.setIdClient(client.getIdClient());
dto.setNom(client.getNom());
// ... 20 lignes plus tard ...
return dto;
```
---

## 🚪 NIVEAU 5 : Les Controllers (L'API REST)
**Rôle :** Être la "porte d'entrée" du serveur. Recevoir les requêtes HTTP (depuis le Frontend PHP/HTML ou une application mobile) et renvoyer des réponses HTTP (du JSON).
**Dossier :** `src/main/java/com/.../controller/`

* **Règles de l'équipe :**
    * Ce sont des `Class` annotées avec `@RestController` et `@RequestMapping`.
    * **Interdiction stricte :** Un Controller ne doit contenir **AUCUNE** règle métier (pas de `if(solde > montant)` ici). Il se contente de prendre la requête, de l'envoyer au Niveau 3 (Service), puis de renvoyer le résultat.
    * Utilisation correcte des verbes HTTP : `@GetMapping` (Lire), `@PostMapping` (Créer), `@PutMapping` (Mettre à jour), `@DeleteMapping` (Désactiver).
    * *Exemple : `TransactionController.java`*

### 🛡️ Spécifications du GlobalExceptionHandler (Le Médiateur de l'API)

#### 🎯 Quel est le problème ?
Dans une application Java classique, lorsqu'un utilisateur demande quelque chose d'impossible (ex: consulter un compte qui n'existe pas), notre code déclenche une erreur, comme `EntityNotFoundException`.

Si personne n'attrape cette erreur, elle remonte jusqu'au serveur (Spring Boot). Par défaut, Spring Boot panique et renvoie au Frontend une énorme "Stack Trace" (des centaines de lignes de code Java incompréhensibles) avec un code HTTP `500 Internal Server Error`.
* **Problème 1 (Sécurité) :** On expose la structure interne de notre code aux hackers.
* **Problème 2 (Frontend) :** Les développeurs Web/Mobile ne peuvent pas lire ce message pour afficher une belle popup à l'utilisateur.

#### 🦸‍♂️ La Solution : Le GlobalExceptionHandler
Le `GlobalExceptionHandler` est comme le **Directeur des Relations Publiques** de la banque.
Dès qu'une erreur explose n'importe où dans l'application, il l'intercepte *avant* qu'elle ne sorte sur Internet. Il analyse l'erreur, la calme, et la transforme en un petit message JSON standardisé, propre et sécurisé.

#### ⚙️ Comment ça fonctionne techniquement ?
Spring Boot nous offre deux "mots magiques" (Annotations) pour faire cela sans avoir à écrire des `try/catch` partout :

1. **`@RestControllerAdvice` (L'Écouteur Global) :**
   On place cette annotation sur notre classe. Elle dit à Spring : *"Surveille tous les Controllers en permanence. Si l'un d'eux crashe, envoie-moi l'erreur."*

2. **`@ExceptionHandler(NomDeLerreur.class)` (Le Traducteur) :**
   À l'intérieur de notre classe, on crée des méthodes spécifiques. Par exemple : *"Si tu attrapes une `EntityNotFoundException`, renvoie un code `404 Not Found` avec le message 'Donnée introuvable'."*

#### 📦 La Structure Standard de notre Erreur
Pour que le Frontend sache exactement à quoi s'attendre quand une erreur survient, nous n'allons pas renvoyer de simples chaînes de caractères. Nous allons créer une petite valise (un DTO) spécialement pour les erreurs : le `ErrorResponseDTO`.

Chaque fois qu'une erreur se produit, notre API renverra toujours ce format exact :
```json
{
  "timestamp": "2026-04-11T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Client introuvable: 999",
  "path": "/api/v1/clients/999"
}
```
---

## 🔄 Résumé d'une action complète (Ex: Faire un Virement)

1. Le Frontend (PHP) envoie un JSON avec les infos du virement ➔ **Niveau 5 (Controller)** le réceptionne.
2. Le Controller convertit le JSON en **Niveau 4 (DTO)** et l'envoie au Service.
3. Le **Niveau 3 (Service)** reçoit le DTO, vérifie les règles métiers (le solde est-il suffisant ?), et prépare la transaction.
4. Le Service appelle le **Niveau 2 (Repository)** pour sauvegarder la transaction.
5. Le Repository traduit l'objet en **Niveau 1 (Entity)** et génère le SQL pour la base de données.

## 🛡️ Les 4 Piliers Transverses (Standard Entreprise)

Pour qu'un Core Banking soit certifié pour la production, le flux des 5 couches ne suffit pas. L'équipe doit intégrer ces 4 piliers globaux :

### 1. La Validation des Données (Jakarta Validation)
**Rôle :** Bloquer les requêtes malveillantes ou erronées avant même qu'elles n'atteignent le Service.
* **Où :** Dans les DTOs et Controllers.
* **Règles :** Utiliser les annotations `@NotBlank`, `@Min`, `@Size`, `@Email` sur les attributs des DTOs.
* **Exemple :** Un `VirementRequestDTO` doit avoir `@Min(value = 1, message = "Le montant doit être positif")` sur son attribut montant.

### 2. La Gestion Globale des Erreurs (Global Exception Handler)
**Rôle :** Ne jamais renvoyer une trace d'erreur Java (stacktrace) illisible au Frontend. Le Frontend (PHP/Mobile) doit toujours recevoir un JSON propre, même quand le serveur plante.
* **Où :** Dans un package `exception`.
* **Règles :** Créer une classe annotée avec `@ControllerAdvice`. Cette classe intercepte toutes les exceptions (`RuntimeException`, `EntityNotFoundException`) et les transforme en réponses HTTP propres (404 Not Found, 400 Bad Request) avec un message compréhensible pour l'utilisateur.

### 3. La Sécurité & Authentification (Spring Security + JWT)
**Rôle :** S'assurer que le client X ne peut pas voir le solde du client Y.
* **Où :** Dans un package `security`.
* **Règles :** * Aucun Controller ne doit être accessible publiquement (sauf la page de Login).
    * L'application utilisera des tokens **JWT (JSON Web Token)**. À chaque requête, le Frontend devra envoyer ce token dans le header (En-tête `Authorization: Bearer <token>`).
    * Utilisation stricte des rôles via `@PreAuthorize("hasRole('GUICHETIER')")` au-dessus des méthodes sensibles.

### 4. La Documentation Automatique (Swagger / OpenAPI)
**Rôle :** Créer un contrat clair entre ton équipe Backend (Java) et l'équipe Frontend (PHP).
* **Où :** Package `config`.
* **Règles :** Intégrer la dépendance `springdoc-openapi`. Cela génère automatiquement une page web (généralement sur `http://localhost:8080/swagger-ui.html`) où tes collègues Frontend pourront voir la liste de toutes tes API, tester les virements et voir la forme des JSON attendus, sans même avoir besoin de lire ton code Java.