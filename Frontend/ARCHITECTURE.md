# Architecture Frontend Backoffice

Ce dossier suit les spécifications `docs/UI_UX_Specifications_Backoffice.md`.

## Structure

```text
Frontend/
  index.html                  # Entrée statique vers le login
  pages/                      # Écrans HTML du backoffice
  assets/
    css/backoffice.css        # Design system, layout, composants
    js/app.js                 # Comportements transversaux
    img/logo.png              # Identité visuelle partagée
```

## Règles appliquées

- Tokens CSS nommés comme la spec : `--color-primary`, `--color-secondary`, `--color-accent`, tokens sémantiques et neutres.
- Layout backoffice commun : sidebar 240px, topbar, breadcrumb, zone de contenu.
- Navigation générée dans `assets/js/app.js` par groupes MVP : Tableau de bord, Clients, Comptes, Opérations, Paramétrage, Sécurité.
- Composants partagés : badges statut, tables triables, pagination, toasts, modals, stepper, upload zone, skeleton loader, empty state.
- Pages de sécurité et erreurs : `login.html`, `otp.html`, `403.html`, `404.html`, `500.html`.

## Convention pour les nouvelles pages

Une page métier doit être placée dans `pages/`, charger `../assets/css/backoffice.css` et `../assets/js/app.js`, puis réutiliser les classes du design system au lieu d’ajouter du style inline.
