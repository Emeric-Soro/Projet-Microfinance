package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ClotureComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerClasseComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCompteComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerEcritureManuelleRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerJournalComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerSchemaComptableRequestDTO;
import com.microfinance.core_banking.dto.request.extension.LigneEcritureDTO;
import com.microfinance.core_banking.dto.request.extension.TesterSchemaComptableRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ControlesComptablesResponseDTO;
import com.microfinance.core_banking.dto.response.extension.BalanceLineDTO;
import com.microfinance.core_banking.dto.response.extension.EcritureDesequilibreeDTO;
import com.microfinance.core_banking.dto.response.extension.LigneSchemaTestDTO;
import com.microfinance.core_banking.dto.response.extension.SchemaTestResponseDTO;
import com.microfinance.core_banking.dto.response.extension.BootstrapResponseDTO;
import com.microfinance.core_banking.dto.response.extension.LigneGrandLivreDTO;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.ClasseComptable;
import com.microfinance.core_banking.entity.ClotureComptable;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.CompteComptable;
import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.entity.JournalComptable;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.ProvisionCredit;
import com.microfinance.core_banking.entity.RemboursementCredit;
import com.microfinance.core_banking.entity.SchemaComptable;
import com.microfinance.core_banking.entity.SensEcriture;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.dto.BalanceLine;
import com.microfinance.core_banking.service.DoubleEntryService;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.ClasseComptableRepository;
import com.microfinance.core_banking.repository.extension.ClotureComptableRepository;
import com.microfinance.core_banking.repository.extension.CompteComptableRepository;
import com.microfinance.core_banking.repository.extension.EcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.JournalComptableRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.ProvisionCreditRepository;
import com.microfinance.core_banking.repository.extension.RemboursementCreditRepository;
import com.microfinance.core_banking.repository.extension.SchemaComptableRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ComptabiliteExtensionService {

    private final ClasseComptableRepository classeComptableRepository;
    private final CompteComptableRepository compteComptableRepository;
    private final JournalComptableRepository journalComptableRepository;
    private final SchemaComptableRepository schemaComptableRepository;
    private final EcritureComptableRepository ecritureComptableRepository;
    private final LigneEcritureComptableRepository ligneEcritureComptableRepository;
    private final ClotureComptableRepository clotureComptableRepository;
    private final TransactionRepository transactionRepository;
    private final RemboursementCreditRepository remboursementCreditRepository;
    private final ProvisionCreditRepository provisionCreditRepository;
    private final AgenceRepository agenceRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ObjectProvider<CreditExtensionService> creditExtensionServiceProvider;
    private final ObjectProvider<PaiementExterneService> paiementExterneServiceProvider;
    private final ObjectProvider<com.microfinance.core_banking.service.tarification.AgioService> agioServiceProvider;
    private final DoubleEntryService doubleEntryService;

    public ComptabiliteExtensionService(
            ClasseComptableRepository classeComptableRepository,
            CompteComptableRepository compteComptableRepository,
            JournalComptableRepository journalComptableRepository,
            SchemaComptableRepository schemaComptableRepository,
            EcritureComptableRepository ecritureComptableRepository,
            LigneEcritureComptableRepository ligneEcritureComptableRepository,
            ClotureComptableRepository clotureComptableRepository,
            TransactionRepository transactionRepository,
            RemboursementCreditRepository remboursementCreditRepository,
            ProvisionCreditRepository provisionCreditRepository,
            AgenceRepository agenceRepository,
            AuthenticatedUserService authenticatedUserService,
            ObjectProvider<CreditExtensionService> creditExtensionServiceProvider,
            ObjectProvider<PaiementExterneService> paiementExterneServiceProvider,
            ObjectProvider<com.microfinance.core_banking.service.tarification.AgioService> agioServiceProvider,
            DoubleEntryService doubleEntryService
    ) {
        this.classeComptableRepository = classeComptableRepository;
        this.compteComptableRepository = compteComptableRepository;
        this.journalComptableRepository = journalComptableRepository;
        this.schemaComptableRepository = schemaComptableRepository;
        this.ecritureComptableRepository = ecritureComptableRepository;
        this.ligneEcritureComptableRepository = ligneEcritureComptableRepository;
        this.clotureComptableRepository = clotureComptableRepository;
        this.transactionRepository = transactionRepository;
        this.remboursementCreditRepository = remboursementCreditRepository;
        this.provisionCreditRepository = provisionCreditRepository;
        this.agenceRepository = agenceRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.creditExtensionServiceProvider = creditExtensionServiceProvider;
        this.paiementExterneServiceProvider = paiementExterneServiceProvider;
        this.agioServiceProvider = agioServiceProvider;
        this.doubleEntryService = doubleEntryService;
    }

    @Transactional
    public BootstrapResponseDTO bootstrapReferentiel() {
        bootstrapSiNecessaire();
        BootstrapResponseDTO response = new BootstrapResponseDTO();
        response.setClasses(classeComptableRepository.count());
        response.setComptes(compteComptableRepository.count());
        response.setJournaux(journalComptableRepository.count());
        response.setSchemas(schemaComptableRepository.count());
        return response;
    }

    @Transactional
    public ClasseComptable creerClasse(CreerClasseComptableRequestDTO payload) {
        ClasseComptable classe = new ClasseComptable();
        classe.setCodeClasse(payload.getCodeClasse());
        classe.setLibelle(payload.getLibelle());
        classe.setOrdreAffichage(payload.getOrdreAffichage() != null ? payload.getOrdreAffichage() : 0);
        return classeComptableRepository.save(classe);
    }

    @Transactional
    public CompteComptable creerCompte(CreerCompteComptableRequestDTO payload) {
        ClasseComptable classe = classeComptableRepository.findByCodeClasse(payload.getCodeClasse())
                .orElseThrow(() -> new EntityNotFoundException("Classe comptable introuvable"));
        CompteComptable compte = new CompteComptable();
        compte.setNumeroCompte(payload.getNumeroCompte());
        compte.setIntitule(payload.getIntitule());
        compte.setTypeSolde(payload.getTypeSolde() != null ? payload.getTypeSolde() : "MIXTE");
        compte.setCompteInterne(payload.getCompteInterne() != null ? payload.getCompteInterne() : false);
        compte.setClasseComptable(classe);
        if (payload.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(payload.getIdAgence())
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            compte.setAgence(agence);
        }
        return compteComptableRepository.save(compte);
    }

    @Transactional
    public JournalComptable creerJournal(CreerJournalComptableRequestDTO payload) {
        JournalComptable journal = new JournalComptable();
        journal.setCodeJournal(payload.getCodeJournal());
        journal.setLibelle(payload.getLibelle());
        journal.setTypeJournal(payload.getTypeJournal());
        journal.setActif(payload.getActif() != null ? payload.getActif() : true);
        return journalComptableRepository.save(journal);
    }

    @Transactional
    public SchemaComptable creerSchema(CreerSchemaComptableRequestDTO payload) {
        SchemaComptable schema = new SchemaComptable();
        schema.setCodeOperation(payload.getCodeOperation());
        schema.setCompteDebit(payload.getCompteDebit());
        schema.setCompteCredit(payload.getCompteCredit());
        schema.setCompteFrais(payload.getCompteFrais());
        schema.setJournalCode(payload.getJournalCode() != null ? payload.getJournalCode() : "OD");
        schema.setActif(payload.getActif() != null ? payload.getActif() : true);
        return schemaComptableRepository.save(schema);
    }

    @Transactional
    public EcritureComptable creerEcritureManuelle(CreerEcritureManuelleRequestDTO payload) {
        bootstrapSiNecessaire();
        List<LigneEcritureDTO> lignes = payload.getLignes();
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Une ecriture manuelle doit contenir au moins une ligne");
        }
        List<ManualLine> manualLines = new ArrayList<>();
        for (LigneEcritureDTO ligne : lignes) {
            manualLines.add(new ManualLine(
                    ligne.getNumeroCompte(),
                    SensEcriture.valueOf(ligne.getSens().toUpperCase()),
                    ligne.getMontant(),
                    ligne.getReferenceAuxiliaire(),
                    ligne.getLibelleAuxiliaire()
            ));
        }
        String referencePiece = payload.getReferencePiece() != null ? payload.getReferencePiece() : "MAN-" + System.currentTimeMillis();
        String referenceSource = payload.getReferenceSource() != null ? payload.getReferenceSource() : referencePiece;
        return enregistrerPiece(
                referencePiece,
                payload.getCodeJournal() != null ? payload.getCodeJournal() : "OD",
                payload.getDateComptable() != null ? payload.getDateComptable() : LocalDate.now(),
                payload.getDateValeur(),
                payload.getLibelle(),
                "MANUELLE",
                referenceSource,
                manualLines
        );
    }

    @Transactional
    public EcritureComptable comptabiliserTransaction(Transaction transaction) {
        if (transaction == null || transaction.getReferenceUnique() == null) {
            throw new IllegalArgumentException("Transaction comptable invalide");
        }
        bootstrapSiNecessaire();
        return ecritureComptableRepository.findBySourceTypeAndSourceReference("TRANSACTION", transaction.getReferenceUnique())
                .orElseGet(() -> comptabiliserNouvelleTransaction(transaction));
    }

    @Transactional(readOnly = true)
    public void verifierTransactionComptable(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction comptable invalide");
        }
        bootstrapSiNecessaire();
        String operationCode = transaction.getCodeOperationMetier() == null || transaction.getCodeOperationMetier().isBlank()
                ? transaction.getTypeTransaction().getCodeTypeTransaction()
                : transaction.getCodeOperationMetier().trim().toUpperCase();
        SchemaComptable schema = chargerSchema(operationCode);
        List<ManualLine> lines = construireLignesTransaction(transaction, schema, operationCode);
        validerPieceEquilibree(lines);
    }

    @Transactional
    public EcritureComptable comptabiliserMouvementTresorerie(
            String operationCode,
            String sourceReference,
            String libelle,
            BigDecimal montant,
            String referenceDebit,
            String libelleDebit,
            String referenceCredit,
            String libelleCredit
    ) {
        bootstrapSiNecessaire();
        SchemaComptable schema = chargerSchema(operationCode);
        List<ManualLine> lines = List.of(
                new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, referenceDebit, libelleDebit),
                new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, referenceCredit, libelleCredit)
        );
        return enregistrerPiece("TRES-" + sourceReference, schema.getJournalCode(), LocalDate.now(), LocalDate.now(), libelle, "TRESORERIE", sourceReference, lines);
    }

    @Transactional
    public EcritureComptable comptabiliserProvisionCredit(ProvisionCredit provisionCredit) {
        bootstrapSiNecessaire();
        if (provisionCredit.getReferencePieceComptable() != null) {
            return ecritureComptableRepository.findBySourceTypeAndSourceReference("PROVISION", provisionCredit.getReferencePieceComptable())
                    .orElseThrow(() -> new EntityNotFoundException("Piece de provision introuvable"));
        }
        SchemaComptable schema = chargerSchema("PROVISION_CREDIT");
        String sourceReference = "PROV-" + provisionCredit.getIdProvisionCredit();
        List<ManualLine> lines = List.of(
                new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, provisionCredit.getMontantProvision(), provisionCredit.getCredit().getReferenceCredit(), "Charge provision credit"),
                new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, provisionCredit.getMontantProvision(), provisionCredit.getCredit().getReferenceCredit(), "Provision credit")
        );
        EcritureComptable ecriture = enregistrerPiece("PROV-" + provisionCredit.getIdProvisionCredit(), schema.getJournalCode(), provisionCredit.getDateCalcul(), provisionCredit.getDateCalcul(), "Provision credit " + provisionCredit.getCredit().getReferenceCredit(), "PROVISION", sourceReference, lines);
        provisionCredit.setReferencePieceComptable(ecriture.getReferencePiece());
        provisionCredit.setStatut("COMPTABILISEE");
        provisionCreditRepository.save(provisionCredit);
        return ecriture;
    }

    @Transactional(readOnly = true)
    public List<ClasseComptable> listerClasses() {
        return classeComptableRepository.findAll().stream()
                .sorted(Comparator.comparing(ClasseComptable::getOrdreAffichage).thenComparing(ClasseComptable::getCodeClasse))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompteComptable> listerComptes() {
        if (authenticatedUserService.hasGlobalScope()) {
            return compteComptableRepository.findAll().stream()
                    .sorted(Comparator.comparing(CompteComptable::getNumeroCompte))
                    .toList();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        return compteComptableRepository.findByAgence_IdAgenceOrAgenceIsNull(idAgence).stream()
                .sorted(Comparator.comparing(CompteComptable::getNumeroCompte))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JournalComptable> listerJournaux() {
        return journalComptableRepository.findAll().stream()
                .sorted(Comparator.comparing(JournalComptable::getCodeJournal))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SchemaComptable> listerSchemas() {
        return schemaComptableRepository.findAll().stream()
                .sorted(Comparator.comparing(SchemaComptable::getCodeOperation))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EcritureComptable> listerEcritures(LocalDate dateDebut, LocalDate dateFin, String codeJournal) {
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(1) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        return ecritureComptableRepository.findByDateComptableBetween(debut, fin).stream()
                .filter(ecriture -> codeJournal == null
                        || codeJournal.isBlank()
                        || (ecriture.getJournalComptable() != null
                        && codeJournal.equalsIgnoreCase(ecriture.getJournalComptable().getCodeJournal())))
                .sorted(Comparator.comparing(EcritureComptable::getDateComptable).reversed()
                        .thenComparing(EcritureComptable::getReferencePiece))
                .toList();
    }

    @Transactional(readOnly = true)
    public SchemaTestResponseDTO testerSchemaComptable(TesterSchemaComptableRequestDTO payload) {
        bootstrapSiNecessaire();
        SchemaComptable schema = chargerSchema(payload.getCodeOperation());
        BigDecimal montant = payload.getMontant();
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de test doit etre strictement positif");
        }
        BigDecimal frais = payload.getFrais() != null ? payload.getFrais() : BigDecimal.ZERO;
        if (frais.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Les frais de test ne peuvent pas etre negatifs");
        }

        chargerCompteComptable(schema.getCompteDebit());
        chargerCompteComptable(schema.getCompteCredit());
        if (frais.compareTo(BigDecimal.ZERO) > 0 && (schema.getCompteFrais() == null || schema.getCompteFrais().isBlank())) {
            throw new IllegalStateException("Le schema ne definit pas de compte de frais pour un test avec frais");
        }
        if (frais.compareTo(BigDecimal.ZERO) > 0) {
            chargerCompteComptable(schema.getCompteFrais());
        }

        List<ManualLine> lines = new ArrayList<>();
        if (frais.compareTo(BigDecimal.ZERO) > 0) {
            lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant.add(frais), payload.getReferenceDebit(), payload.getLibelleDebit()));
            lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, payload.getReferenceCredit(), payload.getLibelleCredit()));
            lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, payload.getReferenceFrais(), payload.getLibelleFrais()));
        } else {
            lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, payload.getReferenceDebit(), payload.getLibelleDebit()));
            lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, payload.getReferenceCredit(), payload.getLibelleCredit()));
        }
        validerPieceEquilibree(lines);

        BigDecimal totalDebit = lines.stream()
                .filter(line -> line.sens() == SensEcriture.DEBIT)
                .map(ManualLine::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = lines.stream()
                .filter(line -> line.sens() == SensEcriture.CREDIT)
                .map(ManualLine::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<LigneSchemaTestDTO> lignes = lines.stream().map(line -> {
            LigneSchemaTestDTO item = new LigneSchemaTestDTO();
            item.setNumeroCompte(line.numeroCompte());
            item.setSens(line.sens().name());
            item.setMontant(line.montant());
            item.setReferenceAuxiliaire(line.referenceAuxiliaire());
            item.setLibelleAuxiliaire(line.libelleAuxiliaire());
            return item;
        }).toList();

        SchemaTestResponseDTO response = new SchemaTestResponseDTO();
        response.setCodeOperation(schema.getCodeOperation());
        response.setJournalCode(schema.getJournalCode());
        response.setMontantOperation(montant);
        response.setFrais(frais);
        response.setTotalDebit(totalDebit);
        response.setTotalCredit(totalCredit);
        response.setEquilibree(totalDebit.compareTo(totalCredit) == 0);
        response.setLignes(lignes);
        return response;
    }

    @Transactional(readOnly = true)
    public ControlesComptablesResponseDTO controlesComptables(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(1) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        List<EcritureComptable> ecritures = ecritureComptableRepository.findByDateComptableBetween(debut, fin);
        List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository.findByEcritureComptable_DateComptableBetween(debut, fin);

        Map<Long, BigDecimal> debitParEcriture = new LinkedHashMap<>();
        Map<Long, BigDecimal> creditParEcriture = new LinkedHashMap<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (LigneEcritureComptable ligne : lignes) {
            Long idEcriture = ligne.getEcritureComptable().getIdEcritureComptable();
            if ("DEBIT".equalsIgnoreCase(ligne.getSens())) {
                debitParEcriture.put(idEcriture, debitParEcriture.getOrDefault(idEcriture, BigDecimal.ZERO).add(ligne.getMontant()));
                totalDebit = totalDebit.add(ligne.getMontant());
            } else {
                creditParEcriture.put(idEcriture, creditParEcriture.getOrDefault(idEcriture, BigDecimal.ZERO).add(ligne.getMontant()));
                totalCredit = totalCredit.add(ligne.getMontant());
            }
        }

        int ecrituresSansLignes = 0;
        List<EcritureDesequilibreeDTO> ecrituresDesequilibrees = new ArrayList<>();
        for (EcritureComptable ecriture : ecritures) {
            Long idEcriture = ecriture.getIdEcritureComptable();
            BigDecimal debit = debitParEcriture.getOrDefault(idEcriture, BigDecimal.ZERO);
            BigDecimal credit = creditParEcriture.getOrDefault(idEcriture, BigDecimal.ZERO);
            if (debit.compareTo(BigDecimal.ZERO) == 0 && credit.compareTo(BigDecimal.ZERO) == 0) {
                ecrituresSansLignes++;
                continue;
            }
            if (debit.compareTo(credit) != 0) {
                EcritureDesequilibreeDTO anomalie = new EcritureDesequilibreeDTO();
                anomalie.setIdEcritureComptable(idEcriture);
                anomalie.setReferencePiece(ecriture.getReferencePiece());
                anomalie.setDateComptable(ecriture.getDateComptable());
                anomalie.setDebit(debit);
                anomalie.setCredit(credit);
                anomalie.setEcart(debit.subtract(credit));
                ecrituresDesequilibrees.add(anomalie);
            }
        }

        ControlesComptablesResponseDTO response = new ControlesComptablesResponseDTO();
        response.setDateDebut(debut);
        response.setDateFin(fin);
        response.setTotalEcritures(ecritures.size());
        response.setTotalLignes(lignes.size());
        response.setTotalDebit(totalDebit);
        response.setTotalCredit(totalCredit);
        response.setEquilibreGlobal(totalDebit.compareTo(totalCredit) == 0);
        response.setEcrituresSansLignes(ecrituresSansLignes);
        response.setEcrituresDesequilibrees(ecrituresDesequilibrees);
        return response;
    }

    @Transactional(readOnly = true)
    public List<LigneGrandLivreDTO> consulterGrandLivre(String numeroCompte, LocalDate dateDebut, LocalDate dateFin) {
        bootstrapSiNecessaire();
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(1) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository
                .findByCompteComptable_NumeroCompteAndEcritureComptable_DateComptableBetweenOrderByEcritureComptable_DateComptableAsc(numeroCompte, debut, fin);
        BigDecimal solde = BigDecimal.ZERO;
        List<LigneGrandLivreDTO> response = new ArrayList<>();
        for (LigneEcritureComptable ligne : lignes) {
            BigDecimal variation = "DEBIT".equalsIgnoreCase(ligne.getSens()) ? ligne.getMontant() : ligne.getMontant().negate();
            solde = solde.add(variation);
            LigneGrandLivreDTO item = new LigneGrandLivreDTO();
            item.setDateComptable(ligne.getEcritureComptable().getDateComptable());
            item.setReferencePiece(ligne.getEcritureComptable().getReferencePiece());
            item.setLibelle(ligne.getEcritureComptable().getLibelle());
            item.setSens(ligne.getSens());
            item.setMontant(ligne.getMontant());
            item.setSolde(solde);
            item.setSourceType(ligne.getEcritureComptable().getSourceType());
            item.setSourceReference(ligne.getEcritureComptable().getSourceReference());
            response.add(item);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<BalanceLineDTO> consulterBalance(LocalDate dateDebut, LocalDate dateFin) {
        bootstrapSiNecessaire();
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(1) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        Map<String, BalanceLineDTO> agregats = new LinkedHashMap<>();
        for (LigneEcritureComptable ligne : ligneEcritureComptableRepository.findByEcritureComptable_DateComptableBetween(debut, fin)) {
            String numeroCompte = ligne.getCompteComptable().getNumeroCompte();
            BalanceLineDTO agregat = agregats.computeIfAbsent(numeroCompte, key -> {
                BalanceLineDTO dto = new BalanceLineDTO();
                dto.setNumeroCompte(key);
                dto.setIntitule(ligne.getCompteComptable().getIntitule());
                dto.setDebit(BigDecimal.ZERO);
                dto.setCredit(BigDecimal.ZERO);
                dto.setSolde(BigDecimal.ZERO);
                return dto;
            });
            BigDecimal debit = agregat.getDebit();
            BigDecimal credit = agregat.getCredit();
            if ("DEBIT".equalsIgnoreCase(ligne.getSens())) {
                debit = debit.add(ligne.getMontant());
            } else {
                credit = credit.add(ligne.getMontant());
            }
            agregat.setDebit(debit);
            agregat.setCredit(credit);
            agregat.setSolde(debit.subtract(credit));
        }
        return new ArrayList<>(agregats.values());
    }

    @Transactional
    public ClotureComptable cloturerPeriode(ClotureComptableRequestDTO payload) {
        LocalDate dateDebut = payload.getDateDebut() != null ? payload.getDateDebut() : LocalDate.now();
        LocalDate dateFin = payload.getDateFin() != null ? payload.getDateFin() : LocalDate.now();
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit etre posterieure ou egale a la date de debut");
        }
        if (transactionRepository.countByStatutOperation(StatutOperation.EN_ATTENTE) > 0) {
            throw new IllegalStateException("Impossible de cloturer tant que des transactions sont encore en attente");
        }
        ControlesComptablesResponseDTO controles = controlesComptables(dateDebut, dateFin);
        if (!Boolean.TRUE.equals(controles.getEquilibreGlobal())) {
            throw new IllegalStateException("Impossible de cloturer une periode comptable desequilibree");
        }

        List<?> agiosPreleves = agioServiceProvider.getIfAvailable(() -> null) == null
                ? List.of()
                : agioServiceProvider.getObject().executerPrelevementsEnAttente(
                        authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                );
        List<ProvisionCredit> provisions = creditExtensionServiceProvider.getIfAvailable(() -> null) == null
                ? List.of()
                : creditExtensionServiceProvider.getObject().calculerProvisions(new CalculerProvisionsRequestDTO(dateFin));
        List<?> impayes = creditExtensionServiceProvider.getIfAvailable(() -> null) == null
                ? List.of()
                : creditExtensionServiceProvider.getObject().detecterImpayes(new DetecterImpayesRequestDTO(dateFin));
        Map<String, Object> reglementExterne = paiementExterneServiceProvider.getIfAvailable(() -> null) == null
                ? Map.of("mobileMoneyReglees", 0, "ordresCompenses", 0)
                : paiementExterneServiceProvider.getObject().traiterReglementFinDeJournee(dateFin.plusDays(1).atStartOfDay().minusSeconds(1));

        ClotureComptable cloture = new ClotureComptable();
        cloture.setTypeCloture(payload.getTypeCloture() != null ? payload.getTypeCloture() : "JOURNALIERE");
        cloture.setDateDebut(dateDebut);
        cloture.setDateFin(dateFin);
        cloture.setStatut(payload.getStatut() != null ? payload.getStatut() : "TERMINEE");
        String commentaireTechnique = "ecritures=" + ecritureComptableRepository.countByDateComptableBetween(dateDebut, dateFin)
                + ", impayes=" + impayes.size()
                + ", provisions=" + provisions.size()
                + ", agiosPreleves=" + agiosPreleves.size()
                + ", reglementsMm=" + reglementExterne.getOrDefault("mobileMoneyReglees", 0)
                + ", ordresCompenses=" + reglementExterne.getOrDefault("ordresCompenses", 0);
        String commentaireMetier = payload.getCommentaire();
        cloture.setCommentaire(commentaireMetier == null ? commentaireTechnique : commentaireMetier + " | " + commentaireTechnique);
        cloture.setTotalEcritures((int) ecritureComptableRepository.countByDateComptableBetween(dateDebut, dateFin));
        return clotureComptableRepository.save(cloture);
    }

    @Transactional(readOnly = true)
    public List<ClotureComptable> listerClotures(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(3) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        return clotureComptableRepository.findByDateFinBetweenOrderByDateFinDesc(debut, fin);
    }

    private EcritureComptable comptabiliserNouvelleTransaction(Transaction transaction) {
        if (transaction.getStatutOperation() != StatutOperation.EXECUTEE) {
            throw new IllegalStateException("Seules les transactions executees peuvent etre comptabilisees");
        }
        String operationCode = transaction.getCodeOperationMetier() == null || transaction.getCodeOperationMetier().isBlank()
                ? transaction.getTypeTransaction().getCodeTypeTransaction()
                : transaction.getCodeOperationMetier().trim().toUpperCase();
        SchemaComptable schema = chargerSchema(operationCode);
        LocalDate dateComptable = transaction.getDateExecution() == null ? LocalDate.now() : transaction.getDateExecution().toLocalDate();
        List<ManualLine> lines = construireLignesTransaction(transaction, schema, operationCode);
        return enregistrerPiece("PC-" + transaction.getReferenceUnique(), schema.getJournalCode(), dateComptable, dateComptable, "Comptabilisation " + operationCode + " " + transaction.getReferenceUnique(), "TRANSACTION", transaction.getReferenceUnique(), lines);
    }

    private List<ManualLine> construireLignesTransaction(Transaction transaction, SchemaComptable schema, String operationCode) {
        BigDecimal montant = transaction.getMontantGlobal();
        BigDecimal frais = safe(transaction.getFrais());
        BigDecimal net = montant.subtract(frais);
        return switch (operationCode) {
            case "DEPOT_CASH" -> {
                List<ManualLine> lines = new ArrayList<>();
                lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, referenceCaisse(transaction), libelleCaisse(transaction)));
                lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, net, referenceCompte(transaction.getCompteDestination()), libelleCompte(transaction.getCompteDestination())));
                if (frais.compareTo(BigDecimal.ZERO) > 0) {
                    lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, referenceCompte(transaction.getCompteDestination()), libelleCompte(transaction.getCompteDestination())));
                }
                yield lines;
            }
            case "RETRAIT_CASH" -> lignesDebitClientCreditCanal(transaction, schema, montant, frais, referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource()), referenceCaisse(transaction), libelleCaisse(transaction));
            case "VIREMENT_INTERNE" -> {
                List<ManualLine> lines = new ArrayList<>();
                lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant.add(frais), referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource())));
                lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, referenceCompte(transaction.getCompteDestination()), libelleCompte(transaction.getCompteDestination())));
                if (frais.compareTo(BigDecimal.ZERO) > 0) {
                    lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource())));
                }
                yield lines;
            }
            case "CREDIT_DEBLOCAGE" -> {
                List<ManualLine> lines = new ArrayList<>();
                lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, transaction.getReferenceUnique(), "Encours " + transaction.getReferenceUnique()));
                lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, net, referenceCompte(transaction.getCompteDestination()), libelleCompte(transaction.getCompteDestination())));
                if (frais.compareTo(BigDecimal.ZERO) > 0) {
                    lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, transaction.getReferenceUnique(), "Frais de deblocage"));
                }
                yield lines;
            }
            case "DAT_SOUSCRIPTION" -> List.of(
                    new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource())),
                    new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, transaction.getReferenceUnique(), "Depot a terme")
            );
            case "AGIO_PRELEVEMENT" -> List.of(
                    new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource())),
                    new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, referenceCompte(transaction.getCompteSource()), "Produit d'agio")
            );
            case "MOBILEMONEY_CASHOUT", "PAIEMENT_FACTURE", "RECHARGE_TELEPHONIQUE", "VIREMENT_RTGS", "COMPENSATION_SICA", "MONETIQUE_REGLEMENT" ->
                    lignesDebitClientCreditCanal(transaction, schema, montant, frais, referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource()), transaction.getReferenceUnique(), operationCode);
            case "MOBILEMONEY_CASHIN", "CHEQUE_ENCAISSEMENT" -> {
                List<ManualLine> lines = new ArrayList<>();
                lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, transaction.getReferenceUnique(), operationCode));
                lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, net, referenceCompte(transaction.getCompteDestination()), libelleCompte(transaction.getCompteDestination())));
                if (frais.compareTo(BigDecimal.ZERO) > 0) {
                    lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, transaction.getReferenceUnique(), "Commission externe"));
                }
                yield lines;
            }
            case "CREDIT_REMBOURSEMENT" -> construireLignesRemboursement(transaction, schema);
            default -> throw new IllegalStateException("Aucun schema de construction des lignes n'est defini pour " + operationCode);
        };
    }

    private List<ManualLine> construireLignesRemboursement(Transaction transaction, SchemaComptable schema) {
        RemboursementCredit remboursement = remboursementCreditRepository.findByReferenceTransaction(transaction.getReferenceUnique())
                .orElseThrow(() -> new IllegalStateException("Remboursement credit introuvable pour la transaction " + transaction.getReferenceUnique()));
        List<ManualLine> lines = new ArrayList<>();
        lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, remboursement.getMontant(), referenceCompte(transaction.getCompteSource()), libelleCompte(transaction.getCompteSource())));
        if (remboursement.getCapitalPaye().compareTo(BigDecimal.ZERO) > 0) {
            lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, remboursement.getCapitalPaye(), remboursement.getCredit().getReferenceCredit(), "Remboursement principal"));
        }
        BigDecimal extras = remboursement.getInteretPaye().add(remboursement.getAssurancePayee());
        if (extras.compareTo(BigDecimal.ZERO) > 0) {
            lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, extras, remboursement.getCredit().getReferenceCredit(), "Interets et assurance"));
        }
        return lines;
    }

    private List<ManualLine> lignesDebitClientCreditCanal(
            Transaction transaction,
            SchemaComptable schema,
            BigDecimal montant,
            BigDecimal frais,
            String referenceDebit,
            String libelleDebit,
            String referenceCredit,
            String libelleCredit
    ) {
        List<ManualLine> lines = new ArrayList<>();
        lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant.add(frais), referenceDebit, libelleDebit));
        lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, referenceCredit, libelleCredit));
        if (frais.compareTo(BigDecimal.ZERO) > 0) {
            lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, referenceDebit, "Commission"));
        }
        return lines;
    }

    private EcritureComptable enregistrerPiece(
            String referencePiece,
            String codeJournal,
            LocalDate dateComptable,
            LocalDate dateValeur,
            String libelle,
            String sourceType,
            String sourceReference,
            List<ManualLine> manualLines
    ) {
        validerPieceEquilibree(manualLines);
        JournalComptable journal = journalComptableRepository.findByCodeJournal(codeJournal)
                .orElseThrow(() -> new EntityNotFoundException("Journal comptable introuvable: " + codeJournal));

        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setReferencePiece(referencePiece);
        ecriture.setJournalComptable(journal);
        ecriture.setDateComptable(dateComptable);
        ecriture.setDateValeur(dateValeur);
        ecriture.setLibelle(libelle);
        ecriture.setSourceType(sourceType);
        ecriture.setSourceReference(sourceReference);
        ecriture.setStatut("COMPTABILISEE");
        EcritureComptable saved = ecritureComptableRepository.save(ecriture);

        for (ManualLine manualLine : manualLines) {
            LigneEcritureComptable ligne = new LigneEcritureComptable();
            ligne.setEcritureComptable(saved);
            ligne.setCompteComptable(chargerCompteComptable(manualLine.numeroCompte()));
            ligne.setSens(manualLine.sens().name());
            ligne.setMontant(manualLine.montant());
            ligne.setReferenceAuxiliaire(manualLine.referenceAuxiliaire());
            ligne.setLibelleAuxiliaire(manualLine.libelleAuxiliaire());
            ligneEcritureComptableRepository.save(ligne);
        }
        return saved;
    }

    private void validerPieceEquilibree(List<ManualLine> manualLines) {
        if (manualLines == null || manualLines.isEmpty()) {
            throw new IllegalArgumentException("Une piece comptable doit contenir des lignes");
        }
        List<BalanceLine> balanceLines = manualLines.stream()
                .map(ml -> new BalanceLine(
                        ml.numeroCompte(),
                        ml.sens(),
                        ml.montant(),
                        ml.referenceAuxiliaire(),
                        ml.libelleAuxiliaire()
                ))
                .toList();
        doubleEntryService.validerPieceEquilibree(balanceLines);
    }

    private SchemaComptable chargerSchema(String operationCode) {
        return schemaComptableRepository.findByCodeOperation(operationCode)
                .orElseThrow(() -> new EntityNotFoundException("Schema comptable introuvable pour " + operationCode));
    }

    private CompteComptable chargerCompteComptable(String numeroCompte) {
        return compteComptableRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte comptable introuvable: " + numeroCompte));
    }

    private void bootstrapSiNecessaire() {
        ClasseComptable classe1 = ensureClasse("1", "Ressources stables", 10);
        ClasseComptable classe2 = ensureClasse("2", "Operations clientele", 20);
        ClasseComptable classe5 = ensureClasse("5", "Tresorerie", 50);
        ClasseComptable classe6 = ensureClasse("6", "Charges", 60);
        ClasseComptable classe7 = ensureClasse("7", "Produits", 70);

        ensureCompte("251000", "Depots clientele", classe2, false);
        ensureCompte("261000", "Encours de credits", classe2, false);
        ensureCompte("271000", "Depots a terme clientele", classe2, false);
        ensureCompte("281000", "Provisions sur credits", classe2, false);
        ensureCompte("571000", "Coffre et encaisse centrale", classe5, true);
        ensureCompte("572000", "Caisses et guichets", classe5, true);
        ensureCompte("273000", "Comptes de compensation et reglement", classe2, true);
        ensureCompte("274000", "Comptes techniques mobile money", classe2, true);
        ensureCompte("681000", "Dotations aux provisions credit", classe6, true);
        ensureCompte("701000", "Commissions sur operations", classe7, true);
        ensureCompte("701100", "Produits d'agios", classe7, true);
        ensureCompte("701200", "Produits sur credit", classe7, true);
        ensureCompte("701300", "Produits mobile money et paiements", classe7, true);

        ensureJournal("CAI", "Journal de caisse", "CAISSE");
        ensureJournal("CRE", "Journal de credit", "CREDIT");
        ensureJournal("EPA", "Journal epargne", "EPARGNE");
        ensureJournal("TRS", "Journal de tresorerie", "TRESORERIE");
        ensureJournal("PAY", "Journal paiements externes", "PAIEMENT");
        ensureJournal("OD", "Operations diverses", "DIVERS");

        ensureSchema("DEPOT_CASH", "571000", "251000", "701000", "CAI");
        ensureSchema("RETRAIT_CASH", "251000", "571000", "701000", "CAI");
        ensureSchema("VIREMENT_INTERNE", "251000", "251000", "701000", "OD");
        ensureSchema("CREDIT_DEBLOCAGE", "261000", "251000", "701200", "CRE");
        ensureSchema("CREDIT_REMBOURSEMENT", "251000", "261000", "701200", "CRE");
        ensureSchema("DAT_SOUSCRIPTION", "251000", "271000", null, "EPA");
        ensureSchema("AGIO_PRELEVEMENT", "251000", "701100", null, "OD");
        ensureSchema("APPROVISIONNEMENT_CAISSE", "572000", "571000", null, "TRS");
        ensureSchema("DELESTAGE_CAISSE", "571000", "572000", null, "TRS");
        ensureSchema("MOBILEMONEY_CASHIN", "274000", "251000", "701300", "PAY");
        ensureSchema("MOBILEMONEY_CASHOUT", "251000", "274000", "701300", "PAY");
        ensureSchema("PAIEMENT_FACTURE", "251000", "273000", "701300", "PAY");
        ensureSchema("RECHARGE_TELEPHONIQUE", "251000", "273000", "701300", "PAY");
        ensureSchema("COMPENSATION_SICA", "251000", "273000", "701300", "PAY");
        ensureSchema("VIREMENT_RTGS", "251000", "273000", "701300", "PAY");
        ensureSchema("CHEQUE_ENCAISSEMENT", "273000", "251000", "701300", "PAY");
        ensureSchema("MONETIQUE_REGLEMENT", "251000", "273000", "701300", "PAY");
        ensureSchema("PROVISION_CREDIT", "681000", "281000", null, "OD");

        ensureCompte("579000", "Caisse - comptes de passage", classe5, true);
        ensureCompte("778000", "Produits exceptionnels", classe7, true);
        ensureCompte("678000", "Charges exceptionnelles", classe6, true);

        ensureSchema("OUVERTURE_CAISSE", "571000", "579000", null, "TRS");
        ensureSchema("FERMETURE_CAISSE", "579000", "571000", null, "TRS");
        ensureSchema("ECART_CAISSE_EXCEDENT", "571000", "778000", null, "OD");
        ensureSchema("ECART_CAISSE_DEFICIT", "678000", "571000", null, "OD");
        ensureSchema("DEPOT_OUVERTURE", "571000", "251000", null, "CAI");
    }

    private ClasseComptable ensureClasse(String codeClasse, String libelle, int ordre) {
        return classeComptableRepository.findByCodeClasse(codeClasse).orElseGet(() -> {
            ClasseComptable classe = new ClasseComptable();
            classe.setCodeClasse(codeClasse);
            classe.setLibelle(libelle);
            classe.setOrdreAffichage(ordre);
            return classeComptableRepository.save(classe);
        });
    }

    private void ensureCompte(String numeroCompte, String intitule, ClasseComptable classe, boolean interne) {
        compteComptableRepository.findByNumeroCompte(numeroCompte).orElseGet(() -> {
            CompteComptable compte = new CompteComptable();
            compte.setNumeroCompte(numeroCompte);
            compte.setIntitule(intitule);
            compte.setTypeSolde("MIXTE");
            compte.setCompteInterne(interne);
            compte.setClasseComptable(classe);
            return compteComptableRepository.save(compte);
        });
    }

    private void ensureJournal(String codeJournal, String libelle, String type) {
        journalComptableRepository.findByCodeJournal(codeJournal).orElseGet(() -> {
            JournalComptable journal = new JournalComptable();
            journal.setCodeJournal(codeJournal);
            journal.setLibelle(libelle);
            journal.setTypeJournal(type);
            journal.setActif(Boolean.TRUE);
            return journalComptableRepository.save(journal);
        });
    }

    private void ensureSchema(String codeOperation, String compteDebit, String compteCredit, String compteFrais, String journalCode) {
        schemaComptableRepository.findByCodeOperation(codeOperation).orElseGet(() -> {
            SchemaComptable schema = new SchemaComptable();
            schema.setCodeOperation(codeOperation);
            schema.setCompteDebit(compteDebit);
            schema.setCompteCredit(compteCredit);
            schema.setCompteFrais(compteFrais);
            schema.setJournalCode(journalCode);
            schema.setActif(Boolean.TRUE);
            return schemaComptableRepository.save(schema);
        });
    }

    private BigDecimal safe(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String referenceCompte(Compte compte) {
        return compte == null ? null : compte.getNumCompte();
    }

    private String libelleCompte(Compte compte) {
        if (compte == null || compte.getClient() == null) {
            return null;
        }
        return (Objects.toString(compte.getClient().getNom(), "") + " " + Objects.toString(compte.getClient().getPrenom(), "")).trim();
    }

    private String referenceCaisse(Transaction transaction) {
        if (transaction.getSessionCaisse() != null && transaction.getSessionCaisse().getCaisse() != null) {
            return transaction.getSessionCaisse().getCaisse().getCodeCaisse();
        }
        return transaction.getAgenceOperation() == null ? null : transaction.getAgenceOperation().getCodeAgence();
    }

    private String libelleCaisse(Transaction transaction) {
        if (transaction.getSessionCaisse() != null && transaction.getSessionCaisse().getCaisse() != null) {
            return transaction.getSessionCaisse().getCaisse().getLibelle();
        }
        return transaction.getAgenceOperation() == null ? null : transaction.getAgenceOperation().getNomAgence();
    }

    private record ManualLine(
            String numeroCompte,
            SensEcriture sens,
            BigDecimal montant,
            String referenceAuxiliaire,
            String libelleAuxiliaire
    ) {
    }
}
