package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.ConsulterBicServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerAlerteServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerRapportServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.GenererRapportFiscalServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.GenererRapportPrudentielServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RescannerClientServiceRequestDTO;
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
    public AlerteConformite creerAlerte(CreerAlerteServiceRequestDTO dto) {
        AlerteConformite alerte = new AlerteConformite();
        alerte.setReferenceAlerte("ALR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        alerte.setTypeAlerte(dto.getTypeAlerte());
        alerte.setNiveauRisque(dto.getNiveauRisque());
        alerte.setResume(dto.getResume());
        alerte.setDetails(dto.getDetails());
        alerte.setStatut(dto.getStatut());
        alerte.setDateDetection(LocalDateTime.now());
        if (dto.getIdClient() != null) {
            Client client = clientRepository.findById(Long.valueOf(dto.getIdClient()))
                    .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
            alerte.setClient(client);
        }
        if (dto.getIdTransaction() != null) {
            Transaction transaction = transactionRepository.findById(Long.valueOf(dto.getIdTransaction()))
                    .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable"));
            alerte.setTransaction(transaction);
        }
        return alerteConformiteRepository.save(alerte);
    }

    @Transactional
    public AlerteConformite creerAlerteInterne(String typeAlerte, String resume,
                                                Client client, Transaction transaction,
                                                String niveauRisque) {
        CreerAlerteServiceRequestDTO dto = new CreerAlerteServiceRequestDTO();
        dto.setTypeAlerte(typeAlerte);
        dto.setNiveauRisque(niveauRisque != null ? niveauRisque : "MOYEN");
        dto.setResume(resume != null ? resume : "Alerte " + typeAlerte);
        dto.setStatut("OUVERTE");
        dto.setIdClient(client != null ? client.getIdClient().toString() : null);
        dto.setIdTransaction(transaction != null ? transaction.getIdTransaction().toString() : null);
        return creerAlerte(dto);
    }

    @Transactional
    public AlerteConformite creerAlerteBic(Client client, BigDecimal encoursTotal) {
        CreerAlerteServiceRequestDTO dto = new CreerAlerteServiceRequestDTO();
        dto.setTypeAlerte("BIC_ENDETTEMENT_ELEVE");
        dto.setNiveauRisque("ELEVE");
        dto.setResume("Endettement total depasse le seuil BIC pour le client "
                + client.getNom() + " " + client.getPrenom() + " : "
                + (encoursTotal != null ? encoursTotal : "0") + " XOF");
        dto.setStatut("OUVERTE");
        dto.setIdClient(client.getIdClient().toString());
        return creerAlerte(dto);
    }

    @Transactional
    public RapportReglementaire creerRapport(CreerRapportServiceRequestDTO dto) {
        RapportReglementaire rapport = new RapportReglementaire();
        rapport.setCodeRapport(dto.getCodeRapport());
        rapport.setTypeRapport(dto.getTypeRapport());
        rapport.setPeriode(dto.getPeriode());
        rapport.setStatut(dto.getStatut());
        rapport.setCheminFichier(dto.getCheminFichier());
        rapport.setCommentaire(dto.getCommentaire());
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
    public AlerteConformite rescannerClient(Long idClient, RescannerClientServiceRequestDTO dto) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        analyserClient(client, dto.getOrigine());
        if (dto.getSanctionHit()) {
            CreerAlerteServiceRequestDTO alerteDto = new CreerAlerteServiceRequestDTO();
            alerteDto.setTypeAlerte("SCREENING_SANCTIONS");
            alerteDto.setNiveauRisque(dto.getNiveauRisque() == null ? "CRITIQUE" : dto.getNiveauRisque());
            alerteDto.setResume("Correspondance potentielle sanctions/PPE pour le client " + client.getCodeClient());
            alerteDto.setDetails(dto.getDetails() == null ? "Rescanning manuel positif" : dto.getDetails());
            alerteDto.setIdClient(String.valueOf(client.getIdClient()));
            return creerAlerte(alerteDto);
        }
        CreerAlerteServiceRequestDTO alerteDto = new CreerAlerteServiceRequestDTO();
        alerteDto.setTypeAlerte("SCREENING_PERIODIQUE");
        alerteDto.setNiveauRisque(client.getNiveauRisque().name());
        alerteDto.setResume("Rescanning periodicite client " + client.getCodeClient());
        alerteDto.setDetails(dto.getDetails() == null ? "Aucune anomalie bloquante" : dto.getDetails());
        alerteDto.setIdClient(String.valueOf(client.getIdClient()));
        return creerAlerte(alerteDto);
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
    public RapportReglementaire enregistrerConsultationBic(ConsulterBicServiceRequestDTO dto) {
        Client client = clientRepository.findById(Long.valueOf(dto.getIdClient()))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        RapportReglementaire rapport = new RapportReglementaire();
        rapport.setCodeRapport(dto.getCodeRapport() == null ? "BIC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase() : dto.getCodeRapport());
        rapport.setTypeRapport("BIC_CONSULTATION");
        rapport.setPeriode(dto.getPeriode() == null ? LocalDateTime.now().toLocalDate().toString() : dto.getPeriode());
        rapport.setStatut(dto.getStatut());
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setCommentaire("Client=" + client.getCodeClient()
                + ", consentement=" + dto.getReferenceConsentement()
                + ", encoursExterne=" + dto.getEncoursExterne()
                + ", etablissements=" + dto.getNombreEtablissements());
        RapportReglementaire saved = rapportReglementaireRepository.save(rapport);
        if (dto.getEncoursExterne().compareTo(new BigDecimal("5000000")) > 0) {
            CreerAlerteServiceRequestDTO alerteDto = new CreerAlerteServiceRequestDTO();
            alerteDto.setTypeAlerte("BIC_ENDETTEMENT_ELEVE");
            alerteDto.setNiveauRisque("ELEVE");
            alerteDto.setResume("Endettement externe eleve detecte au BIC");
            alerteDto.setDetails(saved.getCommentaire());
            alerteDto.setIdClient(String.valueOf(client.getIdClient()));
            creerAlerte(alerteDto);
        }
        return saved;
    }

    @Transactional
    public RapportReglementaire genererRapportPrudentiel(GenererRapportPrudentielServiceRequestDTO dto) {
        PeriodBoundaries periodBoundaries = parsePeriod(dto.getPeriode());
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
        rapport.setCodeRapport(dto.getCodeRapport() == null ? "PRU-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase() : dto.getCodeRapport());
        rapport.setTypeRapport("RATIO_PRUDENTIEL");
        rapport.setPeriode(dto.getPeriode());
        rapport.setStatut(dto.getStatut());
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setCommentaire("Periode=" + periodBoundaries.raw()
                + ", depots=" + depots
                + ", encoursCredit=" + encours
                + ", liquidite=" + liquidite
                + ", ratioLiquidite=" + ratioLiquidite + "%");
        return rapportReglementaireRepository.save(rapport);
    }

    @Transactional
    public RapportReglementaire genererRapportFiscal(GenererRapportFiscalServiceRequestDTO dto) {
        String periode = dto.getPeriode();
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
        rapport.setCodeRapport(dto.getCodeRapport() == null ? "FIS-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase() : dto.getCodeRapport());
        rapport.setTypeRapport("FISCALITE");
        rapport.setPeriode(periode);
        rapport.setStatut(dto.getStatut());
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

    private record PeriodBoundaries(String raw, LocalDate startDate, LocalDate endDate) {
    }
}
