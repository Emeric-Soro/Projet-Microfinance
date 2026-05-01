package com.microfinance.core_banking.service.extension;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeatureInventoryService {

    public Map<String, Object> buildInventory() {
        Map<String, Object> inventory = new LinkedHashMap<>();
        inventory.put("fonctionnalitesExistantes", List.of(
                "Gestion des clients et du KYC de base",
                "Gestion des utilisateurs, roles, JWT, OTP et verrouillage de compte",
                "Gestion des comptes, cartes, agios et tarification",
                "Transactions depot, retrait, virement avec workflow superviseur partiel",
                "Notifications et audit applicatif"
        ));
        inventory.put("fonctionnalitesNouvelles", List.of(
                "Organisation multi-agences avec regions, agences, guichets et affectations",
                "Rattachement agence sur clients, comptes, utilisateurs et transactions",
                "Grand livre bancaire avec plan comptable, journaux, schemas, pieces, grand livre, balance et clotures",
                "Catalogue credit, demandes, deblocage relie aux transactions, echeancier, garanties, remboursements, impayes et provisions",
                "Catalogue epargne avancee et depot a terme relie a un compte support",
                "Gestion des caisses, sessions, coffres, approvisionnements et delestages",
                "Maker-checker branche sur plusieurs creations critiques via actions en attente",
                "Conformite partiellement automatisee sur KYC, transactions, rescans, BIC, ratios prudentiels et fiscalite",
                "Paiements externes via mobile money, factures, recharge, ordres de compensation, RTGS et reglement monetique",
                "Risque et ALM de base via registre des risques, incidents, stress tests et tableau de liquidite",
                "Support entreprise minimal via employes, budgets, achats, paie et immobilisations",
                "Canaux digitaux via appareils clients et partenaires API"
        ));
        inventory.put("couvertureModulesPRD", Map.ofEntries(
                Map.entry("multiAgences", "partiel renforce"),
                Map.entry("grandLivreEtComptabilite", "partiel renforce"),
                Map.entry("credit", "partiel renforce"),
                Map.entry("epargneEtDAT", "partiel renforce"),
                Map.entry("caissesEtFinDeJournee", "partiel renforce"),
                Map.entry("makerChecker", "partiel renforce"),
                Map.entry("conformite", "partiel renforce"),
                Map.entry("paiementsExternesEtMobileMoney", "partiel renforce"),
                Map.entry("risquesEtAlm", "partiel"),
                Map.entry("supportEntreprise", "minimal"),
                Map.entry("digitalOpenBanking", "partiel")
        ));
        return inventory;
    }
}
