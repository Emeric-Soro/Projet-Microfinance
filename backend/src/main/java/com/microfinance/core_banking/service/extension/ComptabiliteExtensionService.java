package com.microfinance.core_banking.service.extension;

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
    public Map<String, Object> bootstrapReferentiel() {
        bootstrapSiNecessaire();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("classes", classeComptableRepository.count());
        response.put("comptes", compteComptableRepository.count());
        response.put("journaux", journalComptableRepository.count());
        response.put("schemas", schemaComptableRepository.count());
        return response;
    }

    @Transactional
    public ClasseComptable creerClasse(Map<String, Object> payload) {
        ClasseComptable classe = new ClasseComptable();
        classe.setCodeClasse(required(payload, "codeClasse"));
        classe.setLibelle(required(payload, "libelle"));
        classe.setOrdreAffichage(integerOrDefault(payload, "ordreAffichage", 0));
        return classeComptableRepository.save(classe);
    }

    @Transactional
    public CompteComptable creerCompte(Map<String, Object> payload) {
        ClasseComptable classe = classeComptableRepository.findByCodeClasse(required(payload, "codeClasse"))
                .orElseThrow(() -> new EntityNotFoundException("Classe comptable introuvable"));
        CompteComptable compte = new CompteComptable();
        compte.setNumeroCompte(required(payload, "numeroCompte"));
        compte.setIntitule(required(payload, "intitule"));
        compte.setTypeSolde(defaulted(payload, "typeSolde", "MIXTE"));
        compte.setCompteInterne(booleanValue(payload, "compteInterne", false));
        compte.setClasseComptable(classe);
        if (payload.get("idAgence") != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(payload.get("idAgence").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            compte.setAgence(agence);
        }
        return compteComptableRepository.save(compte);
    }

    @Transactional
    public JournalComptable creerJournal(Map<String, Object> payload) {
        JournalComptable journal = new JournalComptable();
        journal.setCodeJournal(required(payload, "codeJournal"));
        journal.setLibelle(required(payload, "libelle"));
        journal.setTypeJournal(required(payload, "typeJournal"));
        journal.setActif(booleanValue(payload, "actif", true));
        return journalComptableRepository.save(journal);
    }

    @Transactional
    public SchemaComptable creerSchema(Map<String, Object> payload) {
        SchemaComptable schema = new SchemaComptable();
        schema.setCodeOperation(required(payload, "codeOperation"));
        schema.setCompteDebit(required(payload, "compteDebit"));
        schema.setCompteCredit(required(payload, "compteCredit"));
        schema.setCompteFrais(optionalString(payload, "compteFrais"));
        schema.setJournalCode(defaulted(payload, "journalCode", "OD"));
        schema.setActif(booleanValue(payload, "actif", true));
        return schemaComptableRepository.save(schema);
    }

    @Transactional
    public EcritureComptable creerEcritureManuelle(Map<String, Object> payload) {
        bootstrapSiNecessaire();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lignes = (List<Map<String, Object>>) payload.get("lignes");
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Une ecriture manuelle doit contenir au moins une ligne");
        }
        List<ManualLine> manualLines = new ArrayList<>();
        for (Map<String, Object> ligne : lignes) {
            manualLines.add(new ManualLine(
                    required(ligne, "numeroCompte"),
                    SensEcriture.valueOf(required(ligne, "sens").toUpperCase()),
                    decimal(ligne, "montant"),
                    optionalString(ligne, "referenceAuxiliaire"),
                    optionalString(ligne, "libelleAuxiliaire")
            ));
        }
        return enregistrerPiece(
                defaulted(payload, "referencePiece", "MAN-" + System.currentTimeMillis()),
                defaulted(payload, "codeJournal", "OD"),
                payload.get("dateComptable") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateComptable").toString()),
                payload.get("dateValeur") == null ? null : LocalDate.parse(payload.get("dateValeur").toString()),
                required(payload, "libelle"),
                "MANUELLE",
                defaulted(payload, "referenceSource", defaulted(payload, "referencePiece", "MAN-" + System.currentTimeMillis())),
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
    public Map<String, Object> testerSchemaComptable(Map<String, Object> payload) {
        bootstrapSiNecessaire();
        SchemaComptable schema = chargerSchema(required(payload, "codeOperation"));
        BigDecimal montant = decimal(payload, "montant");
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de test doit etre strictement positif");
        }
        BigDecimal frais = payload.get("frais") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("frais").toString());
        if (frais.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Les frais de test ne peuvent pas etre negatifs");
        }

        // Validation des comptes references par le schema cible.
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
            lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant.add(frais), optionalString(payload, "referenceDebit"), optionalString(payload, "libelleDebit")));
            lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, optionalString(payload, "referenceCredit"), optionalString(payload, "libelleCredit")));
            lines.add(new ManualLine(schema.getCompteFrais(), SensEcriture.CREDIT, frais, optionalString(payload, "referenceFrais"), optionalString(payload, "libelleFrais")));
        } else {
            lines.add(new ManualLine(schema.getCompteDebit(), SensEcriture.DEBIT, montant, optionalString(payload, "referenceDebit"), optionalString(payload, "libelleDebit")));
            lines.add(new ManualLine(schema.getCompteCredit(), SensEcriture.CREDIT, montant, optionalString(payload, "referenceCredit"), optionalString(payload, "libelleCredit")));
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

        List<Map<String, Object>> lignes = lines.stream().map(line -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("numeroCompte", line.numeroCompte());
            item.put("sens", line.sens().name());
            item.put("montant", line.montant());
            item.put("referenceAuxiliaire", line.referenceAuxiliaire());
            item.put("libelleAuxiliaire", line.libelleAuxiliaire());
            return item;
        }).toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("codeOperation", schema.getCodeOperation());
        response.put("journalCode", schema.getJournalCode());
        response.put("montantOperation", montant);
        response.put("frais", frais);
        response.put("totalDebit", totalDebit);
        response.put("totalCredit", totalCredit);
        response.put("equilibree", totalDebit.compareTo(totalCredit) == 0);
        response.put("lignes", lignes);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> controlesComptables(LocalDate dateDebut, LocalDate dateFin) {
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
        List<Map<String, Object>> ecrituresDesequilibrees = new ArrayList<>();
        for (EcritureComptable ecriture : ecritures) {
            Long idEcriture = ecriture.getIdEcritureComptable();
            BigDecimal debit = debitParEcriture.getOrDefault(idEcriture, BigDecimal.ZERO);
            BigDecimal credit = creditParEcriture.getOrDefault(idEcriture, BigDecimal.ZERO);
            if (debit.compareTo(BigDecimal.ZERO) == 0 && credit.compareTo(BigDecimal.ZERO) == 0) {
                ecrituresSansLignes++;
                continue;
            }
            if (debit.compareTo(credit) != 0) {
                Map<String, Object> anomalie = new LinkedHashMap<>();
                anomalie.put("idEcritureComptable", idEcriture);
                anomalie.put("referencePiece", ecriture.getReferencePiece());
                anomalie.put("dateComptable", ecriture.getDateComptable());
                anomalie.put("debit", debit);
                anomalie.put("credit", credit);
                anomalie.put("ecart", debit.subtract(credit));
                ecrituresDesequilibrees.add(anomalie);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("dateDebut", debut);
        response.put("dateFin", fin);
        response.put("totalEcritures", ecritures.size());
        response.put("totalLignes", lignes.size());
        response.put("totalDebit", totalDebit);
        response.put("totalCredit", totalCredit);
        response.put("equilibreGlobal", totalDebit.compareTo(totalCredit) == 0);
        response.put("ecrituresSansLignes", ecrituresSansLignes);
        response.put("ecrituresDesequilibrees", ecrituresDesequilibrees);
        return response;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> consulterGrandLivre(String numeroCompte, LocalDate dateDebut, LocalDate dateFin) {
        bootstrapSiNecessaire();
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(1) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository
                .findByCompteComptable_NumeroCompteAndEcritureComptable_DateComptableBetweenOrderByEcritureComptable_DateComptableAsc(numeroCompte, debut, fin);
        BigDecimal solde = BigDecimal.ZERO;
        List<Map<String, Object>> response = new ArrayList<>();
        for (LigneEcritureComptable ligne : lignes) {
            BigDecimal variation = "DEBIT".equalsIgnoreCase(ligne.getSens()) ? ligne.getMontant() : ligne.getMontant().negate();
            solde = solde.add(variation);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("dateComptable", ligne.getEcritureComptable().getDateComptable());
            item.put("referencePiece", ligne.getEcritureComptable().getReferencePiece());
            item.put("libelle", ligne.getEcritureComptable().getLibelle());
            item.put("sens", ligne.getSens());
            item.put("montant", ligne.getMontant());
            item.put("solde", solde);
            item.put("sourceType", ligne.getEcritureComptable().getSourceType());
            item.put("sourceReference", ligne.getEcritureComptable().getSourceReference());
            response.add(item);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> consulterBalance(LocalDate dateDebut, LocalDate dateFin) {
        bootstrapSiNecessaire();
        LocalDate debut = dateDebut == null ? LocalDate.now().minusMonths(1) : dateDebut;
        LocalDate fin = dateFin == null ? LocalDate.now() : dateFin;
        Map<String, Map<String, Object>> agregats = new LinkedHashMap<>();
        for (LigneEcritureComptable ligne : ligneEcritureComptableRepository.findByEcritureComptable_DateComptableBetween(debut, fin)) {
            String numeroCompte = ligne.getCompteComptable().getNumeroCompte();
            Map<String, Object> agregat = agregats.computeIfAbsent(numeroCompte, key -> {
                Map<String, Object> initial = new LinkedHashMap<>();
                initial.put("numeroCompte", key);
                initial.put("intitule", ligne.getCompteComptable().getIntitule());
                initial.put("debit", BigDecimal.ZERO);
                initial.put("credit", BigDecimal.ZERO);
                initial.put("solde", BigDecimal.ZERO);
                return initial;
            });
            BigDecimal debit = (BigDecimal) agregat.get("debit");
            BigDecimal credit = (BigDecimal) agregat.get("credit");
            if ("DEBIT".equalsIgnoreCase(ligne.getSens())) {
                debit = debit.add(ligne.getMontant());
            } else {
                credit = credit.add(ligne.getMontant());
            }
            agregat.put("debit", debit);
            agregat.put("credit", credit);
            agregat.put("solde", debit.subtract(credit));
        }
        return new ArrayList<>(agregats.values());
    }

    @Transactional
    public ClotureComptable cloturerPeriode(Map<String, Object> payload) {
        LocalDate dateDebut = payload.get("dateDebut") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateDebut").toString());
        LocalDate dateFin = payload.get("dateFin") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateFin").toString());
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit etre posterieure ou egale a la date de debut");
        }
        if (transactionRepository.countByStatutOperation(StatutOperation.EN_ATTENTE) > 0) {
            throw new IllegalStateException("Impossible de cloturer tant que des transactions sont encore en attente");
        }
        Map<String, Object> controles = controlesComptables(dateDebut, dateFin);
        if (!Boolean.TRUE.equals(controles.get("equilibreGlobal"))) {
            throw new IllegalStateException("Impossible de cloturer une periode comptable desequilibree");
        }

        List<?> agiosPreleves = agioServiceProvider.getIfAvailable(() -> null) == null
                ? List.of()
                : agioServiceProvider.getObject().executerPrelevementsEnAttente(
                        authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                );
        List<ProvisionCredit> provisions = creditExtensionServiceProvider.getIfAvailable(() -> null) == null
                ? List.of()
                : creditExtensionServiceProvider.getObject().calculerProvisions(Map.of("dateCalcul", dateFin.toString()));
        List<?> impayes = creditExtensionServiceProvider.getIfAvailable(() -> null) == null
                ? List.of()
                : creditExtensionServiceProvider.getObject().detecterImpayes(Map.of("dateArrete", dateFin.toString()));
        Map<String, Object> reglementExterne = paiementExterneServiceProvider.getIfAvailable(() -> null) == null
                ? Map.of("mobileMoneyReglees", 0, "ordresCompenses", 0)
                : paiementExterneServiceProvider.getObject().traiterReglementFinDeJournee(dateFin.plusDays(1).atStartOfDay().minusSeconds(1));

        ClotureComptable cloture = new ClotureComptable();
        cloture.setTypeCloture(defaulted(payload, "typeCloture", "JOURNALIERE"));
        cloture.setDateDebut(dateDebut);
        cloture.setDateFin(dateFin);
        cloture.setStatut(defaulted(payload, "statut", "TERMINEE"));
        String commentaireTechnique = "ecritures=" + ecritureComptableRepository.countByDateComptableBetween(dateDebut, dateFin)
                + ", impayes=" + impayes.size()
                + ", provisions=" + provisions.size()
                + ", agiosPreleves=" + agiosPreleves.size()
                + ", reglementsMm=" + reglementExterne.getOrDefault("mobileMoneyReglees", 0)
                + ", ordresCompenses=" + reglementExterne.getOrDefault("ordresCompenses", 0);
        String commentaireMetier = optionalString(payload, "commentaire");
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
        // Map ManualLine to BalanceLine DTOs and delegate to the dedicated service
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

    private String optionalString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? null : value.toString().trim();
    }

    private BigDecimal decimal(Map<String, Object> payload, String key) {
        return new BigDecimal(required(payload, key));
    }

    private Integer integerOrDefault(Map<String, Object> payload, String key, Integer defaultValue) {
        return payload.get(key) == null ? defaultValue : Integer.valueOf(payload.get(key).toString());
    }

    private boolean booleanValue(Map<String, Object> payload, String key, boolean defaultValue) {
        return payload.get(key) == null ? defaultValue : Boolean.parseBoolean(payload.get(key).toString());
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
