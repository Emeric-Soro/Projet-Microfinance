package com.soutra.microfinance.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPERVISEUR = "SUPERVISEUR";
    public static final String ROLE_CHEF_AGENCE = "CHEF_AGENCE";

    // Statuts Compte
    public static final String STATUT_COMPTE_ACTIF = "ACTIF";
    public static final String STATUT_COMPTE_BLOQUE = "BLOQUE";
    public static final String STATUT_COMPTE_FERME = "FERME";

    // Statuts Client
    public static final String STATUT_CLIENT_NOUVEAU = "NOUVEAU";
    public static final String STATUT_CLIENT_ACTIF = "ACTIF";
    public static final String STATUT_CLIENT_BLOQUE = "BLOQUE";
    public static final String STATUT_CLIENT_SUSPENDU = "SUSPENDU";
    public static final String STATUT_CLIENT_INACTIF = "INACTIF";

    // Types Transaction
    public static final String TX_DEPOT = "DEPOT";
    public static final String TX_RETRAIT = "RETRAIT";
    public static final String TX_VIREMENT = "VIREMENT";
    public static final String TX_PAIEMENT_CARTE = "PAIEMENT_CARTE";

    // Statuts Credit
    public static final String CREDIT_APPROUVE = "APPROUVE";
    public static final String CREDIT_DECAISSE = "DECAISSE";
    public static final String CREDIT_EN_COURS = "EN_COURS";
    public static final String CREDIT_EN_RETARD = "EN_RETARD";
    public static final String CREDIT_SOLDE = "SOLDE";
}
