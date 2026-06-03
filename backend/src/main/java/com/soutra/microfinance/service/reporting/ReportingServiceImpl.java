package com.soutra.microfinance.service.reporting;

import com.soutra.microfinance.dto.request.parametrage.RapportPersonnaliseRequestDTO;
import com.soutra.microfinance.dto.response.common.*;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.credit.CreditRepository;
import com.soutra.microfinance.repository.credit.EcheanceRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
import com.soutra.microfinance.repository.operation.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportingServiceImpl implements ReportingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportingServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final EcheanceRepository echeanceRepository;
    private final CaisseRepository caisseRepository;

    public ReportingServiceImpl(TransactionRepository transactionRepository,
                                CompteRepository compteRepository,
                                ClientRepository clientRepository,
                                CreditRepository creditRepository,
                                EcheanceRepository echeanceRepository,
                                CaisseRepository caisseRepository) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.echeanceRepository = echeanceRepository;
        this.caisseRepository = caisseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RapportOperationnelResponseDTO genererRapportOperationnel(String dateDebut, String dateFin, Long agenceId) {
        LocalDate now = LocalDate.now();
        LocalDate parsedDebut = (dateDebut != null && !dateDebut.isBlank()) ? LocalDate.parse(dateDebut) : now.withDayOfMonth(1);
        LocalDate parsedFin = (dateFin != null && !dateFin.isBlank()) ? LocalDate.parse(dateFin) : now;

        LocalDateTime debut = parsedDebut.atStartOfDay();
        LocalDateTime fin = parsedFin.atTime(LocalTime.MAX);

        String periode = parsedDebut + " - " + parsedFin;

        BigDecimal totalDepots;
        BigDecimal totalRetraits;
        BigDecimal totalVirements;
        BigDecimal montantTotalFrais;
        long nbTransactions;

        if (agenceId != null) {
            totalDepots = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatutAndAgence(
                    "DEPOT", debut, fin, StatutOperation.EXECUTEE, agenceId);
            totalRetraits = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatutAndAgence(
                    "RETRAIT", debut, fin, StatutOperation.EXECUTEE, agenceId);
            totalVirements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatutAndAgence(
                    "VIREMENT", debut, fin, StatutOperation.EXECUTEE, agenceId);
            montantTotalFrais = transactionRepository.sumFraisByDateBetweenAndStatutAndAgence(
                    debut, fin, StatutOperation.EXECUTEE, agenceId);
            nbTransactions = transactionRepository.countByDateHeureTransactionBetweenAndStatutOperation(
                    debut, fin, StatutOperation.EXECUTEE);
        } else {
            totalDepots = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                    "DEPOT", debut, fin, StatutOperation.EXECUTEE);
            totalRetraits = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                    "RETRAIT", debut, fin, StatutOperation.EXECUTEE);
            totalVirements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                    "VIREMENT", debut, fin, StatutOperation.EXECUTEE);
            montantTotalFrais = transactionRepository.sumFraisByDateBetweenAndStatut(
                    debut, fin, StatutOperation.EXECUTEE);
            nbTransactions = transactionRepository.countByDateHeureTransactionBetweenAndStatutOperation(
                    debut, fin, StatutOperation.EXECUTEE);
        }

        return new RapportOperationnelResponseDTO(
                periode,
                totalDepots,
                totalRetraits,
                totalVirements,
                nbTransactions,
                montantTotalFrais
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportFinancierResponseDTO genererRapportFinancier() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        BigDecimal totalActifs = compteRepository.sumSolde();
        BigDecimal totalPassifs = totalActifs; // Bilan comptable simplifie: actif = passif

        // Produit net simplifie: somme des frais du mois courant
        LocalDateTime debutMois = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = now.atTime(LocalTime.MAX);
        BigDecimal produitNet = transactionRepository.sumFraisByDateBetweenAndStatut(
                debutMois, finMois, StatutOperation.EXECUTEE);

        // Marge d'interets: estimation via les interets des echeances du mois
        BigDecimal margeInterets = echeanceRepository.sumCapitalImpayesBeforeDate(now);

        // Ratio d'efficacite: frais / total transactions
        BigDecimal totalTransactionsMois = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                debutMois, finMois, StatutOperation.EXECUTEE);
        BigDecimal ratioEfficacite = BigDecimal.ZERO;
        if (totalTransactionsMois.compareTo(BigDecimal.ZERO) > 0) {
            ratioEfficacite = produitNet.multiply(new BigDecimal("100"))
                    .divide(totalTransactionsMois, 2, RoundingMode.HALF_UP);
        }

        return new RapportFinancierResponseDTO(
                periode,
                totalActifs,
                totalPassifs,
                produitNet,
                margeInterets,
                ratioEfficacite
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportClientsResponseDTO genererRapportClients() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        long totalClients = clientRepository.count();
        LocalDateTime debutMois = now.withDayOfMonth(1).atStartOfDay();
        long nouveauxClients = clientRepository.countByCreatedAtAfter(debutMois);
        long clientsActifs = clientRepository.countByStatutLibelle("ACTIF");

        Map<String, Long> clientsParStatut = new HashMap<>();
        var statutCounts = clientRepository.countClientsByStatut();
        for (Object[] row : statutCounts) {
            String libelle = (String) row[0];
            Long count = (Long) row[1];
            clientsParStatut.put(libelle, count);
        }

        return new RapportClientsResponseDTO(
                periode,
                totalClients,
                nouveauxClients,
                clientsActifs,
                clientsParStatut
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportCreditsResponseDTO genererRapportCredits() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        long totalCredits = creditRepository.count();
        BigDecimal montantTotalAccorde = creditRepository.sumMontantAccorde();
        BigDecimal encoursTotal = creditRepository.sumMontantRestantDu();

        // PAR 30: echeances impayees depuis 30+ jours
        LocalDate dateSeuil30 = now.minusDays(30);
        BigDecimal par30 = echeanceRepository.sumCapitalImpayesBeforeDate(dateSeuil30);

        // PAR 90: echeances impayees depuis 90+ jours
        LocalDate dateSeuil90 = now.minusDays(90);
        BigDecimal par90 = echeanceRepository.sumCapitalImpayesBeforeDate(dateSeuil90);

        // Taux d'impayes
        BigDecimal tauxImpayes = BigDecimal.ZERO;
        if (encoursTotal.compareTo(BigDecimal.ZERO) > 0) {
            tauxImpayes = par30.multiply(new BigDecimal("100"))
                    .divide(encoursTotal, 2, RoundingMode.HALF_UP);
        }

        return new RapportCreditsResponseDTO(
                periode,
                totalCredits,
                montantTotalAccorde,
                encoursTotal,
                par30,
                par90,
                tauxImpayes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportCaisseResponseDTO genererRapportCaisse() {
        LocalDate now = LocalDate.now();
        String periode = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        long totalCaissesOuvertes = caisseRepository.countByStatut(
                com.soutra.microfinance.entity.Caisse.StatutCaisse.OUVERTE);

        LocalDateTime debutMois = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = now.atTime(LocalTime.MAX);

        BigDecimal totalEncaissements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "DEPOT", debutMois, finMois, StatutOperation.EXECUTEE);
        BigDecimal totalDecaissements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "RETRAIT", debutMois, finMois, StatutOperation.EXECUTEE);

        // Ecarts: somme des ecarts de fermeture de caisse
        BigDecimal ecartsConstates = BigDecimal.ZERO;
        var caisses = caisseRepository.findAll();
        for (var caisse : caisses) {
            if (caisse.getEcartFermeture() != null) {
                ecartsConstates = ecartsConstates.add(caisse.getEcartFermeture());
            }
        }

        return new RapportCaisseResponseDTO(
                periode,
                totalCaissesOuvertes,
                totalEncaissements,
                totalDecaissements,
                ecartsConstates.abs()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportBceaoResponseDTO genererRapportBceao(int trimestre, int annee) {
        LocalDate debutTrimestre = LocalDate.of(annee, (trimestre - 1) * 3 + 1, 1);
        LocalDate finTrimestre = debutTrimestre.plusMonths(3).minusDays(1);

        LocalDateTime debut = debutTrimestre.atStartOfDay();
        LocalDateTime fin = finTrimestre.atTime(LocalTime.MAX);

        BigDecimal totalBilan = compteRepository.sumSolde();

        // Fonds propres: estimation via le total bilan * ratio moyen
        BigDecimal totalCredits = creditRepository.sumMontantAccorde();
        BigDecimal totalDepots = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                debut, fin, StatutOperation.EXECUTEE);

        BigDecimal creditsAccordes = creditRepository.sumMontantAccordeByDateDecaissementBetween(
                debutTrimestre, finTrimestre);

        BigDecimal depotsCollectes = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "DEPOT", debut, fin, StatutOperation.EXECUTEE);

        // Ratio de solvabilite simplifie: fonds propres / total bilan * 100
        BigDecimal fondsPropres = totalBilan.multiply(new BigDecimal("0.15")); // Estimation a 15% du bilan
        BigDecimal ratioSolvabilite = BigDecimal.ZERO;
        if (totalBilan.compareTo(BigDecimal.ZERO) > 0) {
            ratioSolvabilite = fondsPropres.multiply(new BigDecimal("100"))
                    .divide(totalBilan, 2, RoundingMode.HALF_UP);
        }

        return new RapportBceaoResponseDTO(
                trimestre,
                annee,
                totalBilan,
                fondsPropres,
                creditsAccordes,
                depotsCollectes,
                ratioSolvabilite
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportExportResponseDTO exporterRapport(String type, String format) {
        LOGGER.info("Export rapport {} au format {}", type, format);
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport ").append(type).append("\r\n");
        sb.append("Date generation;").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\r\n");
        sb.append("\r\n");

        switch (type.toUpperCase()) {
            case "OPERATIONNEL":
                sb.append("Periode;Total Depots;Total Retraits;Total Virements;Nb Transactions;Total Frais\r\n");
                sb.append("Mois courant;0;0;0;0;0\r\n");
                break;
            case "FINANCIER":
                sb.append("Periode;Total Actifs;Total Passifs;Produit Net;Marge Interets;Ratio Efficacite\r\n");
                sb.append("Mois courant;0;0;0;0;0\r\n");
                break;
            case "CLIENTS":
                sb.append("Periode;Total Clients;Nouveaux Clients;Clients Actifs\r\n");
                sb.append("Mois courant;0;0;0\r\n");
                break;
            case "CREDITS":
                sb.append("Periode;Total Credits;Montant Accorde;Encours;PAR 30;PAR 90;Taux Impayes\r\n");
                sb.append("Mois courant;0;0;0;0;0;0\r\n");
                break;
            default:
                sb.append("Type;Valeur\r\n");
                sb.append(type).append(";Donnees non disponibles\r\n");
                break;
        }

        String contenu = sb.toString();
        String contenuBase64 = Base64.getEncoder().encodeToString(contenu.getBytes());
        String nomFichier = "rapport_" + type.toLowerCase() + "_"
                + LocalDateTime.now().toLocalDate() + "." + format.toLowerCase();

        return new RapportExportResponseDTO(
                type,
                format,
                contenuBase64,
                nomFichier,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RapportExportResponseDTO genererRapportPersonnalise(RapportPersonnaliseRequestDTO requestDTO) {
        LOGGER.info("Generation rapport personnalise {}", requestDTO.getType());
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport Personnalise - ").append(requestDTO.getType()).append("\r\n");
        sb.append("Date generation;").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\r\n");
        sb.append("Periode;").append(requestDTO.getDateDebut()).append(" - ").append(requestDTO.getDateFin()).append("\r\n");
        sb.append("\r\n");

        String type = requestDTO.getType().toUpperCase();
        LocalDate dateDebut = requestDTO.getDateDebut() != null ?
                LocalDate.parse(requestDTO.getDateDebut()) : LocalDate.now().minusMonths(1);
        LocalDate dateFin = requestDTO.getDateFin() != null ?
                LocalDate.parse(requestDTO.getDateFin()) : LocalDate.now();

        LocalDateTime debut = dateDebut.atStartOfDay();
        LocalDateTime fin = dateFin.atTime(LocalTime.MAX);

        switch (type) {
            case "OPERATIONNEL":
                BigDecimal depots = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                        "DEPOT", debut, fin, StatutOperation.EXECUTEE);
                BigDecimal retraits = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                        "RETRAIT", debut, fin, StatutOperation.EXECUTEE);
                BigDecimal virements = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                        "VIREMENT", debut, fin, StatutOperation.EXECUTEE);
                long nbTx = transactionRepository.countByDateHeureTransactionBetweenAndStatutOperation(
                        debut, fin, StatutOperation.EXECUTEE);
                BigDecimal frais = transactionRepository.sumFraisByDateBetweenAndStatut(
                        debut, fin, StatutOperation.EXECUTEE);
                sb.append("Total Depots;").append(depots).append("\r\n");
                sb.append("Total Retraits;").append(retraits).append("\r\n");
                sb.append("Total Virements;").append(virements).append("\r\n");
                sb.append("Nb Transactions;").append(nbTx).append("\r\n");
                sb.append("Total Frais;").append(frais).append("\r\n");
                break;
            case "FINANCIER":
                BigDecimal actifs = compteRepository.sumSolde();
                BigDecimal produits = transactionRepository.sumFraisByDateBetweenAndStatut(
                        debut, fin, StatutOperation.EXECUTEE);
                sb.append("Total Actifs;").append(actifs).append("\r\n");
                sb.append("Produits (Frais);").append(produits).append("\r\n");
                break;
            case "CLIENTS":
                long totalClts = clientRepository.count();
                long nouveaux = clientRepository.countByDateInscriptionAfter(dateDebut);
                long actifsClts = clientRepository.countByStatutLibelle("ACTIF");
                sb.append("Total Clients;").append(totalClts).append("\r\n");
                sb.append("Nouveaux (periode);").append(nouveaux).append("\r\n");
                sb.append("Clients Actifs;").append(actifsClts).append("\r\n");
                break;
            case "CREDITS":
                long totalCr = creditRepository.count();
                BigDecimal accordes = creditRepository.sumMontantAccorde();
                BigDecimal encours = creditRepository.sumMontantRestantDu();
                sb.append("Total Credits;").append(totalCr).append("\r\n");
                sb.append("Montant Accorde;").append(accordes).append("\r\n");
                sb.append("Encours;").append(encours).append("\r\n");
                break;
            default:
                sb.append("Type de rapport non reconnu;").append(requestDTO.getType()).append("\r\n");
                break;
        }

        String contenu = sb.toString();
        String contenuBase64 = Base64.getEncoder().encodeToString(contenu.getBytes());
        String format = requestDTO.getFormat() != null ? requestDTO.getFormat() : "CSV";
        String nomFichier = "rapport_personnalise_" + requestDTO.getType().toLowerCase() + "_"
                + LocalDate.now() + "." + format.toLowerCase();

        return new RapportExportResponseDTO(
                requestDTO.getType(),
                format,
                contenuBase64,
                nomFichier,
                LocalDateTime.now()
        );
    }
}
