# 📐 Spécifications UI/UX — Backoffice Microfinance

> **Document de référence front-end**
> Ce document décrit l'ensemble des écrans du backoffice, leurs composants, comportements et règles UX.
> Il couvre les 6 sprints de la roadmap.

---

## 🎨 Système de Design Global

### Palette de couleurs

#### Couleurs de base du projet
| Token | Valeur | Nom | Usage |
|---|---|---|---|
| `--color-primary` | `#084355` | Bleu nuit profond | Sidebar, header, boutons principaux, titres |
| `--color-secondary` | `#8cd5fa` | Bleu ciel clair | Accents, liens actifs, bordures focus, icônes |
| `--color-accent` | `#fab482` | Pêche dorée | CTA secondaires, badges, highlights, hover |

#### Couleurs dérivées (générées depuis les bases)
| Token | Valeur | Usage |
|---|---|---|
| `--color-primary-dark` | `#052d3a` | Header topbar, sidebar collapsed |
| `--color-primary-light` | `#0d6680` | Hover sur éléments primaires |
| `--color-secondary-light` | `#c8eafd` | Fonds de badges info, KYC en cours |
| `--color-secondary-dark` | `#4ab0e8` | Hover sur éléments secondaires |
| `--color-accent-light` | `#fdd4b0` | Fonds de badges warning, en attente |
| `--color-accent-dark` | `#f08d52` | Hover sur boutons accent |

#### Couleurs sémantiques
| Token | Valeur | Usage |
|---|---|---|
| `--color-danger` | `#D93025` | Erreurs, blocage, rejet, opposition carte |
| `--color-success` | `#2E7D32` | Validation, succès, statut ACTIF |
| `--color-warning` | `#E65100` | Avertissements, découvert, retard |
| `--color-info` | `#8cd5fa` | Informations, KYC en cours (= secondary) |

#### Couleurs neutres
| Token | Valeur | Usage |
|---|---|---|
| `--color-bg` | `#F0F6FA` | Fond général (teinte bleutée douce) |
| `--color-surface` | `#FFFFFF` | Cards, modals, panneaux |
| `--color-border` | `#C9DFE8` | Séparateurs, bordures de champs |
| `--color-text-primary` | `#084355` | Textes principaux (= primary) |
| `--color-text-secondary` | `#4A7A8A` | Labels, sous-titres, placeholders |
| `--color-text-muted` | `#8AABB5` | Textes tertiaires, hints |

### Typographie
- **Police principale** : `Inter` (Google Fonts)
- **Titres de page** : `Inter 600`, 22px
- **Sous-titres de section** : `Inter 500`, 16px
- **Labels de champs** : `Inter 500`, 13px
- **Valeurs / corps** : `Inter 400`, 14px
- **Badges / tags** : `Inter 600`, 11px, UPPERCASE

### Composants réutilisables
- **Badge statut** : pill arrondie colorée selon statut (ACTIF, BLOQUÉ, EN ATTENTE, etc.)
- **Table paginée** : en-têtes fixes, alternance de lignes, tri par colonne, pagination numérotée
- **Card métriques** : fond blanc, icône, valeur grande, label, variation en %
- **Breadcrumb** : chemin de navigation au-dessus de chaque page
- **Toast notification** : slide-in depuis le coin supérieur droit (succès, erreur, info)
- **Modal de confirmation** : overlay avec titre, message, boutons Annuler / Confirmer
- **Skeleton loader** : animation de chargement pour les tables et cards

### Layout général
```
┌──────────────────────────────────────────────────────────┐
│  [Logo]  TOPBAR : Nom agence | Caisse | User | Déco      │
├─────────────┬────────────────────────────────────────────┤
│             │  Breadcrumb                                │
│  SIDEBAR    │  ─────────────────────────────────────     │
│  (nav)      │  CONTENU PRINCIPAL                         │
│             │                                            │
│             │                                            │
└─────────────┴────────────────────────────────────────────┘
```

### Sidebar navigation
- Largeur : 240px (collapsible à 64px sur mobile)
- Groupes de navigation correspondant aux sprints :
  - 👥 Clients (KYC, liste, détail)
  - 🏦 Comptes & Cartes
  - 💰 Caisse & Opérations
  - 📋 Crédits
  - ⚙️ Paramétrage
  - 📊 Pilotage & Audit
- Indicateur actif : bordure gauche colorée + fond légèrement coloré
- Badges de comptage sur les items avec des éléments en attente (ex: KYC à traiter)

---

## 🔐 SPRINT 1 — Sécurité & Cycle Client

---

### S1-01 · Page de Connexion

**URL** : `/login`
**Rôles** : Tous (non authentifiés)

#### Layout
Page centrée, fond dégradé (`--color-primary-dark` → `--color-primary`), logo en haut, card blanche au centre (max-width 420px, border-radius 16px, shadow).

#### Éléments de la page
| Élément | Détails |
|---|---|
| Logo | Centré en haut de la card, hauteur 60px |
| Titre | "Connexion Backoffice" — Inter 600, 20px |
| Sous-titre | "Accès réservé au personnel autorisé" — texte gris |
| Champ Login | Label "Identifiant", input text, icône personne à gauche |
| Champ Mot de passe | Label "Mot de passe", input password, icône œil pour afficher/masquer |
| Bouton Se connecter | Largeur 100%, `--color-primary`, spin loader pendant l'appel API |
| Lien "Mot de passe oublié" | Texte sous le bouton, couleur accent |

#### Comportements UX
- **Validation en temps réel** : outline rouge + message d'erreur si champs vides à la soumission
- **Erreur d'identifiants** : toast rouge "Identifiants incorrects" ou message inline sous le formulaire
- **Redirection** : après succès → page OTP (S1-02) ou dashboard si OTP non activé

---

### S1-02 · Vérification OTP

**URL** : `/login/otp`
**Rôles** : Tous (flux post-login)

#### Layout
Même fond dégradé que le login. Card blanche, centré.

#### Éléments de la page
| Élément | Détails |
|---|---|
| Icône de sécurité | Bouclier ou téléphone — illustratif |
| Titre | "Vérification en deux étapes" |
| Sous-titre | "Un code à 6 chiffres a été envoyé à votre numéro ···456" |
| Champs OTP | 6 inputs numériques individuels (1 chiffre chacun), focus auto-avancé |
| Compteur de validité | "Code valide encore 4:32" — décompte en temps réel |
| Bouton Valider | Activé seulement quand les 6 chiffres sont remplis |
| Lien "Renvoyer le code" | Grisé pendant le décompte, actif après expiration |

#### Comportements UX
- **Auto-focus** : le curseur passe automatiquement au champ suivant à chaque chiffre
- **Paste** : si l'utilisateur colle 6 chiffres d'un coup, ils se répartissent dans les 6 inputs
- **Expiration** : à 0:00 les champs se grisent, message "Code expiré — Renvoyez un nouveau code"
- **Erreur** : animation shake sur les inputs + message "Code incorrect"

---

### S1-03 · Création de Compte Web Client (par backoffice)

**URL** : `/clients/nouveau`
**Rôles** : Agent commercial, Superviseur

#### Layout
Page à deux colonnes sur desktop : formulaire à gauche, récapitulatif / aperçu à droite. Stepper horizontal en haut (3 étapes : Identité → Documents → Validation).

#### Étape 1 — Identité

| Champ | Type | Règles |
|---|---|---|
| Nom | Text | Obligatoire, 2–60 caractères |
| Prénom | Text | Obligatoire |
| Date de naissance | Date picker | Obligatoire, âge ≥ 18 ans |
| Sexe | Radio (Homme / Femme) | Obligatoire |
| Nationalité | Select (pays) | Obligatoire |
| Pays de résidence | Select (pays) | Obligatoire |
| Email | Email | Obligatoire, format valide, unicité vérifiée |
| Téléphone | Tel + indicatif pays | Obligatoire |
| Adresse complète | Textarea | Obligatoire |
| Profession | Text | Obligatoire |
| Employeur | Text | Optionnel |

#### Étape 2 — Documents KYC

| Champ | Type | Règles |
|---|---|---|
| Type de pièce d'identité | Select (CNI / Passeport / Titre séjour) | Obligatoire |
| Numéro de pièce | Text | Obligatoire |
| Date d'expiration pièce | Date picker | Obligatoire, date future |
| Photo d'identité | Upload (JPG/PNG, max 5MB) | Obligatoire |
| Justificatif de domicile | Upload (PDF/JPG, max 5MB) | Obligatoire |
| Justificatif de revenus | Upload (PDF/JPG, max 5MB) | Optionnel |
| Personne Politiquement Exposée (PEP) | Toggle Oui/Non | Obligatoire |

#### Étape 3 — Validation

Récapitulatif en lecture seule de toutes les informations saisies. Bouton **Créer le client** en bas.

#### Comportements UX
- **Stepper** : navigation Précédent / Suivant, validation de l'étape avant de passer à la suivante
- **Upload** : drag-and-drop + clic, aperçu miniature après upload, bouton de suppression
- **Sauvegarde automatique** : brouillon local si l'utilisateur quitte la page
- **Succès** : modal "Client créé avec succès — ID : #XXXXX" avec bouton "Voir la fiche client"

---

### S1-04 · Liste des Clients

**URL** : `/clients`
**Rôles** : Agent, Superviseur, Admin

#### Layout
Header de page avec titre + bouton "Nouveau client" à droite. Barre de recherche + filtres. Table paginée.

#### Barre de recherche & filtres
| Filtre | Type |
|---|---|
| Recherche globale | Input texte (nom, prénom, email, téléphone, numéro pièce) |
| Statut client | Select (Tous / Actif / Inactif / Bloqué / En attente KYC) |
| Statut KYC | Select (Tous / En attente / Validé / Rejeté) |
| Date de création | Range de dates (Du… Au…) |
| Agence | Select (si multi-agences) |

#### Colonnes de la table
| Colonne | Détails |
|---|---|
| # | ID client (numérique) |
| Client | Avatar initiales + Nom Prénom |
| Email | Adresse email tronquée |
| Téléphone | Numéro formaté |
| Date d'inscription | JJ/MM/AAAA |
| Statut KYC | Badge coloré (EN ATTENTE / VALIDÉ / REJETÉ) |
| Statut compte | Badge (ACTIF / INACTIF / BLOQUÉ) |
| Actions | Icônes : 👁 Voir · ✏️ Modifier · ⚙️ Changer statut |

#### Comportements UX
- **Tri** : clic sur en-tête de colonne → tri croissant/décroissant (flèche indicatrice)
- **Pagination** : 10/25/50 lignes par page, navigation numérotée
- **Export** : bouton "Exporter CSV" en haut à droite
- **Ligne cliquable** : clic sur une ligne → naviguer vers S1-05 (détail client)
- **Empty state** : illustration + "Aucun client trouvé — Modifier les filtres"

---

### S1-05 · Détail Client

**URL** : `/clients/:id`
**Rôles** : Agent, Superviseur, Admin

#### Layout
En-tête avec photo/avatar, nom, statut badge, et actions rapides. Puis onglets horizontaux.

#### En-tête (Header de fiche)
| Élément | Détails |
|---|---|
| Avatar / Photo | Cercle 80px, initiales si pas de photo |
| Nom complet | Titre H1 |
| ID client | `#XXXXX` — gris |
| Badge statut | ACTIF / INACTIF / BLOQUÉ |
| Badge KYC | EN ATTENTE / VALIDÉ / REJETÉ |
| Boutons d'action | "Modifier" · "Changer statut" · "Voir les comptes" |
| Date de création | "Client depuis le JJ/MM/AAAA" |

#### Onglets

**Onglet 1 — Informations personnelles**
- Deux colonnes : données d'identité à gauche, coordonnées à droite
- Champs affichés en mode lecture (label gris + valeur noire)

**Onglet 2 — KYC & Documents**
- Résumé KYC (statut, niveau de risque, date de soumission, validateur)
- Pièces jointes avec aperçu cliquable (thumbnail + icône download)
- Section "Historique des décisions KYC" (timeline)

**Onglet 3 — Comptes**
- Tableau des comptes liés : numéro, type, solde, statut, date ouverture
- Bouton "Ouvrir un nouveau compte"

**Onglet 4 — Crédits**
- Liste des crédits en cours et terminés : produit, montant, date déblocage, statut, capital restant

**Onglet 5 — Historique / Activité**
- Timeline des actions sur le dossier (création, modifications KYC, changements de statut)

---

### S1-06 · Saisie / Mise à Jour KYC Client

**URL** : `/clients/:id/kyc`
**Rôles** : Agent commercial, Superviseur

#### Layout
Formulaire en deux colonnes, avec le récapitulatif du client actuel dans un panneau latéral fixe.

#### Champs du formulaire
| Section | Champs |
|---|---|
| **Identité** | Type pièce, Numéro, Date expiration |
| **Documents** | Upload photo, domicile, revenus (avec aperçu des fichiers déjà soumis) |
| **Informations financières** | Profession, Employeur, revenus mensuels estimés |
| **Conformité** | Toggle PEP, Pays nationalité, Pays résidence |
| **Date de soumission** | Date picker (aujourd'hui par défaut) |
| **Commentaire interne** | Textarea optionnel |

#### Comportements UX
- **Pré-remplissage** : si un KYC existe déjà, les champs sont pré-remplis en mode édition
- **Fichiers existants** : affichage des pièces déjà uploadées avec option "Remplacer"
- **Confirmation de soumission** : modal "Soumettre le dossier KYC ?" avant envoi
- **Succès** : toast vert + retour automatique sur la fiche client

---

### S1-07 · Décision KYC

**URL** : `/clients/:id/kyc/decision`
**Rôles** : Superviseur, Compliance Officer

#### Layout
Page divisée en deux parties : à gauche le dossier KYC complet en lecture seule, à droite le panneau de décision.

#### Panneau de dossier (gauche — lecture seule)
- Toutes les informations du KYC
- Visualiseur de pièces jointes inline (PDF / image)
- Historique des soumissions précédentes

#### Panneau de décision (droite)
| Élément | Détails |
|---|---|
| Statut de décision | Boutons radio larges : ✅ VALIDER / ❌ REJETER / 🔄 DEMANDER COMPLÉMENT |
| Niveau de risque | Select : FAIBLE / MOYEN / ÉLEVÉ (affiché seulement si VALIDER) |
| Commentaire | Textarea obligatoire (min. 10 caractères) |
| Validateur | Champ auto-rempli avec le nom de l'utilisateur connecté (non modifiable) |
| Bouton Soumettre | Désactivé jusqu'à ce que tous les champs soient remplis |

#### Comportements UX
- **Rejet** : affiche un champ "Motif de rejet" obligatoire + liste des documents manquants (checkboxes)
- **Complément** : affiche un champ pour décrire ce qui est attendu
- **Confirmation** : modal récapitulatif avant soumission définitive
- **Non-réversibilité** : avertissement "Cette décision est définitive et sera tracée dans le journal d'audit"

---

### S1-08 · Changement de Statut Client

**Accès** : Modal accessible depuis la liste ou la fiche client
**Rôles** : Superviseur, Admin

#### Contenu de la modal
| Élément | Détails |
|---|---|
| Titre | "Changer le statut du client" |
| Nom du client | Affiché en sous-titre (non modifiable) |
| Statut actuel | Badge actuel du client |
| Nouveau statut | Select : ACTIF / INACTIF / BLOQUÉ |
| Motif | Textarea obligatoire |
| Boutons | Annuler / Confirmer |

#### Comportements UX
- **Alerte conditionnelle** : si passage à BLOQUÉ → message d'avertissement "Toutes les opérations du client seront suspendues"
- **Confirmation** : bouton Confirmer en rouge si passage à BLOQUÉ
- **Succès** : toast + rafraîchissement du badge statut sur la fiche

---

## 🏦 SPRINT 2 — Comptes & Cartes

---

### S2-01 · Ouverture de Compte

**URL** : `/comptes/nouveau`
**Rôles** : Agent commercial, Superviseur

#### Layout
Formulaire en une colonne, card blanche centrée (max-width 600px). En haut : sélecteur de client.

#### Champs du formulaire
| Champ | Type | Règles |
|---|---|---|
| Client | Autocomplete (recherche par nom/ID) | Obligatoire, KYC doit être VALIDÉ |
| Type de compte | Select (COURANT / ÉPARGNE / DAT) | Obligatoire |
| Produit d'épargne | Select (si type = ÉPARGNE) | Obligatoire conditionnellement |
| Dépôt initial | Montant numérique (FCFA) | Obligatoire, ≥ montant minimum du produit |
| Devise | Select (XOF par défaut) | Obligatoire |
| Agence gestionnaire | Select (agence connectée par défaut) | Obligatoire |

#### Comportements UX
- **Recherche client** : dropdown avec résultats en temps réel (nom, ID, statut KYC badge)
- **Blocage KYC** : si client sélectionné sans KYC validé → message d'erreur inline rouge "Ce client n'a pas de KYC validé"
- **Calcul dépôt minimum** : affiché dynamiquement selon le produit sélectionné
- **Numéro de compte** : généré automatiquement côté serveur, affiché dans le modal de succès
- **Succès** : modal avec le numéro de compte généré + bouton "Voir le compte"

---

### S2-02 · Consultation Solde Compte

**Accès** : Via fiche client (onglet Comptes) ou via menu Comptes
**URL** : `/comptes/:numCompte`
**Rôles** : Agent, Superviseur, Admin

#### Layout
Card d'en-tête avec info compte, puis section solde en grand, puis historique.

#### Card d'en-tête
| Élément | Détails |
|---|---|
| Numéro de compte | Format CI23CB... — monospace |
| Type de compte | Badge (COURANT / ÉPARGNE) |
| Titulaire | Lien vers la fiche client |
| Agence | Nom de l'agence gestionnaire |
| Date d'ouverture | JJ/MM/AAAA |
| Statut | Badge ACTIF / BLOQUÉ / CLÔTURÉ |

#### Section Solde
- **Solde disponible** : grande valeur centrale (36px, bold, vert si positif / rouge si négatif)
- **Découvert autorisé** : valeur en dessous, en gris
- **Solde comptable** : valeur intermédiaire
- Boutons rapides : "Déposer" · "Retirer" · "Virement"

#### Comportements UX
- **Rafraîchissement** : bouton icône ↺ pour actualiser le solde
- **Copier numéro** : icône copy à côté du numéro de compte
- **Historique** : mini-tableau des 5 dernières opérations avec lien "Voir tout"

---

### S2-03 · Blocage de Compte

**Accès** : Bouton "Bloquer" sur la fiche compte ou liste comptes
**Rôles** : Superviseur, Admin

#### Contenu de la modal
| Élément | Détails |
|---|---|
| Titre | "Bloquer le compte" avec icône 🔒 |
| Compte ciblé | Numéro + titulaire (non modifiable) |
| Motif de blocage | Select (FRAUDE SUSPECTÉE / DÉCISION JUDICIAIRE / DEMANDE CLIENT / AUTRE) |
| Commentaire | Textarea obligatoire |
| Durée | Optionnel : date de fin de blocage ou "Indéfini" |
| Bouton Confirmer | Rouge + icône verrou |

#### Comportements UX
- **Double confirmation** : "Êtes-vous sûr ? Cette action suspend toutes les opérations sur ce compte"
- **Conséquences affichées** : liste des impacts (cartes associées, virements en cours)
- **Succès** : badge compte passe immédiatement à BLOQUÉ dans l'UI

---

### S2-04 · Déblocage de Compte

**Accès** : Bouton "Débloquer" visible sur un compte BLOQUÉ
**Rôles** : Superviseur, Admin

#### Contenu de la modal
| Élément | Détails |
|---|---|
| Titre | "Débloquer le compte" avec icône 🔓 |
| Motif du blocage initial | Affiché en lecture seule |
| Motif du déblocage | Textarea obligatoire |
| Validé par | Utilisateur connecté (non modifiable) |
| Bouton Confirmer | Vert |

---

### S2-05 · Clôture de Compte

**Accès** : Bouton "Clôturer" sur la fiche compte
**Rôles** : Superviseur, Admin

#### Contenu de la modal
| Élément | Détails |
|---|---|
| Titre | "Clôturer le compte" avec icône ⚠️ |
| Avertissement | "Cette action est irréversible" — fond orange |
| Conditions de clôture | Solde = 0, pas d'encours de crédit (vérification automatique) |
| Solde résiduel | Si solde > 0 : champ "Compte de remise des fonds" |
| Motif | Select + Textarea |
| Double validation | Checkbox "Je confirme vouloir clôturer ce compte définitivement" |

#### Comportements UX
- **Pré-vérification** : si crédit actif sur ce compte → blocage avec message d'erreur explicatif
- **Virement automatique** : si solde > 0, UI guide vers sélection du compte bénéficiaire du solde résiduel

---

### S2-06 · Changement Découvert Autorisé

**Accès** : Onglet paramètres du compte
**Rôles** : Superviseur, Admin

#### Contenu de la modal
| Élément | Détails |
|---|---|
| Titre | "Modifier le découvert autorisé" |
| Découvert actuel | Valeur en lecture seule |
| Nouveau découvert | Input numérique (FCFA), min 0 |
| Motif | Textarea |
| Validité | Date de fin optionnelle |

---

### S2-07 · Commande Carte Visa

**URL** : `/comptes/:numCompte/carte/commande`
**Rôles** : Agent commercial, Superviseur

#### Formulaire
| Champ | Type | Règles |
|---|---|---|
| Compte associé | Affiché, non modifiable | — |
| Type de carte | Select (VISA CLASSIC / VISA GOLD / VISA INFINITE) | Obligatoire |
| Nom sur la carte | Text | Obligatoire, max 26 caractères |
| Adresse de livraison | Textarea | Obligatoire |
| Mode de livraison | Radio (Agence / Domicile) | Obligatoire |

#### Comportements UX
- **Aperçu carte** : représentation visuelle de la carte avec le nom saisi en temps réel
- **Délai estimé** : affiché dynamiquement selon le mode de livraison sélectionné
- **Confirmation** : modal récapitulatif avec coût de la carte et délai
- **Succès** : numéro de commande généré, affiché dans la fiche compte

---

### S2-08 · Opposition Carte Visa

**Accès** : Depuis la liste des cartes du compte
**Rôles** : Agent, Superviseur

#### Contenu de la modal
| Élément | Détails |
|---|---|
| Carte ciblée | 4 derniers chiffres + type |
| Motif | Select : PERTE / VOL / FRAUDE / DÉTÉRIORATION |
| Date et heure | Date/heure de l'incident |
| Commentaire | Textarea |
| Confirmation | Checkbox d'accord sur les conséquences |
| Urgence | Badge "Opposition immédiate — carte désactivée dès validation" |

---

## 💰 SPRINT 3 — Opérations & Caisse

---

### S3-01 · Ouverture Caisse

**URL** : `/caisse/ouverture`
**Rôles** : Caissier, Superviseur

#### Layout
Page centrée, card unique.

#### Éléments
| Élément | Détails |
|---|---|
| Statut caisse | Indicateur visuel (FERMÉE en rouge, OUVERTE en vert) |
| Solde initial | Input numérique (FCFA) — montant comptabilisé en physique |
| Date/heure | Auto-rempli avec la date du jour (non modifiable) |
| Agent | Utilisateur connecté (non modifiable) |
| Bouton Ouvrir la caisse | Vert, plein, avec icône cadenas ouvert |

#### Comportements UX
- **Caisse déjà ouverte** : affiche l'état actuel avec bouton "Consulter l'état" au lieu du formulaire
- **Montant minimum** : validation que le solde initial est ≥ 0
- **Confirmation** : modal "Confirmer l'ouverture avec un solde initial de X FCFA ?"

---

### S3-02 · État de Caisse

**URL** : `/caisse/etat`
**Rôles** : Caissier, Superviseur, Admin

#### Layout
Dashboard de caisse avec 4 KPI cards en haut, puis tableau des mouvements du jour.

#### KPI Cards
| Card | Valeur affichée |
|---|---|
| Solde d'ouverture | Montant initial du matin |
| Total entrées | Cumul des dépôts + autres crédits |
| Total sorties | Cumul des retraits + autres débits |
| Solde courant | Calculé = Ouverture + Entrées − Sorties |

#### Tableau des mouvements
Colonnes : Heure · Type (DÉPÔT/RETRAIT/VIREMENT/PAIEMENT) · Client · Compte · Montant · Agent

#### Actions
- Bouton "Fermer la caisse" en haut à droite (rouge)
- Bouton "Exporter relevé journalier" (PDF/CSV)

---

### S3-03 · Dépôt

**URL** : `/operations/depot`
**Rôles** : Caissier

#### Layout
Formulaire en card, largeur 500px, centré.

#### Champs
| Champ | Type | Règles |
|---|---|---|
| Numéro de compte | Input texte + autocomplete | Obligatoire |
| Titulaire | Affiché automatiquement après saisie du compte | Lecture seule |
| Solde actuel | Affiché automatiquement | Lecture seule |
| Montant | Input numérique | Obligatoire, > 0 |
| Libellé | Text | Optionnel |
| Pièce justificative | Upload | Optionnel |

#### Comportements UX
- **Recherche de compte** : saisie partielle → dropdown avec compte, titulaire, solde
- **Calcul en temps réel** : "Nouveau solde après dépôt : X FCFA" mis à jour à la saisie du montant
- **Alerte compte bloqué** : si le compte est BLOQUÉ → erreur immédiate, formulaire non soumettable
- **Confirmation** : modal avant envoi avec récapitulatif complet
- **Reçu** : après succès, modal avec option "Imprimer le reçu" (PDF)

---

### S3-04 · Retrait

**URL** : `/operations/retrait`
**Rôles** : Caissier

#### Layout
Identique au dépôt avec champs supplémentaires.

#### Champs supplémentaires
| Champ | Type | Règles |
|---|---|---|
| Pièce d'identité présentée | Text | Obligatoire |
| Montant | Input numérique | Obligatoire, ≤ solde disponible |

#### Comportements UX
- **Contrôle de solde** : si montant > solde disponible → message d'erreur rouge inline
- **Contrôle de caisse** : si montant > solde caisse → avertissement orange
- **Gros retraits** : si montant > seuil paramétré → validation 4-eyes automatiquement déclenchée

---

### S3-05 · Virement

**URL** : `/operations/virement`
**Rôles** : Caissier, Agent

#### Layout
Formulaire en deux colonnes : Compte source (gauche) / Compte destination (droite) avec flèche →.

#### Champs
| Champ | Type |
|---|---|
| Compte source | Autocomplete (numéro ou nom titulaire) |
| Solde disponible source | Lecture seule, affiché sous le champ |
| Compte destination | Autocomplete |
| Titulaire destination | Affiché automatiquement |
| Montant | Input numérique |
| Motif du virement | Text |
| Type de virement | Radio (INTERNE / EXTERNE) |

#### Comportements UX
- **Virement interne/externe** : si externe → champ IBAN destination + banque bénéficiaire
- **Même compte** : si source = destination → erreur "Compte source et destination identiques"
- **Animation visuelle** : flèche animée entre les deux comptes après validation du montant

---

### S3-06 · Paiement Carte

**URL** : `/operations/paiement-carte`
**Rôles** : Caissier

#### Champs
| Champ | Type |
|---|---|
| Numéro de carte | Input (4 groupes de 4 chiffres) |
| Compte débité | Affiché automatiquement |
| Montant | Input numérique |
| Référence marchand | Text |
| Description | Text |

---

### S3-07 · Validation Transaction — Approbation 4-Eyes

**URL** : `/transactions/validation`
**Rôles** : Superviseur, Admin (second validateur)

#### Layout
Liste des transactions en attente de validation. Chaque ligne est extensible.

#### Table des transactions en attente
| Colonne | Détails |
|---|---|
| # Transaction | ID unique |
| Type | Badge (DÉPÔT / RETRAIT / VIREMENT) |
| Montant | En FCFA, mis en avant |
| Compte | Numéro + titulaire |
| Initiateur | Agent qui a soumis la transaction |
| Date/Heure | Horodatage de soumission |
| Actions | Bouton ✅ Approuver · ❌ Rejeter |

#### Modal d'approbation
- Détail complet de la transaction
- Commentaire du validateur (optionnel)
- Bouton "Approuver" (vert) ou "Rejeter" (rouge)

#### Comportements UX
- **Badge de comptage** : nombre de transactions en attente visible dans la sidebar
- **Auto-rafraîchissement** : toutes les 30 secondes ou via WebSocket
- **Urgence** : transactions en attente > 1h surlignées en orange

---

### S3-08 · Validation Transaction — Rejet 4-Eyes

**Accès** : Depuis S3-07

#### Modal de rejet
| Élément | Détails |
|---|---|
| Titre | "Rejeter la transaction" |
| Motif | Select (DONNÉES INCORRECTES / MONTANT SUSPECT / DOCUMENT MANQUANT / AUTRE) |
| Commentaire | Textarea obligatoire |
| Notification | L'initiateur recevra une notification du rejet |

---

### S3-09 · Historique des Opérations d'un Compte

**URL** : `/comptes/:numCompte/historique`
**Rôles** : Agent, Superviseur, Admin

#### Layout
Filtres en haut, tableau paginé, graphique de solde en option.

#### Filtres
| Filtre | Type |
|---|---|
| Période | Range de dates |
| Type d'opération | Multiselect (DÉPÔT / RETRAIT / VIREMENT / PAIEMENT / DÉBIT AGIOS) |
| Sens | Select (TOUS / DÉBIT / CRÉDIT) |
| Montant min / max | Deux inputs numériques |

#### Colonnes de la table
| Colonne | Détails |
|---|---|
| Date/Heure | Horodatage complet |
| Libellé | Description de l'opération |
| Type | Badge coloré |
| Débit | Montant en rouge (si débit) |
| Crédit | Montant en vert (si crédit) |
| Solde après | Solde du compte après l'opération |
| Réf. transaction | Lien vers le détail |

#### Comportements UX
- **Export** : PDF (relevé de compte formaté) + CSV
- **Graphique** : toggle pour afficher l'évolution du solde en courbe
- **Recherche par référence** : input de recherche par numéro de transaction

---

## 📋 SPRINT 4 — Crédits

---

### S4-01 · Soumission Demande de Crédit

**URL** : `/credits/nouvelle-demande`
**Rôles** : Agent de crédit

#### Layout
Formulaire en stepper (4 étapes) avec barre de progression.

#### Étape 1 — Client & Produit
| Champ | Type | Règles |
|---|---|---|
| Client | Autocomplete | KYC validé obligatoire |
| Produit de crédit | Select avec détails dynamiques | Obligatoire |
| Objet du crédit | Textarea | Obligatoire, 20–500 caractères |
| Agent de crédit | Auto-rempli | Non modifiable |

#### Étape 2 — Montant & Durée
| Champ | Type | Règles |
|---|---|---|
| Montant demandé | Input numérique (FCFA) | Entre montant min et max du produit |
| Durée souhaitée | Select (en mois) | Dans les limites du produit |
| Fréquence remboursement | Radio (Mensuel / Trimestriel) | — |
| Compte de décaissement | Select (comptes actifs du client) | Obligatoire |

#### Étape 3 — Garanties & Pièces
| Champ | Type |
|---|---|
| Type de garantie | Select (SALAIRE / BIEN IMMOBILIER / CAUTION / AUTRE) |
| Description garantie | Textarea |
| Pièces jointes | Upload multiple |

#### Étape 4 — Simulation & Récapitulatif
- Tableau d'amortissement prévisionnel (non modifiable, calculé automatiquement)
- Taux d'intérêt affiché
- Montant des mensualités
- Coût total du crédit
- Bouton "Soumettre la demande"

---

### S4-02 · Liste des Demandes de Crédit en Attente

**URL** : `/credits/demandes`
**Rôles** : Agent de crédit, Comité de crédit, Admin

#### Filtres
| Filtre | Type |
|---|---|
| Statut | Select (EN ATTENTE / APPROUVÉE / REJETÉE / TOUTES) |
| Produit | Select |
| Agent | Select |
| Montant | Range |
| Date soumission | Range de dates |

#### Colonnes de la table
| Colonne | Détails |
|---|---|
| # Demande | ID |
| Client | Nom + ID |
| Produit | Code produit |
| Montant demandé | FCFA, mis en avant |
| Durée | En mois |
| Agent | Nom de l'agent |
| Date soumission | JJ/MM/AAAA |
| Statut | Badge (EN ATTENTE / APPROUVÉE / REJETÉE) |
| Actions | 👁 Voir · ✅ Décider |

---

### S4-03 · Détail Demande de Crédit

**URL** : `/credits/demandes/:idDemande`
**Rôles** : Agent, Comité de crédit

#### Layout
Page complète en deux colonnes. Gauche : informations de la demande. Droite : panneau de décision (si en attente).

#### Section gauche — Dossier
- **Bloc client** : nom, ID, statut KYC, lien vers fiche client
- **Bloc crédit** : produit, montant, durée, objet, agent instructeur
- **Bloc garanties** : type, description, documents joints (visualiseur)
- **Simulation** : tableau d'amortissement prévisionnel complet
- **Historique des décisions** : si la demande a été révisée plusieurs fois

---

### S4-04 · Décision Demande de Crédit

**Accès** : Panneau droit de S4-03 ou bouton dédié
**Rôles** : Comité de crédit, Superviseur

#### Panneau de décision
| Élément | Détails |
|---|---|
| Décision | Boutons larges : ✅ APPROUVER · ❌ REJETER · ✏️ MODIFIER |
| Montant accordé | Input (peut différer du montant demandé) |
| Durée accordée | Select (peut différer de la durée demandée) |
| Taux appliqué | Input (pré-rempli par le produit, modifiable) |
| Motif de rejet | Textarea (visible si REJETER sélectionné) |
| Commentaire comité | Textarea |

---

### S4-05 · Consultation Crédit

**URL** : `/credits/:idCredit`
**Rôles** : Agent, Superviseur, Admin

#### Layout
Card d'en-tête + onglets.

#### Card d'en-tête
| Élément | Détails |
|---|---|
| ID Crédit | Code unique |
| Client | Lien vers fiche |
| Produit | Badge produit |
| Statut | Badge (ACTIF / REMBOURSÉ / EN DÉFAUT / CLÔTURÉ) |
| Montant décaissé | Grande valeur |
| Capital restant dû | Valeur mise en avant |
| Prochaine échéance | Date + montant |

#### Onglets
- **Onglet 1 — Résumé** : métriques principales (taux, durée, mensualité, capital remboursé %)
- **Onglet 2 — Échéancier** : tableau complet des échéances (voir S4-08)
- **Onglet 3 — Remboursements** : historique des paiements effectués
- **Onglet 4 — Documents** : pièces du dossier de crédit

---

### S4-06 · Décaissement Crédit

**URL** : `/credits/:idCredit/decaissement`
**Rôles** : Caissier, Superviseur

#### Layout
Formulaire court en card.

#### Éléments
| Élément | Détails |
|---|---|
| Crédit ciblé | Infos résumées (client, montant, statut = APPROUVÉ) |
| Compte cible | Select parmi les comptes actifs du client |
| Montant à décaisser | Affiché, non modifiable |
| Date de décaissement | Aujourd'hui par défaut |
| Bouton Décaisser | Vert, avec confirmation obligatoire |

#### Comportements UX
- **Pré-condition** : bouton actif seulement si crédit statut = APPROUVÉE
- **Confirmation** : modal "Le montant de X FCFA sera crédité sur le compte Y. Confirmer ?"
- **Succès** : statut du crédit passe à ACTIF, le compte client est crédité

---

### S4-07 · Remboursement Crédit

**URL** : `/credits/:idCredit/remboursement`
**Rôles** : Caissier, Agent

#### Champs
| Champ | Type | Règles |
|---|---|---|
| Crédit | Affiché, non modifiable | — |
| Prochaine échéance | Affichée (montant attendu) | Lecture seule |
| Montant remboursé | Input numérique | Obligatoire, > 0 |
| Mode de paiement | Select (CASH / VIREMENT / PRÉLÈVEMENT) | — |
| Date | Date picker | Obligatoire |

#### Comportements UX
- **Mise en évidence de l'échéance courante** : surlignée dans le mini-tableau affiché
- **Répartition capital/intérêts** : calculée automatiquement et affichée
- **Paiement partiel** : avertissement si montant < montant échéance
- **Avance** : message informatif si montant > montant échéance

---

### S4-08 · Échéancier / Tableau d'Amortissement

**URL** : `/credits/:idCredit/echeancier`
**Rôles** : Agent, Superviseur, Admin

#### Layout
Tableau complet, exportable.

#### Colonnes
| Colonne | Détails |
|---|---|
| # | Numéro d'échéance |
| Date d'échéance | JJ/MM/AAAA |
| Capital remboursé | En FCFA |
| Intérêts | En FCFA |
| Mensualité | Total (capital + intérêts) |
| Capital restant dû | Après remboursement |
| Statut | PAYÉE / EN COURS / À VENIR / EN RETARD |

#### Comportements UX
- **Lignes colorées** : PAYÉE en vert pâle, EN RETARD en rouge pâle
- **Ligne courante** : surlignée en bleu
- **Export** : bouton "Exporter PDF" (tableau d'amortissement officiel formaté)
- **Graphique** : visualisation de la part capital vs intérêts en courbe

---

### S4-09 · Crédits par Client

**URL** : `/clients/:id/credits`
**Rôles** : Agent, Superviseur

**Description** : Liste de tous les crédits d'un client (actifs, terminés, rejetés) avec accès aux détails. Identique à S4-05 mais filtrée par client.

---

### S4-10 · Simulation Crédit

**URL** : `/credits/simulation`
**Rôles** : Agent de crédit

#### Layout
Formulaire à gauche, tableau de résultat à droite (apparaît après calcul).

#### Champs de simulation
| Champ | Type |
|---|---|
| Montant | Input numérique |
| Taux d'intérêt | Input % (ou pré-rempli si produit sélectionné) |
| Durée | Input en mois |
| Méthode | Select (DÉGRESSIF / LINÉAIRE) |
| Produit | Select optionnel (pré-remplit taux et conditions) |

#### Résultat affiché
- **Mensualité** : grande valeur mise en avant
- **Coût total du crédit** : intérêts totaux
- **Tableau d'amortissement complet** : avec toutes les colonnes
- **Graphique** : camembert ou barres capital vs intérêts

#### Comportements UX
- **Calcul en temps réel** : le tableau se met à jour à chaque modification
- **Bouton "Créer une demande"** : pré-remplit la demande avec les paramètres de la simulation

---

## ⚙️ SPRINT 5 — Paramétrage & Tarification

---

### S5-01 · Création Produit de Crédit

**URL** : `/parametrage/produits-credit/nouveau`
**Rôles** : Admin

#### Champs
| Champ | Type | Règles |
|---|---|---|
| Code produit | Text | Unique, majuscules, pas d'espaces |
| Libellé | Text | Obligatoire |
| Description | Textarea | — |
| Taux d'intérêt annuel | Input % | Obligatoire, > 0 |
| Méthode de calcul | Select (DÉGRESSIF / LINÉAIRE) | Obligatoire |
| Montant minimum | Input numérique | Obligatoire |
| Montant maximum | Input numérique | Obligatoire, > min |
| Durée minimale (mois) | Input numérique | — |
| Durée maximale (mois) | Input numérique | — |
| Fréquence | Select (MENSUEL / TRIMESTRIEL) | — |
| Actif | Toggle | — |

---

### S5-02 · Liste Produits de Crédit

**URL** : `/parametrage/produits-credit`
**Rôles** : Admin, Superviseur

#### Colonnes
Code · Libellé · Taux · Méthode · Montant min · Montant max · Statut · Actions (Modifier / Désactiver)

---

### S5-03 · Création Produit d'Épargne

**URL** : `/parametrage/produits-epargne/nouveau`
**Rôles** : Admin

#### Champs
| Champ | Type |
|---|---|
| Code produit | Text unique |
| Libellé | Text |
| Taux de rémunération | Input % |
| Dépôt minimum à l'ouverture | Input numérique |
| Solde minimum | Input numérique |
| Frais de tenue de compte | Input numérique (par mois) |
| Découvert autorisé par défaut | Input numérique |
| Actif | Toggle |

---

### S5-04 · Liste Produits d'Épargne

**URL** : `/parametrage/produits-epargne`
**Rôles** : Admin, Superviseur

Identique à S5-02 mais pour les produits d'épargne.

---

### S5-05 · Création Agence

**URL** : `/parametrage/agences/nouvelle`
**Rôles** : Admin

#### Champs
| Champ | Type |
|---|---|
| Code agence | Text unique |
| Nom de l'agence | Text |
| Ville | Text |
| Adresse complète | Textarea |
| Téléphone | Tel |
| Email | Email |
| Directeur d'agence | Select (utilisateurs) |
| Statut | Toggle Actif/Inactif |

---

### S5-06 · Modification Agence

**URL** : `/parametrage/agences/:id/modifier`

Formulaire pré-rempli, identique à S5-05.

---

### S5-07 · Détail Agence

**URL** : `/parametrage/agences/:id`

#### Sections
- Informations générales (lecture seule)
- Statistiques de l'agence : nombre de clients, comptes, crédits actifs, solde total
- Liste des agents rattachés
- Historique des modifications

---

### S5-08 · Liste Agences

**URL** : `/parametrage/agences`
**Rôles** : Admin, Superviseur

#### Colonnes
Code · Nom · Ville · Directeur · Nb clients · Nb comptes · Statut · Actions (Voir / Modifier / Désactiver)

---

### S5-09 · Désactivation Agence

**Accès** : Modal depuis la liste ou la fiche agence

#### Contenu
- Avertissement sur les impacts (comptes, clients, agents rattachés)
- Champ motif obligatoire
- Confirmation double

---

### S5-10 · Rafraîchir Cache Tarification

**Accès** : Bouton dans le menu Paramétrage
**Rôles** : Admin

#### Layout
Card simple avec description du cache, date de dernière mise à jour, bouton "Rafraîchir maintenant".

#### Comportements UX
- **Spinner** pendant le rafraîchissement
- **Succès** : toast vert "Cache tarification mis à jour — JJ/MM/AAAA HH:MM"
- **Erreur** : toast rouge avec détail de l'erreur

---

## 📊 SPRINT 6 — Agios, Pilotage & Audit

---

### S6-01 · Calcul Frais Tenue Mensuels

**URL** : `/agios/frais-tenue`
**Rôles** : Admin, Superviseur

#### Layout
Formulaire de lancement de batch + tableau des derniers calculs.

#### Formulaire de lancement
| Champ | Type |
|---|---|
| Mois de calcul | Mois/Année picker |
| Produit(s) concerné(s) | Multiselect |
| Mode | Radio (SIMULATION / RÉEL) |

#### Comportements UX
- **Mode simulation** : aperçu du montant total avant application réelle
- **Confirmation en mode RÉEL** : modal d'avertissement (action irréversible)
- **Progression** : barre de progression pendant le calcul batch
- **Résultats** : tableau des comptes débités avec montant

---

### S6-02 · Calcul Pénalité Découvert

**URL** : `/agios/penalites-decouvert`
**Rôles** : Admin, Superviseur

Identique à S6-01 mais pour les pénalités de découvert.

#### Champs supplémentaires
| Champ | Type |
|---|---|
| Taux de pénalité | Affiché (issu du paramétrage) |
| Seuil de tolérance | Nombre de jours en découvert avant pénalité |

---

### S6-03 · Prélèvement Agios en Attente

**URL** : `/agios/en-attente`
**Rôles** : Admin, Superviseur

#### Layout
Table des agios calculés mais non encore prélevés.

#### Colonnes
| Colonne | Détails |
|---|---|
| Client | Nom + ID |
| Compte | Numéro |
| Type d'agio | TENUE / DÉCOUVERT |
| Montant calculé | En FCFA |
| Période | Mois/Année |
| Statut | EN ATTENTE / PRÉLEVÉ / INSUFFISANT |
| Actions | Prélever manuellement · Annuler |

#### Actions groupées
- Sélection multiple + "Prélever la sélection"
- Filtres par type, période, statut

---

### S6-04 · KPIs / Tableau de Bord Direction

**URL** : `/pilotage/tableau-de-bord`
**Rôles** : Direction, Admin

#### Layout
Dashboard full-width avec grille de KPI cards, graphiques, et tableaux synthétiques.

#### KPI Cards (ligne du haut)
| KPI | Détails |
|---|---|
| Clients actifs | Total + variation vs mois précédent |
| Total dépôts | Cumul en FCFA |
| Crédits en cours | Nombre + encours total |
| Taux de remboursement | % + tendance |
| Revenus du mois | Intérêts + frais collectés |
| Solde global caisses | Cumul toutes agences |

#### Graphiques
| Graphique | Type |
|---|---|
| Évolution dépôts vs crédits | Barres empilées (12 mois glissants) |
| Nouveaux clients par mois | Courbe |
| Répartition crédits par produit | Camembert |
| Taux de défaut par agence | Barres horizontales |
| Top 5 agents (crédit soumis) | Classement avec barres |

#### Filtres globaux du tableau de bord
- Sélecteur de période (Mois / Trimestre / Année)
- Sélecteur d'agence (Toutes / Agence X)

#### Comportements UX
- **Rafraîchissement automatique** : toutes les 5 minutes (avec indicateur discret)
- **Drill-down** : clic sur un KPI → page de détail
- **Export** : bouton "Exporter le rapport direction" (PDF formaté)
- **Date de dernière mise à jour** : affichée discrètement

---

### S6-05 · Journal d'Audit

**URL** : `/audit/journal`
**Rôles** : Admin, Compliance Officer

#### Layout
Page full-width avec filtres puissants + table dense + export.

#### Filtres
| Filtre | Type |
|---|---|
| Période | Range de dates + heures |
| Utilisateur | Select |
| Type d'action | Multiselect (CONNEXION / CRÉATION / MODIFICATION / SUPPRESSION / DÉCISION / TRANSACTION) |
| Entité concernée | Select (CLIENT / COMPTE / CRÉDIT / CAISSE / PARAMÈTRE) |
| ID de l'entité | Input texte |
| Adresse IP | Input texte |

#### Colonnes de la table
| Colonne | Détails |
|---|---|
| Date/Heure | Horodatage précis (JJ/MM/AAAA HH:MM:SS) |
| Utilisateur | Login + nom complet |
| Action | Badge coloré |
| Entité | Type + ID |
| Détail | Description courte de l'action |
| IP | Adresse IP source |
| Résultat | SUCCÈS / ÉCHEC |

#### Comportements UX
- **Lignes d'échec** : surlignées en rouge pâle
- **Détail expandable** : clic sur une ligne → détail JSON diff (avant/après pour les modifications)
- **Export** : CSV non modifiable (piste d'audit légale)
- **Immutabilité** : aucune action de modification n'est disponible sur ce journal

---

## 📱 Comportements Transversaux & Accessibilité

### Responsive Design
- **Desktop** (≥ 1200px) : sidebar visible, mise en page complète
- **Tablette** (768–1199px) : sidebar collapsible, layouts à 1–2 colonnes
- **Mobile** (< 768px) : sidebar en drawer, formulaires plein écran

### Gestion des erreurs
- **404** : page d'erreur illustrée + lien retour accueil
- **403** : page "Accès refusé" avec rôle requis expliqué
- **500** : page "Erreur serveur" + bouton "Réessayer"
- **Timeout réseau** : toast d'avertissement + retry automatique

### États de chargement
- **Tables** : skeleton loader (lignes animées grises)
- **Cards KPI** : placeholder animé
- **Formulaires** : bouton de soumission avec spinner, désactivé pendant l'appel

### Sécurité côté UI
- **Session expirée** : modal de reconnexion (sans perte de données en cours)
- **Inactivité** : déconnexion automatique après 30 minutes, avertissement à 5 minutes
- **Rôles** : boutons et menus masqués ou désactivés selon le rôle de l'utilisateur connecté (pas seulement le backend)

### Notifications en temps réel
- Transactions 4-eyes en attente → badge dans la sidebar
- Nouvelles demandes KYC → notification dans l'interface
- Erreurs de paiement → alerte dans le dashboard caisse

---

*Document généré pour le projet Microfinance — Sprint 1 à 6 — Version 1.0*
