package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.AffectationUtilisateurAgence;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.CommissionInterAgence;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.CompteComptable;
import com.microfinance.core_banking.entity.CompteLiaisonAgence;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.Employe;
import com.microfinance.core_banking.entity.Guichet;
import com.microfinance.core_banking.entity.MutationPersonnel;
import com.microfinance.core_banking.entity.OperationDeplacee;
import com.microfinance.core_banking.entity.ParametreAgence;
import com.microfinance.core_banking.entity.Region;
import com.microfinance.core_banking.entity.RapprochementInterAgence;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.AffectationUtilisateurAgenceRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.CommissionInterAgenceRepository;
import com.microfinance.core_banking.repository.extension.CompteComptableRepository;
import com.microfinance.core_banking.repository.extension.CompteLiaisonAgenceRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.EmployeRepository;
import com.microfinance.core_banking.repository.extension.GuichetRepository;
import com.microfinance.core_banking.repository.extension.MutationPersonnelRepository;
import com.microfinance.core_banking.repository.extension.OperationDeplaceeRepository;
import com.microfinance.core_banking.repository.extension.ParametreAgenceRepository;
import com.microfinance.core_banking.repository.extension.RegionRepository;
import com.microfinance.core_banking.repository.extension.RapprochementInterAgenceRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrganisationService {

    private final RegionRepository regionRepository;
    private final AgenceRepository agenceRepository;
    private final GuichetRepository guichetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AffectationUtilisateurAgenceRepository affectationRepository;
    private final ParametreAgenceRepository parametreAgenceRepository;
    private final EmployeRepository employeRepository;
    private final MutationPersonnelRepository mutationPersonnelRepository;
    private final CompteComptableRepository compteComptableRepository;
    private final CompteLiaisonAgenceRepository compteLiaisonAgenceRepository;
    private final OperationDeplaceeRepository operationDeplaceeRepository;
    private final CommissionInterAgenceRepository commissionInterAgenceRepository;
    private final RapprochementInterAgenceRepository rapprochementInterAgenceRepository;
    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final CompteRepository compteRepository;
    private final CreditRepository creditRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public OrganisationService(
            RegionRepository regionRepository,
            AgenceRepository agenceRepository,
            GuichetRepository guichetRepository,
            UtilisateurRepository utilisateurRepository,
            AffectationUtilisateurAgenceRepository affectationRepository,
            ParametreAgenceRepository parametreAgenceRepository,
            EmployeRepository employeRepository,
            MutationPersonnelRepository mutationPersonnelRepository,
            CompteComptableRepository compteComptableRepository,
            CompteLiaisonAgenceRepository compteLiaisonAgenceRepository,
            OperationDeplaceeRepository operationDeplaceeRepository,
            CommissionInterAgenceRepository commissionInterAgenceRepository,
            RapprochementInterAgenceRepository rapprochementInterAgenceRepository,
            TransactionRepository transactionRepository,
            ClientRepository clientRepository,
            CompteRepository compteRepository,
            CreditRepository creditRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.regionRepository = regionRepository;
        this.agenceRepository = agenceRepository;
        this.guichetRepository = guichetRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.affectationRepository = affectationRepository;
        this.parametreAgenceRepository = parametreAgenceRepository;
        this.employeRepository = employeRepository;
        this.mutationPersonnelRepository = mutationPersonnelRepository;
        this.compteComptableRepository = compteComptableRepository;
        this.compteLiaisonAgenceRepository = compteLiaisonAgenceRepository;
        this.operationDeplaceeRepository = operationDeplaceeRepository;
        this.commissionInterAgenceRepository = commissionInterAgenceRepository;
        this.rapprochementInterAgenceRepository = rapprochementInterAgenceRepository;
        this.transactionRepository = transactionRepository;
        this.clientRepository = clientRepository;
        this.compteRepository = compteRepository;
        this.creditRepository = creditRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public Region creerRegion(Map<String, Object> payload) {
        Region region = new Region();
        region.setCodeRegion(stringValue(payload, "codeRegion"));
        region.setNomRegion(stringValue(payload, "nomRegion"));
        region.setStatut(defaulted(payload, "statut", "ACTIVE"));
        return regionRepository.save(region);
    }

    @Transactional
    public Agence creerAgence(Map<String, Object> payload) {
        Agence agence = new Agence();
        agence.setCodeAgence(stringValue(payload, "codeAgence"));
        agence.setNomAgence(stringValue(payload, "nomAgence"));
        agence.setAdresse((String) payload.get("adresse"));
        agence.setTelephone((String) payload.get("telephone"));
        agence.setStatut(defaulted(payload, "statut", "ACTIVE"));
        if (payload.get("idRegion") != null) {
            agence.setRegion(regionRepository.findById(longValue(payload, "idRegion"))
                    .orElseThrow(() -> new EntityNotFoundException("Region introuvable")));
        }
        return agenceRepository.save(agence);
    }

    @Transactional
    public Guichet creerGuichet(Map<String, Object> payload) {
        Guichet guichet = new Guichet();
        guichet.setCodeGuichet(stringValue(payload, "codeGuichet"));
        guichet.setNomGuichet(stringValue(payload, "nomGuichet"));
        guichet.setStatut(defaulted(payload, "statut", "ACTIF"));
        guichet.setAgence(chargerAgence(longValue(payload, "idAgence")));
        return guichetRepository.save(guichet);
    }

    @Transactional
    public AffectationUtilisateurAgence affecterUtilisateur(Map<String, Object> payload) {
        Utilisateur utilisateur = utilisateurRepository.findById(longValue(payload, "idUtilisateur"))
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        Agence agence = chargerAgence(longValue(payload, "idAgence"));

        AffectationUtilisateurAgence affectation = new AffectationUtilisateurAgence();
        affectation.setUtilisateur(utilisateur);
        affectation.setAgence(agence);
        affectation.setRoleOperatoire((String) payload.get("roleOperatoire"));
        affectation.setDateDebut(payload.get("dateDebut") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateDebut").toString()));
        affectation.setDateFin(payload.get("dateFin") == null ? null : LocalDate.parse(payload.get("dateFin").toString()));
        affectation.setActif(payload.get("actif") == null || Boolean.parseBoolean(payload.get("actif").toString()));

        utilisateur.setAgenceActive(agence);
        utilisateurRepository.save(utilisateur);
        return affectationRepository.save(affectation);
    }

    @Transactional
    public ParametreAgence creerParametreAgence(Map<String, Object> payload) {
        Agence agence = chargerAgence(longValue(payload, "idAgence"));
        authenticatedUserService.assertAgencyAccess(agence.getIdAgence());

        ParametreAgence parametre = new ParametreAgence();
        parametre.setAgence(agence);
        parametre.setCodeParametre(required(payload, "codeParametre"));
        parametre.setValeurParametre(required(payload, "valeurParametre"));
        parametre.setTypeValeur(defaulted(payload, "typeValeur", "STRING"));
        parametre.setDescriptionParametre((String) payload.get("descriptionParametre"));
        parametre.setDateEffet(dateValue(payload, "dateEffet", LocalDate.now()));
        parametre.setDateFin(payload.get("dateFin") == null ? null : LocalDate.parse(payload.get("dateFin").toString()));
        if (parametre.getDateFin() != null && parametre.getDateFin().isBefore(parametre.getDateEffet())) {
            throw new IllegalArgumentException("La date de fin doit etre superieure ou egale a la date d'effet");
        }
        parametre.setActif(booleanValue(payload, "actif", true));
        parametre.setVersionParametre(payload.get("versionParametre") == null
                ? prochaineVersionParametre(agence.getIdAgence(), parametre.getCodeParametre())
                : Integer.valueOf(payload.get("versionParametre").toString()));
        return parametreAgenceRepository.save(parametre);
    }

    @Transactional
    public MutationPersonnel creerMutationPersonnel(Map<String, Object> payload) {
        Employe employe = employeRepository.findById(longValue(payload, "idEmploye"))
                .orElseThrow(() -> new EntityNotFoundException("Employe introuvable"));
        Agence agenceSource = employe.getAgence() != null
                ? employe.getAgence()
                : chargerAgence(longValue(payload, "idAgenceSource"));
        Agence agenceDestination = chargerAgence(longValue(payload, "idAgenceDestination"));
        if (agenceSource.getIdAgence().equals(agenceDestination.getIdAgence())) {
            throw new IllegalStateException("La mutation doit cibler une agence destination differente");
        }
        authenticatedUserService.assertAgencyAccess(agenceSource.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceDestination.getIdAgence());

        MutationPersonnel mutation = new MutationPersonnel();
        mutation.setEmploye(employe);
        mutation.setAgenceSource(agenceSource);
        mutation.setAgenceDestination(agenceDestination);
        mutation.setDateMutation(dateValue(payload, "dateMutation", LocalDate.now()));
        mutation.setMotif((String) payload.get("motif"));
        mutation.setStatut(defaulted(payload, "statut", "INITIEE"));
        return mutationPersonnelRepository.save(mutation);
    }

    @Transactional
    public MutationPersonnel validerMutationPersonnel(Long idMutation, Map<String, Object> payload) {
        MutationPersonnel mutation = mutationPersonnelRepository.findById(idMutation)
                .orElseThrow(() -> new EntityNotFoundException("Mutation introuvable"));
        authenticatedUserService.assertAgencyAccess(mutation.getAgenceSource().getIdAgence());
        authenticatedUserService.assertAgencyAccess(mutation.getAgenceDestination().getIdAgence());

        String decision = defaulted(payload, "decision", "APPROUVEE").toUpperCase();
        mutation.setDateValidation(LocalDateTime.now());
        mutation.setCommentaireValidation((String) payload.get("commentaireValidation"));
        if (payload.get("idValidateur") != null) {
            Utilisateur validateur = utilisateurRepository.findById(Long.valueOf(payload.get("idValidateur").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Validateur introuvable"));
            mutation.setValidateur(validateur);
        }
        if ("APPROUVEE".equals(decision) || "VALIDEE".equals(decision)) {
            mutation.setStatut("VALIDEE");
            Employe employe = mutation.getEmploye();
            employe.setAgence(mutation.getAgenceDestination());
            employeRepository.save(employe);
        } else if ("REJETEE".equals(decision) || "REJET".equals(decision)) {
            mutation.setStatut("REJETEE");
        } else {
            throw new IllegalArgumentException("Decision de mutation non supportee: " + decision);
        }
        return mutationPersonnelRepository.save(mutation);
    }

    @Transactional
    public CompteLiaisonAgence creerCompteLiaison(Map<String, Object> payload) {
        Agence agenceSource = chargerAgence(longValue(payload, "idAgenceSource"));
        Agence agenceDestination = chargerAgence(longValue(payload, "idAgenceDestination"));
        if (agenceSource.getIdAgence().equals(agenceDestination.getIdAgence())) {
            throw new IllegalStateException("Le compte de liaison doit relier deux agences distinctes");
        }
        authenticatedUserService.assertAgencyAccess(agenceSource.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceDestination.getIdAgence());
        CompteComptable compteComptable = compteComptableRepository.findById(longValue(payload, "idCompteComptable"))
                .orElseThrow(() -> new EntityNotFoundException("Compte comptable introuvable"));

        CompteLiaisonAgence compteLiaison = new CompteLiaisonAgence();
        compteLiaison.setAgenceSource(agenceSource);
        compteLiaison.setAgenceDestination(agenceDestination);
        compteLiaison.setCompteComptable(compteComptable);
        compteLiaison.setLibelle(defaulted(payload, "libelle", "Liaison " + agenceSource.getCodeAgence() + " -> " + agenceDestination.getCodeAgence()));
        compteLiaison.setActif(booleanValue(payload, "actif", true));
        return compteLiaisonAgenceRepository.save(compteLiaison);
    }

    @Transactional
    public OperationDeplacee enregistrerOperationDeplacee(Map<String, Object> payload) {
        Transaction transaction = transactionRepository.findById(longValue(payload, "idTransaction"))
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable"));
        Agence agenceOrigine = payload.get("idAgenceOrigine") == null
                ? extraireAgenceOrigineTransaction(transaction)
                : chargerAgence(longValue(payload, "idAgenceOrigine"));
        Agence agenceOperante = payload.get("idAgenceOperante") == null
                ? transaction.getAgenceOperation()
                : chargerAgence(longValue(payload, "idAgenceOperante"));
        if (agenceOrigine == null || agenceOperante == null) {
            throw new IllegalStateException("Les agences origine et operante sont obligatoires");
        }
        if (agenceOrigine.getIdAgence().equals(agenceOperante.getIdAgence())) {
            throw new IllegalStateException("Une operation deplacee doit concerner deux agences distinctes");
        }
        authenticatedUserService.assertAgencyAccess(agenceOrigine.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceOperante.getIdAgence());

        OperationDeplacee operationDeplacee = new OperationDeplacee();
        operationDeplacee.setTransaction(transaction);
        operationDeplacee.setAgenceOrigine(agenceOrigine);
        operationDeplacee.setAgenceOperante(agenceOperante);
        operationDeplacee.setTypeOperation(defaulted(payload, "typeOperation", transaction.getTypeTransaction().getCodeTypeTransaction()));
        operationDeplacee.setMontant(payload.get("montant") == null ? transaction.getMontantGlobal() : new BigDecimal(payload.get("montant").toString()));
        operationDeplacee.setDevise(defaulted(payload, "devise", "XOF"));
        operationDeplacee.setStatut(defaulted(payload, "statut", "INITIEE"));
        operationDeplacee.setReferenceOperation(defaulted(payload, "referenceOperation", "OPD-" + randomSuffix()));
        operationDeplacee.setDateOperation(LocalDateTime.now());
        operationDeplacee.setCommentaire((String) payload.get("commentaire"));
        OperationDeplacee saved = operationDeplaceeRepository.save(operationDeplacee);

        if (payload.get("tauxCommission") != null || payload.get("montantCommission") != null || payload.get("idCompteComptable") != null) {
            enregistrerCommissionInterAgence(saved, payload);
        }
        return saved;
    }

    @Transactional
    public CommissionInterAgence enregistrerCommissionInterAgence(OperationDeplacee operationDeplacee, Map<String, Object> payload) {
        CommissionInterAgence commission = new CommissionInterAgence();
        commission.setOperationDeplacee(operationDeplacee);
        BigDecimal taux = payload.get("tauxCommission") == null ? BigDecimal.ZERO : new BigDecimal(payload.get("tauxCommission").toString());
        BigDecimal montant = payload.get("montantCommission") == null
                ? operationDeplacee.getMontant().multiply(taux).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP)
                : new BigDecimal(payload.get("montantCommission").toString());
        commission.setTauxCommission(taux);
        commission.setMontantCommission(montant);
        if (payload.get("idCompteComptable") != null) {
            CompteComptable compteComptable = compteComptableRepository.findById(Long.valueOf(payload.get("idCompteComptable").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Compte comptable de commission introuvable"));
            commission.setCompteComptable(compteComptable);
        }
        commission.setStatut(defaulted(payload, "statutCommission", "CALCULEE"));
        commission.setReferencePiece((String) payload.get("referencePiece"));
        commission.setDateCalcul(LocalDateTime.now());
        if (payload.get("dateComptabilisation") != null) {
            commission.setDateComptabilisation(LocalDateTime.parse(payload.get("dateComptabilisation").toString()));
        }
        return commissionInterAgenceRepository.save(commission);
    }

    @Transactional
    public RapprochementInterAgence rapprocherInterAgences(Map<String, Object> payload) {
        Agence agenceSource = chargerAgence(longValue(payload, "idAgenceSource"));
        Agence agenceDestination = chargerAgence(longValue(payload, "idAgenceDestination"));
        if (agenceSource.getIdAgence().equals(agenceDestination.getIdAgence())) {
            throw new IllegalStateException("Le rapprochement inter-agences exige deux agences distinctes");
        }
        authenticatedUserService.assertAgencyAccess(agenceSource.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceDestination.getIdAgence());

        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setAgenceSource(agenceSource);
        rapprochement.setAgenceDestination(agenceDestination);
        rapprochement.setPeriodeDebut(dateValue(payload, "periodeDebut", LocalDate.now()));
        rapprochement.setPeriodeFin(dateValue(payload, "periodeFin", LocalDate.now()));
        if (rapprochement.getPeriodeFin().isBefore(rapprochement.getPeriodeDebut())) {
            throw new IllegalArgumentException("La periode de fin doit etre superieure ou egale a la periode de debut");
        }
        rapprochement.setDateRapprochement(LocalDateTime.now());
        rapprochement.setMontantDebit(decimalOrZero(payload, "montantDebit"));
        rapprochement.setMontantCredit(decimalOrZero(payload, "montantCredit"));
        rapprochement.setEcart(payload.get("ecart") == null
                ? rapprochement.getMontantDebit().subtract(rapprochement.getMontantCredit())
                : new BigDecimal(payload.get("ecart").toString()));
        rapprochement.setStatut(defaulted(payload, "statut", "BROUILLON"));
        rapprochement.setCommentaire((String) payload.get("commentaire"));
        if (payload.get("idValidateur") != null) {
            Utilisateur validateur = utilisateurRepository.findById(Long.valueOf(payload.get("idValidateur").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Validateur introuvable"));
            rapprochement.setValidateur(validateur);
        }
        return rapprochementInterAgenceRepository.save(rapprochement);
    }

    @Transactional(readOnly = true)
    public List<Region> listerRegions() {
        return regionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Agence> listerAgences() {
        if (!authenticatedUserService.hasGlobalScope()) {
            Long idAgence = authenticatedUserService.getCurrentAgencyId();
            return idAgence == null
                    ? List.of()
                    : agenceRepository.findById(idAgence).stream().toList();
        }
        return agenceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Guichet> listerGuichets() {
        if (authenticatedUserService.hasGlobalScope()) {
            return guichetRepository.findAll();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        return guichetRepository.findAll().stream()
                .filter(guichet -> guichet.getAgence() != null && guichet.getAgence().getIdAgence() != null && guichet.getAgence().getIdAgence().equals(idAgence))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParametreAgence> listerParametresAgence() {
        if (authenticatedUserService.hasGlobalScope()) {
            return parametreAgenceRepository.findAll().stream()
                    .sorted(Comparator.comparing((ParametreAgence p) -> p.getAgence().getCodeAgence())
                            .thenComparing(ParametreAgence::getCodeParametre)
                            .thenComparing(ParametreAgence::getVersionParametre).reversed())
                    .toList();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return List.of();
        }
        return parametreAgenceRepository.findByAgence_IdAgenceOrderByCodeParametreAscDateEffetDesc(idAgence);
    }

    @Transactional(readOnly = true)
    public List<MutationPersonnel> listerMutations() {
        if (authenticatedUserService.hasGlobalScope()) {
            return mutationPersonnelRepository.findAll().stream()
                    .sorted(Comparator.comparing(MutationPersonnel::getDateMutation).reversed())
                    .toList();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return List.of();
        }
        return mutationPersonnelRepository.findByAgenceSource_IdAgenceOrAgenceDestination_IdAgenceOrderByDateMutationDesc(idAgence, idAgence);
    }

    @Transactional(readOnly = true)
    public List<CompteLiaisonAgence> listerComptesLiaison() {
        if (authenticatedUserService.hasGlobalScope()) {
            return compteLiaisonAgenceRepository.findAll();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return List.of();
        }
        return compteLiaisonAgenceRepository.findByAgenceSource_IdAgenceOrAgenceDestination_IdAgence(idAgence, idAgence);
    }

    @Transactional(readOnly = true)
    public List<OperationDeplacee> listerOperationsDeplacees() {
        if (authenticatedUserService.hasGlobalScope()) {
            return operationDeplaceeRepository.findAll().stream()
                    .sorted(Comparator.comparing(OperationDeplacee::getDateOperation).reversed())
                    .toList();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return List.of();
        }
        return operationDeplaceeRepository.findByAgenceOperante_IdAgenceOrAgenceOrigine_IdAgenceOrderByDateOperationDesc(idAgence, idAgence);
    }

    @Transactional(readOnly = true)
    public List<CommissionInterAgence> listerCommissionsInterAgences() {
        if (authenticatedUserService.hasGlobalScope()) {
            return commissionInterAgenceRepository.findAll();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return List.of();
        }
        return commissionInterAgenceRepository.findAll().stream()
                .filter(commission -> commission.getOperationDeplacee() != null
                        && ((commission.getOperationDeplacee().getAgenceOrigine() != null
                        && idAgence.equals(commission.getOperationDeplacee().getAgenceOrigine().getIdAgence()))
                        || (commission.getOperationDeplacee().getAgenceOperante() != null
                        && idAgence.equals(commission.getOperationDeplacee().getAgenceOperante().getIdAgence()))))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RapprochementInterAgence> listerRapprochementsInterAgences() {
        if (authenticatedUserService.hasGlobalScope()) {
            return rapprochementInterAgenceRepository.findAll().stream()
                    .sorted(Comparator.comparing(RapprochementInterAgence::getDateRapprochement).reversed())
                    .toList();
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return List.of();
        }
        return rapprochementInterAgenceRepository.findByAgenceSource_IdAgenceOrAgenceDestination_IdAgenceOrderByDateRapprochementDesc(idAgence, idAgence);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> reportingPerformanceReseau() {
        List<Agence> agences = listerAgences();
        List<Client> clients = clientRepository.findAll();
        List<Compte> comptes = compteRepository.findAll();
        List<Credit> credits = creditRepository.findAll();
        List<Transaction> transactions = transactionRepository.findAll();

        return agences.stream().map(agence -> {
            Long idAgence = agence.getIdAgence();
            long clientsCount = clients.stream()
                    .filter(client -> client.getAgence() != null && idAgence.equals(client.getAgence().getIdAgence()))
                    .count();
            long comptesCount = comptes.stream()
                    .filter(compte -> compte.getAgence() != null && idAgence.equals(compte.getAgence().getIdAgence()))
                    .count();
            long creditsCount = credits.stream()
                    .filter(credit -> credit.getClient() != null
                            && credit.getClient().getAgence() != null
                            && idAgence.equals(credit.getClient().getAgence().getIdAgence()))
                    .count();
            BigDecimal volumeTransactions = transactions.stream()
                    .filter(transaction -> transaction.getAgenceOperation() != null
                            && idAgence.equals(transaction.getAgenceOperation().getIdAgence())
                            && transaction.getStatutOperation() == StatutOperation.EXECUTEE)
                    .map(Transaction::getMontantGlobal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idAgence", idAgence);
            item.put("codeAgence", agence.getCodeAgence());
            item.put("nomAgence", agence.getNomAgence());
            item.put("clients", clientsCount);
            item.put("comptes", comptesCount);
            item.put("credits", creditsCount);
            item.put("volumeTransactions", volumeTransactions);
            return item;
        }).toList();
    }

    private Agence chargerAgence(Long idAgence) {
        return agenceRepository.findById(idAgence)
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
    }

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    private String stringValue(Map<String, Object> payload, String key) {
        return required(payload, key);
    }

    private String defaulted(Map<String, Object> payload, String key, String defaultValue) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? defaultValue : value.toString().trim();
    }

    private Long longValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return Long.valueOf(value.toString());
    }

    private LocalDate dateValue(Map<String, Object> payload, String key, LocalDate defaultValue) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? defaultValue : LocalDate.parse(value.toString());
    }

    private boolean booleanValue(Map<String, Object> payload, String key, boolean defaultValue) {
        Object value = payload.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }

    private BigDecimal decimalOrZero(Map<String, Object> payload, String key) {
        return payload.get(key) == null ? BigDecimal.ZERO : new BigDecimal(payload.get(key).toString());
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private int prochaineVersionParametre(Long idAgence, String codeParametre) {
        return parametreAgenceRepository.findByAgence_IdAgenceOrderByCodeParametreAscDateEffetDesc(idAgence).stream()
                .filter(parametre -> parametre.getCodeParametre().equalsIgnoreCase(codeParametre))
                .map(ParametreAgence::getVersionParametre)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private Agence extraireAgenceOrigineTransaction(Transaction transaction) {
        if (transaction.getCompteSource() != null
                && transaction.getCompteSource().getAgence() != null
                && transaction.getCompteSource().getAgence().getIdAgence() != null) {
            return transaction.getCompteSource().getAgence();
        }
        if (transaction.getCompteDestination() != null
                && transaction.getCompteDestination().getAgence() != null
                && transaction.getCompteDestination().getAgence().getIdAgence() != null) {
            return transaction.getCompteDestination().getAgence();
        }
        return transaction.getAgenceOperation();
    }
}
