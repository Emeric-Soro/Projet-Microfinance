package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.AlerteConformite;
import com.microfinance.core_banking.entity.Agio;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Coffre;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.RapportReglementaire;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.AlerteConformiteRepository;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.CoffreRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.RapportReglementaireRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.tarification.AgioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ConformiteExtensionService {

    private final AlerteConformiteRepository alerteConformiteRepository;
    private final RapportReglementaireRepository rapportReglementaireRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final CreditRepository creditRepository;
    private final CompteRepository compteRepository;
    private final CaisseRepository caisseRepository;
    private final CoffreRepository coffreRepository;
    private final AgioRepository agioRepository;
    private final LigneEcritureComptableRepository ligneEcritureComptableRepository;

    public ConformiteExtensionService(
            AlerteConformiteRepository alerteConformiteRepository,
            RapportReglementaireRepository rapportReglementaireRepository,
            ClientRepository clientRepository,
            TransactionRepository transactionRepository,
            CreditRepository creditRepository,
            CompteRepository compteRepository,
            CaisseRepository caisseRepository,
            CoffreRepository coffreRepository,
            AgioRepository agioRepository,
            LigneEcritureComptableRepository ligneEcritureComptableRepository
    ) {
        this.alerteConformiteRepository = alerteConformiteRepository;
        this.rapportReglementaireRepository = rapportReglementaireRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.creditRepository = creditRepository;
        this.compteRepository = compteRepository;
        this.caisseRepository = caisseRepository;
        this.coffreRepository = coffreRepository;
        this.agioRepository = agioRepository;
        this.ligneEcritureComptableRepository = ligneEcritureComptableRepository;
    }

    @Transactional
    public AlerteConformite creerAlerte(Map<String, Object> payload) {
        AlerteConformite alerte = new AlerteConformite();
        alerte.setReferenceAlerte("ALR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        alerte.setTypeAlerte(required(payload, "typeAlerte"));
        alerte.setNiveauRisque(required(payload, "niveauRisque"));
        alerte.setResume(required(payload, "resume"));
        alerte.setDetails((String) payload.get("details"));
        alerte.setStatut(defaulted(payload, "statut", "OUVERTE"));
        alerte.setDateDetection(LocalDateTime.now());
        if (payload.get("idClient") != null) {
            Client client = clientRepository.findById(Long.valueOf(payload.get("idClient").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
            alerte.setClient(client);
        }
        if (payload.get("idTransaction") != null) {
            Transaction transaction = transactionRepository.findById(Long.valueOf(payload.get("idTransaction").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable"));
            alerte.setTransaction(transaction);
        }
        return alerteConformiteRepository.save(alerte);
    }

    @Transactional
    public RapportReglementaire creerRapport(Map<String, Object> payload) {
        RapportReglementaire rapport = new RapportReglementaire();
        rapport.setCodeRapport(required(payload, "codeRapport"));
        rapport.setTypeRapport(required(payload, "typeRapport"));
        rapport.setPeriode(required(payload, "periode"));
        rapport.setStatut(defaulted(payload, "statut", "BROUILLON"));
        rapport.setCheminFichier((String) payload.get("cheminFichier"));
        rapport.setCommentaire((String) payload.get("commentaire"));
        if ("GENERE".equalsIgnoreCase(rapport.getStatut()) || "VALIDE".equalsIgnoreCase(rapport.getStatut())) {
            rapport.setDateGeneration(LocalDateTime.now());
        }
        return rapportReglementaireRepository.save(rapport);
    }

    @Transactional(readOnly = true)
    public List<AlerteConformite> listerAlertes() {
        return alerteConformiteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RapportReglementaire> listerRapports() {
        return rapportReglementaireRepository.findAll();
    }

    @Transactional
    public AlerteConformite rescannerClient(Long idClient, Map<String, Object> payload) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        analyserClient(client, defaulted(payload, "origine", "RESCAN"));
        if (Boolean.parseBoolean(String.valueOf(payload.getOrDefault("sanctionHit", false)))) {
            return creerAlerte(Map.of(
                    "typeAlerte", "SCREENING_SANCTIONS",
                    "niveauRisque", defaulted(payload, "niveauRisque", "CRITIQUE"),
                    "resume", "Correspondance potentielle sanctions/PPE pour le client " + client.getCodeClient(),
                    "details", defaulted(payload, "details", "Rescanning manuel positif"),
                    "idClient", client.getIdClient()
            ));
        }
        return creerAlerte(Map.of(
                "typeAlerte", "SCREENING_PERIODIQUE",
                "niveauRisque", client.getNiveauRisque().name(),
                "resume", "Rescanning periodicite client " + client.getCodeClient(),
                "details", defaulted(payload, "details", "Aucune anomalie bloquante"),
                "idClient", client.getIdClient()
        ));
    }

    @Transactional
    public AlerteConformite rescannerTransaction(Long idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable"));
        analyserTransaction(transaction);
        return alerteConformiteRepository.findAll().stream()
                .filter(alerte -> alerte.getTransaction() != null && idTransaction.equals(alerte.getTransaction().getIdTransaction()))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Aucune alerte n'a ete generee pour cette transaction"));
    }

    @Transactional
    public RapportReglementaire enregistrerConsultationBic(Map<String, Object> payload) {
        Client client = clientRepository.findById(Long.valueOf(required(payload, "idClient")))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        BigDecimal encoursExterne = payload.get("encoursExterne") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("encoursExterne").toString());
        RapportReglementaire rapport = new RapportReglementaire();
        rapport.setCodeRapport(defaulted(payload, "codeRapport", "BIC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()));
        rapport.setTypeRapport("BIC_CONSULTATION");
        rapport.setPeriode(defaulted(payload, "periode", LocalDateTime.now().toLocalDate().toString()));
        rapport.setStatut(defaulted(payload, "statut", "GENERE"));
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setCommentaire("Client=" + client.getCodeClient()
                + ", consentement=" + defaulted(payload, "referenceConsentement", "N/A")
                + ", encoursExterne=" + encoursExterne
                + ", etablissements=" + defaulted(payload, "nombreEtablissements", "0"));
        RapportReglementaire saved = rapportReglementaireRepository.save(rapport);
        if (encoursExterne.compareTo(new BigDecimal("5000000")) > 0) {
            creerAlerte(Map.of(
                    "typeAlerte", "BIC_ENDETTEMENT_ELEVE",
                    "niveauRisque", "ELEVE",
                    "resume", "Endettement externe eleve detecte au BIC",
                    "details", saved.getCommentaire(),
                    "idClient", client.getIdClient()
            ));
        }
        return saved;
    }

    @Transactional
    public RapportReglementaire genererRapportPrudentiel(Map<String, Object> payload) {
        PeriodBoundaries periodBoundaries = parsePeriod(required(payload, "periode"));
        List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository.findByEcritureComptable_DateComptableBetween(
                LocalDate.of(2000, 1, 1),
                periodBoundaries.endDate()
        );
        BigDecimal depots = soldeCompteComptable(lignes, "251");
        BigDecimal encours = soldeCompteComptable(lignes, "261");
        BigDecimal caisse = soldeCompteComptable(lignes, "572");
        BigDecimal coffre = soldeCompteComptable(lignes, "571");
        BigDecimal liquidite = caisse.add(coffre);
        BigDecimal ratioLiquidite = depots.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : liquidite.multiply(BigDecimal.valueOf(100)).divide(depots, 2, RoundingMode.HALF_UP);
        RapportReglementaire rapport = new RapportReglementaire();
        rapport.setCodeRapport(defaulted(payload, "codeRapport", "PRU-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()));
        rapport.setTypeRapport("RATIO_PRUDENTIEL");
        rapport.setPeriode(required(payload, "periode"));
        rapport.setStatut(defaulted(payload, "statut", "GENERE"));
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setCommentaire("Periode=" + periodBoundaries.raw()
                + ", depots=" + depots
                + ", encoursCredit=" + encours
                + ", liquidite=" + liquidite
                + ", ratioLiquidite=" + ratioLiquidite + "%");
        return rapportReglementaireRepository.save(rapport);
    }

    @Transactional
    public RapportReglementaire genererRapportFiscal(Map<String, Object> payload) {
        String periode = required(payload, "periode");
        PeriodBoundaries periodBoundaries = parsePeriod(periode);
        BigDecimal totalAgios = agioRepository.findByDateCalculBetween(periodBoundaries.startDate(), periodBoundaries.endDate()).stream()
                .map(Agio::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFraisTransactions = transactionRepository.findByDateHeureTransactionBetweenOrderByDateHeureTransactionAsc(
                        periodBoundaries.startDate().atStartOfDay(),
                        periodBoundaries.endDate().plusDays(1).atStartOfDay().minusSeconds(1)
                ).stream()
                .map(Transaction::getFrais)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        RapportReglementaire rapport = new RapportReglementaire();
        rapport.setCodeRapport(defaulted(payload, "codeRapport", "FIS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()));
        rapport.setTypeRapport("FISCALITE");
        rapport.setPeriode(periode);
        rapport.setStatut(defaulted(payload, "statut", "GENERE"));
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setCommentaire("Agios=" + totalAgios + ", fraisTransactions=" + totalFraisTransactions + ", baseTaxable=" + totalAgios.add(totalFraisTransactions));
        return rapportReglementaireRepository.save(rapport);
    }

    @Transactional
    public void analyserClient(Client client, String origine) {
        if (client == null) {
            return;
        }
        if (Boolean.TRUE.equals(client.getPep()) || client.getNiveauRisque() == NiveauRisqueClient.ELEVE || client.getNiveauRisque() == NiveauRisqueClient.CRITIQUE) {
            AlerteConformite alerte = new AlerteConformite();
            alerte.setReferenceAlerte("ALR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
            alerte.setTypeAlerte("SCREENING_CLIENT");
            alerte.setNiveauRisque(client.getNiveauRisque() == null ? "MODERE" : client.getNiveauRisque().name());
            alerte.setStatut("OUVERTE");
            alerte.setClient(client);
            alerte.setResume("Client a vigilance renforcee detecte lors de " + origine);
            alerte.setDetails("PEP=" + client.getPep() + ", niveauRisque=" + client.getNiveauRisque());
            alerte.setDateDetection(LocalDateTime.now());
            alerteConformiteRepository.save(alerte);
        }
    }

    @Transactional
    public void analyserTransaction(Transaction transaction) {
        if (transaction == null || transaction.getMontantGlobal() == null) {
            return;
        }
        Client clientRisque = transaction.getCompteSource() != null ? transaction.getCompteSource().getClient()
                : transaction.getCompteDestination() != null ? transaction.getCompteDestination().getClient() : null;
        boolean montantEleve = transaction.getMontantGlobal().compareTo(new java.math.BigDecimal("1000000")) >= 0;
        boolean clientSensible = clientRisque != null
                && (Boolean.TRUE.equals(clientRisque.getPep())
                || clientRisque.getNiveauRisque() == NiveauRisqueClient.ELEVE
                || clientRisque.getNiveauRisque() == NiveauRisqueClient.CRITIQUE);
        if (!montantEleve && !clientSensible) {
            return;
        }
        AlerteConformite alerte = new AlerteConformite();
        alerte.setReferenceAlerte("ALR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        alerte.setTypeAlerte("SURVEILLANCE_TRANSACTION");
        alerte.setNiveauRisque(clientSensible ? "ELEVE" : "MODERE");
        alerte.setStatut("OUVERTE");
        alerte.setClient(clientRisque);
        alerte.setTransaction(transaction);
        alerte.setResume("Transaction a surveiller: " + transaction.getReferenceUnique());
        alerte.setDetails("Montant=" + transaction.getMontantGlobal() + ", agenceOperation="
                + (transaction.getAgenceOperation() == null ? null : transaction.getAgenceOperation().getCodeAgence()));
        alerte.setDateDetection(LocalDateTime.now());
        alerteConformiteRepository.save(alerte);
    }

    private BigDecimal soldeCompteComptable(List<LigneEcritureComptable> lignes, String prefixeCompte) {
        return lignes.stream()
                .filter(ligne -> ligne.getCompteComptable() != null
                        && ligne.getCompteComptable().getNumeroCompte() != null
                        && ligne.getCompteComptable().getNumeroCompte().startsWith(prefixeCompte))
                .map(ligne -> "DEBIT".equalsIgnoreCase(ligne.getSens()) ? ligne.getMontant() : ligne.getMontant().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();
    }

    private PeriodBoundaries parsePeriod(String rawPeriod) {
        if (rawPeriod.matches("\\d{4}-\\d{2}")) {
            LocalDate startDate = LocalDate.parse(rawPeriod + "-01");
            return new PeriodBoundaries(rawPeriod, startDate, startDate.withDayOfMonth(startDate.lengthOfMonth()));
        }
        if (rawPeriod.matches("\\d{4}-\\d{2}-\\d{2}")) {
            LocalDate day = LocalDate.parse(rawPeriod);
            return new PeriodBoundaries(rawPeriod, day, day);
        }
        if (rawPeriod.contains("/")) {
            String[] tokens = rawPeriod.split("/", 2);
            LocalDate startDate = LocalDate.parse(tokens[0].trim());
            LocalDate endDate = LocalDate.parse(tokens[1].trim());
            return new PeriodBoundaries(rawPeriod, startDate, endDate);
        }
        throw new IllegalArgumentException("Format de periode non supporte: " + rawPeriod);
    }

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    private String defaulted(Map<String, Object> payload, String key, String defaultValue) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? defaultValue : value.toString().trim();
    }

    private record PeriodBoundaries(String raw, LocalDate startDate, LocalDate endDate) {
    }
}
