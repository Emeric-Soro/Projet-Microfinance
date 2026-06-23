# Prompt — Refonte design du Frontoffice (ClientPortal) SOUTRA FINANCE

> À copier-coller tel quel dans une conversation avec Claude (ou un autre assistant IA) en lui donnant accès au repo `Projet-Microfinance`, ou en y collant le contenu de `backoffice.css` / `portal.css` en pièce jointe.

---

## Contexte

Je travaille sur **SOUTRA FINANCE**, une application web de microfinance composée de deux portails distincts dans le même repo :

- **`Frontend/`** = le **backoffice** (interface staff/agents) → design system mature, documenté, fichier `Frontend/assets/css/backoffice.css`
- **`ClientPortal/`** = le **frontoffice** (espace client e-banking) → fichier `ClientPortal/assets/css/portal.css`, actuellement dans un style **glassmorphism** (sidebar flottante avec `backdrop-filter: blur()`, blobs décoratifs en fond, cards translucides)

**Problème** : les deux portails partagent la même palette de couleurs de base (`--color-primary: #084355`, `--color-secondary: #8cd5fa`, `--color-accent: #fab482`) mais ont des **philosophies visuelles totalement différentes**. Le backoffice est dense, opaque, "enterprise admin" ; le frontoffice est aéré, translucide, "marketing/grand public". Un utilisateur qui passerait de l'un à l'autre (ou un agent qui maintient les deux) ne reconnaît pas la même marque.

## Objectif

Refaire le **design du frontoffice uniquement** (`ClientPortal/`) pour qu'il soit visuellement cohérent avec le `backoffice.css`, **sans copier bêtement le style admin** — le frontoffice doit rester un espace client agréable, rassurant et un peu plus chaleureux que le backoffice, mais en utilisant **le même langage visuel** : mêmes tokens de couleur, mêmes rayons de bordure, mêmes ombres, même typographie, mêmes patterns de composants (cards, badges, boutons, formulaires, tables).

⚠️ **Important** : il s'agit uniquement d'un travail de **design / UI / UX**. Ne touche pas à la logique JS (`portal-app.js`), aux appels API, ni à la structure fonctionnelle des pages. L'objectif est : mêmes fonctionnalités, nouveau design.

---

## Ce que je veux concrètement

### 1. Unifier les design tokens
- Repartir des variables CSS du `backoffice.css` (`--color-primary`, `--color-secondary`, `--color-accent`, leurs variantes `-light`/`-dark`, `--color-danger`, `--color-success`, `--color-warning`, `--radius-sm/md/lg/xl`, `--shadow-sm/md/lg`, `--transition`) comme source de vérité.
- Le `portal.css` actuel duplique presque les mêmes valeurs mais avec des noms de variables différents (`--border-color` au lieu de `--color-border`, pas de `--radius-*`, pas de `--shadow-*`) → harmoniser les noms pour que les deux fichiers CSS partagent le même vocabulaire de tokens, quitte à factoriser dans un futur fichier commun `tokens.css`.
- Décider explicitement : on garde un peu de transparence/blur pour le côté "client" (ça peut rester un identifiant du frontoffice), ou on l'aligne complètement sur les cards opaques du backoffice ? Présente-moi les deux options avant de trancher.

### 2. Remplacer les emojis par des icônes SVG cohérentes
Le frontoffice utilise actuellement des emojis comme icônes (📊 💳 💸 📈 ✉️ 👤 🚪 ⚠️ 💰) dans la sidebar, les metric cards et les alertes — alors que le backoffice utilise exclusivement des **icônes SVG line-style** (stroke `currentColor`, `stroke-width="2"`, viewBox `0 0 24 24`, type Feather/Lucide). Remplace tous les emojis du frontoffice par des SVG du même set visuel que le backoffice, pour la sidebar (`nav-item`), les `kpi-icon` / `metric-card`, le bouton de déconnexion, et les bannières d'alerte (KYC notamment).

### 3. Harmoniser la structure de la sidebar et de la navigation
- Backoffice : `.sidebar` (240px, fond `--color-primary` plein, scroll personnalisé), `.sidebar-header` avec logo rond, `.sidebar-user` avec avatar initiales, `.sidebar-nav` avec `.nav-section-title` + `.nav-item` (icône SVG + label + `.nav-badge` optionnel), indicateur actif = bordure gauche colorée + fond translucide secondaire.
- Frontoffice actuel : `.portal-sidebar` (260px, fond blanc translucide + blur), `.nav-menu` en `<ul>`, items avec emoji inline dans le texte du lien.
- Demande : restructurer le HTML/CSS de la sidebar client pour reprendre la même anatomie de composants (header avec logo, bloc user avec avatar, nav avec icônes séparées du label, état actif visuellement cohérent) tout en gardant une largeur et un ton adaptés à un usage grand public. Garde le mobile toggle (`.mobile-sidebar-toggle`) du même type que le backoffice puisque le frontoffice n'a pas encore ce comportement responsive.

### 4. Réutiliser les composants déjà standardisés du backoffice
Plutôt que de réinventer, reprends telles quelles (en les adaptant visuellement si besoin) les classes de composants suivantes définies dans `backoffice.css`, qui n'existent pas ou existent différemment dans `portal.css` :
- `.btn` / `.btn-primary` / `.btn-secondary` / `.btn-outline` / `.btn-ghost` / `.btn-danger` (le frontoffice a son propre système de boutons à harmoniser)
- `.badge` + variantes (`-active`, `-pending`, `-blocked`, `-info`, `-warning`, `-danger`, `-success`) pour les statuts de compte, virement, réclamation
- `.form-group` / `.form-label` / `.form-control` / `.field-error` pour tous les formulaires (login, register, virement, réclamation)
- `.data-table` pour les listes (historique de transactions, comptes, réclamations)
- `.modal-overlay` / `.modal` pour les confirmations (ex: confirmation de virement)
- `.toast-container` pour les notifications
- `.kpi-card` / `.kpi-icon` / `.kpi-value` / `.kpi-trend` comme base pour les `.metric-card` du dashboard client

### 5. Page de login
Le backoffice a déjà un layout "split-screen" abouti (`.login-wrapper`, `.login-container`, `.login-left` avec image de fond + overlay dégradé, `.login-right` avec formulaire) dans `backoffice.css`. Le login client (`ClientPortal/pages/login.html`) doit suivre **la même structure de layout**, avec un contenu/ton adapté au client final (message d'accueil différent de celui du staff, pas de lien vers le backoffice bien sûr) mais le même habillage visuel (mêmes proportions, même style de carte, même style de champs et de bouton `.btn-login-primary`).

### 6. Cohérence des pages spécifiques du frontoffice
Pour chaque page de `ClientPortal/pages/`, applique le nouveau design en gardant leur fonction actuelle :
- `dashboard.html` : metric cards alignées sur `.kpi-card`, bannière KYC alignée sur le pattern `.alerts-list` / badge du backoffice
- `comptes.html` : utiliser le pattern `.account-card` (mini) du backoffice si pertinent pour afficher les comptes du client
- `virement.html` : `.amount-input-large` du backoffice pour la saisie de montant, `.form-row` pour les champs bénéficiaire
- `credits.html` : badges de statut harmonisés, éventuellement `.gauge-circle` pour visualiser une progression de remboursement
- `reclamations.html` : `.data-table` ou liste de cards selon ce qui est déjà en place, badges de statut harmonisés
- `register.html` / `otp.html` : même traitement que le login (cohérence du parcours d'authentification/onboarding)

### 7. Responsive
Le backoffice a des breakpoints clairs (`1200px` pour les grids, `768px` pour la sidebar qui se cache + toggle mobile). Vérifie que le frontoffice suit la même logique responsive, en l'adaptant si le frontoffice doit être plus mobile-first (probable, vu que c'est un portail client).

---

## Exemple concret du niveau d'intervention attendu (sidebar)

Pour calibrer ce que j'attends, voici l'écart actuel sur un seul composant — la sidebar :

**Backoffice (`Frontend/`) — référence à suivre :**
```html
<div class="sidebar-header">
  <img src="../assets/img/logo.png" class="sidebar-logo">
  <div class="sidebar-brand">SOUTRA FINANCE</div>
  <div class="sidebar-subtitle">Backoffice</div>
</div>
<nav class="sidebar-nav">
  <a href="#" class="nav-item active">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">...</svg>
    <span>Tableau de bord</span>
  </a>
</nav>
```
Fond plein `--color-primary`, icônes SVG line-style, état actif = bordure gauche + fond translucide.

**Frontoffice (`ClientPortal/`) — état actuel à transformer :**
```html
<ul class="nav-menu">
  <li class="nav-item active"><a href="dashboard">📊 Tableau de bord</a></li>
</ul>
```
Emoji inline dans le texte du lien, pas de séparation icône/label, fond blanc translucide flouté.

**Attendu** : la version refaite doit reprendre l'anatomie du backoffice (logo header, nav avec icône SVG séparée du label, état actif cohérent) tout en gardant le ton plus aéré du frontoffice — ce n'est donc ni un copier-coller du HTML backoffice, ni un simple remplacement d'emoji par SVG en gardant tout le reste identique. C'est ce niveau de retravail structurel (pas seulement cosmétique) que j'attends sur l'ensemble des composants listés en section 4.

---

## Contraintes

- **Ne pas casser les `id` JS-bindés** : attention, le binding JS du frontoffice n'est **pas centralisé** dans `portal-app.js` (qui ne gère que 2 éléments globaux : `portalUserName` et `portalLogoutBtn`). L'essentiel de la logique est dans des **scripts inline `<script>` à l'intérieur de chaque page HTML** (ex: `virement.html` a 30 appels `getElementById`, `credits.html` en a 27, `register.html` 24, `reclamations.html` 16, `comptes.html` 18, `dashboard.html` 11). Au total ~119 ID différents sont utilisés dans `ClientPortal/pages/`. Avant de modifier la structure HTML d'une page, repère et conserve exactement tous ses `id="..."` (ou adapte le script inline correspondant si un changement est nécessaire, et signale-le clairement). Ne te fie pas uniquement à `portal-app.js` pour cet inventaire.
- **Garde le français** et le ton du contenu existant (textes, labels, messages).
- **Respecte l'accessibilité** déjà en place côté backoffice (labels de formulaire, `aria-current`, contrastes suffisants).
- Le frontoffice doit rester clairement identifiable comme un espace **différent** du backoffice (ce n'est pas un copier-coller à l'identique) — c'est une cohérence de famille de design, pas un clonage pixel-perfect.

## Méthode de travail souhaitée

1. Commence par une **analyse comparative courte** : liste les écarts de design les plus visibles entre `backoffice.css` et `portal.css` (tokens, composants, patterns) avant de toucher au code.
2. Propose une **nouvelle version de `portal.css`** réorganisée selon les mêmes sections que `backoffice.css` (Design tokens → Layout → Sidebar → Topbar/Header → Cards → Boutons → Formulaires → Tables → Badges → Modal → Toast → Responsive), en réutilisant au maximum les valeurs et noms de variables du backoffice.
3. Mets à jour le HTML des pages de `ClientPortal/pages/` une par une pour utiliser les nouvelles classes, en commençant par `login.html` et `dashboard.html`.
4. À la fin, fais-moi un résumé des changements et des points où tu as dû faire un choix de design arbitraire (pour validation).

Vas-y étape par étape, ne fais pas tout en un seul bloc — je veux pouvoir valider l'approche après l'étape 1 et 2 avant que tu touches à toutes les pages HTML.
