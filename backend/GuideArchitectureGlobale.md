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

---

## 💼 NIVEAU 4 : Les DTOs & Mappers (Les Valises de Transport)
**Rôle :** Sécuriser et filtrer les données envoyées ou reçues de l'extérieur. On n'envoie **jamais** une Entité brute au Frontend (pour cacher les mots de passe, les données techniques d'audit, etc.).
**Dossier :** `src/main/java/com/.../dto/` et `.../mapper/`

* **Règles de l'équipe :**
    * Les DTOs (Data Transfer Objects) peuvent être de simples `Record` (nouveauté Java) ou des `Class` classiques (via Lombok).
    * Les noms doivent être explicites : `ClientResponseDTO` (ce qu'on envoie) ou `VirementRequestDTO` (ce qu'on reçoit).
    * Les Mappers servent à convertir : `Entité ➔ DTO` ou `DTO ➔ Entité`. (Nous utiliserons *MapStruct* ou des méthodes manuelles).

---

## 🚪 NIVEAU 5 : Les Controllers (L'API REST)
**Rôle :** Être la "porte d'entrée" du serveur. Recevoir les requêtes HTTP (depuis le Frontend PHP/HTML ou une application mobile) et renvoyer des réponses HTTP (du JSON).
**Dossier :** `src/main/java/com/.../controller/`

* **Règles de l'équipe :**
    * Ce sont des `Class` annotées avec `@RestController` et `@RequestMapping`.
    * **Interdiction stricte :** Un Controller ne doit contenir **AUCUNE** règle métier (pas de `if(solde > montant)` ici). Il se contente de prendre la requête, de l'envoyer au Niveau 3 (Service), puis de renvoyer le résultat.
    * Utilisation correcte des verbes HTTP : `@GetMapping` (Lire), `@PostMapping` (Créer), `@PutMapping` (Mettre à jour), `@DeleteMapping` (Désactiver).
    * *Exemple : `TransactionController.java`*

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