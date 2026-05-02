package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.ListePepRepository;
import com.microfinance.core_banking.repository.extension.ListeSanctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AmlService {

    private static final BigDecimal SEUIL_TRANSACTION_SUSPECTE = new BigDecimal("1000000");

    private final ListeSanctionRepository listeSanctionRepository;
    private final ListePepRepository listePepRepository;
    private final ConformiteExtensionService conformiteExtensionService;

    public AmlService(ListeSanctionRepository listeSanctionRepository,
                      ListePepRepository listePepRepository,
                      ConformiteExtensionService conformiteExtensionService) {
        this.listeSanctionRepository = listeSanctionRepository;
        this.listePepRepository = listePepRepository;
        this.conformiteExtensionService = conformiteExtensionService;
    }

    @Transactional
    public List<AlerteConformite> screenerClient(Client client) {
        List<AlerteConformite> alertes = new ArrayList<>();

        List<ListeSanction> sanctionsMatch = listeSanctionRepository.findByNomCompletContainingIgnoreCase(
                client.getNom() + " " + client.getPrenom());
        for (ListeSanction sanction : sanctionsMatch) {
            if (sanction.getActif()) {
                AlerteConformite alerte = conformiteExtensionService.creerAlerteInterne(
                        "SCREENING_SANCTIONS",
                        "Client '" + client.getNom() + " " + client.getPrenom()
                                + "' correspond a une personne sous sanction: "
                                + sanction.getNomComplet() + " (" + sanction.getTypeSanction() + ")",
                        client, null, "ELEVE"
                );
                alertes.add(alerte);
            }
        }

        List<ListePep> pepMatch = listePepRepository.findByNomCompletContainingIgnoreCase(
                client.getNom() + " " + client.getPrenom());
        for (ListePep pep : pepMatch) {
            if (pep.getActif()) {
                AlerteConformite alerte = conformiteExtensionService.creerAlerteInterne(
                        "SCREENING_PEP",
                        "Client '" + client.getNom() + " " + client.getPrenom()
                                + "' identifie comme personne politiquement exposee: "
                                + pep.getNomComplet() + " (" + pep.getFonction() + ")",
                        client, null, pep.getNiveauRisque() != null ? pep.getNiveauRisque() : "MOYEN"
                );
                alertes.add(alerte);
            }
        }

        return alertes;
    }

    @Transactional(readOnly = true)
    public boolean estSousSanction(String nomComplet) {
        return listeSanctionRepository.findByNomCompletContainingIgnoreCase(nomComplet)
                .stream().anyMatch(ListeSanction::getActif);
    }

    @Transactional(readOnly = true)
    public List<ListePep> identifierPep(String nomComplet) {
        return listePepRepository.findByNomCompletContainingIgnoreCase(nomComplet)
                .stream().filter(ListePep::getActif).toList();
    }

    @Transactional
    public AlerteConformite analyserTransaction(Transaction transaction, Client client) {
        String niveauRisque = "FAIBLE";
        List<String> motifs = new ArrayList<>();

        if (transaction.getMontantGlobal() != null
                && transaction.getMontantGlobal().compareTo(SEUIL_TRANSACTION_SUSPECTE) >= 0) {
            motifs.add("Montant eleve: " + transaction.getMontantGlobal());
            niveauRisque = "ELEVE";
        }

        if (client != null && "ELEVE".equals(client.getNiveauRisque())) {
            motifs.add("Client a haut risque");
            niveauRisque = "ELEVE";
        }

        if (estSousSanction(client != null ? client.getNom() + " " + client.getPrenom() : "")) {
            motifs.add("Correspondance sanctions");
            niveauRisque = "TRES_ELEVE";
        }

        if (motifs.isEmpty()) {
            return null;
        }

        return conformiteExtensionService.creerAlerteInterne(
                "SURVEILLANCE_TRANSACTION",
                "Transaction suspecte: " + String.join(", ", motifs),
                client, transaction, niveauRisque
        );
    }
}
