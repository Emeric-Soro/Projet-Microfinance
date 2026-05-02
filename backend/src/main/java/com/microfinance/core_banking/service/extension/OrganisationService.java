package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.AffecterUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CommissionInterAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCompteLiaisonRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerGuichetRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerMutationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerParametreAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerRegionRequestDTO;
import com.microfinance.core_banking.dto.request.extension.OperationDeplaceeRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RapprochementInterAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ValiderMutationRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ReseauReportingDTO;
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
import java.util.List;
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
    public Region creerRegion(CreerRegionRequestDTO dto) {
        Region region = new Region();
        region.setCodeRegion(dto.getCodeRegion());
        region.setNomRegion(dto.getNomRegion());
        region.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIVE");
        return regionRepository.save(region);
    }

    @Transactional
    public Agence creerAgence(CreerAgenceRequestDTO dto) {
        Agence agence = new Agence();
        agence.setCodeAgence(dto.getCodeAgence());
        agence.setNomAgence(dto.getNomAgence());
        agence.setAdresse(dto.getAdresse());
        agence.setTelephone(dto.getTelephone());
        agence.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIVE");
        if (dto.getIdRegion() != null) {
            agence.setRegion(regionRepository.findById(dto.getIdRegion())
                    .orElseThrow(() -> new EntityNotFoundException("Region introuvable")));
        }
        return agenceRepository.save(agence);
    }

    @Transactional
    public Guichet creerGuichet(CreerGuichetRequestDTO dto) {
        Guichet guichet = new Guichet();
        guichet.setCodeGuichet(dto.getCodeGuichet());
        guichet.setNomGuichet(dto.getNomGuichet());
        guichet.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        guichet.setAgence(chargerAgence(dto.getIdAgence()));
        return guichetRepository.save(guichet);
    }

    @Transactional
    public AffectationUtilisateurAgence affecterUtilisateur(AffecterUtilisateurRequestDTO dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getIdUtilisateur())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        Agence agence = chargerAgence(dto.getIdAgence());

        AffectationUtilisateurAgence affectation = new AffectationUtilisateurAgence();
        affectation.setUtilisateur(utilisateur);
        affectation.setAgence(agence);
        affectation.setRoleOperatoire(dto.getRoleOperatoire());
        affectation.setDateDebut(dto.getDateDebut() != null ? dto.getDateDebut() : LocalDate.now());
        affectation.setDateFin(dto.getDateFin());
        affectation.setActif(dto.getActif() == null || dto.getActif());

        utilisateur.setAgenceActive(agence);
        utilisateurRepository.save(utilisateur);
        return affectationRepository.save(affectation);
    }

    @Transactional
    public ParametreAgence creerParametreAgence(CreerParametreAgenceRequestDTO dto) {
        Agence agence = chargerAgence(dto.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agence.getIdAgence());

        ParametreAgence parametre = new ParametreAgence();
        parametre.setAgence(agence);
        parametre.setCodeParametre(dto.getCodeParametre());
        parametre.setValeurParametre(dto.getValeurParametre());
        parametre.setTypeValeur(dto.getTypeValeur() != null ? dto.getTypeValeur() : "STRING");
        parametre.setDescriptionParametre(dto.getDescriptionParametre());
        parametre.setDateEffet(dto.getDateEffet() != null ? dto.getDateEffet() : LocalDate.now());
        parametre.setDateFin(dto.getDateFin());
        if (parametre.getDateFin() != null && parametre.getDateFin().isBefore(parametre.getDateEffet())) {
            throw new IllegalArgumentException("La date de fin doit etre superieure ou egale a la date d'effet");
        }
        parametre.setActif(dto.getActif() != null ? dto.getActif() : true);
        parametre.setVersionParametre(dto.getVersionParametre() != null
                ? dto.getVersionParametre()
                : prochaineVersionParametre(agence.getIdAgence(), parametre.getCodeParametre()));
        return parametreAgenceRepository.save(parametre);
    }

    @Transactional
    public MutationPersonnel creerMutationPersonnel(CreerMutationRequestDTO dto) {
        Employe employe = employeRepository.findById(dto.getIdEmploye())
                .orElseThrow(() -> new EntityNotFoundException("Employe introuvable"));
        Agence agenceSource = employe.getAgence() != null
                ? employe.getAgence()
                : chargerAgence(dto.getIdAgenceSource());
        Agence agenceDestination = chargerAgence(dto.getIdAgenceDestination());
        if (agenceSource.getIdAgence().equals(agenceDestination.getIdAgence())) {
            throw new IllegalStateException("La mutation doit cibler une agence destination differente");
        }
        authenticatedUserService.assertAgencyAccess(agenceSource.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceDestination.getIdAgence());

        MutationPersonnel mutation = new MutationPersonnel();
        mutation.setEmploye(employe);
        mutation.setAgenceSource(agenceSource);
        mutation.setAgenceDestination(agenceDestination);
        mutation.setDateMutation(dto.getDateMutation() != null ? dto.getDateMutation() : LocalDate.now());
        mutation.setMotif(dto.getMotif());
        mutation.setStatut(dto.getStatut() != null ? dto.getStatut() : "INITIEE");
        return mutationPersonnelRepository.save(mutation);
    }

    @Transactional
    public MutationPersonnel validerMutationPersonnel(Long idMutation, ValiderMutationRequestDTO dto) {
        MutationPersonnel mutation = mutationPersonnelRepository.findById(idMutation)
                .orElseThrow(() -> new EntityNotFoundException("Mutation introuvable"));
        authenticatedUserService.assertAgencyAccess(mutation.getAgenceSource().getIdAgence());
        authenticatedUserService.assertAgencyAccess(mutation.getAgenceDestination().getIdAgence());

        String decision = dto.getDecision() != null ? dto.getDecision().toUpperCase() : "APPROUVEE";
        mutation.setDateValidation(LocalDateTime.now());
        mutation.setCommentaireValidation(dto.getCommentaireValidation());
        if (dto.getIdValidateur() != null) {
            Utilisateur validateur = utilisateurRepository.findById(dto.getIdValidateur())
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
    public CompteLiaisonAgence creerCompteLiaison(CreerCompteLiaisonRequestDTO dto) {
        Agence agenceSource = chargerAgence(dto.getIdAgenceSource());
        Agence agenceDestination = chargerAgence(dto.getIdAgenceDestination());
        if (agenceSource.getIdAgence().equals(agenceDestination.getIdAgence())) {
            throw new IllegalStateException("Le compte de liaison doit relier deux agences distinctes");
        }
        authenticatedUserService.assertAgencyAccess(agenceSource.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceDestination.getIdAgence());
        CompteComptable compteComptable = compteComptableRepository.findById(dto.getIdCompteComptable())
                .orElseThrow(() -> new EntityNotFoundException("Compte comptable introuvable"));

        CompteLiaisonAgence compteLiaison = new CompteLiaisonAgence();
        compteLiaison.setAgenceSource(agenceSource);
        compteLiaison.setAgenceDestination(agenceDestination);
        compteLiaison.setCompteComptable(compteComptable);
        compteLiaison.setLibelle(dto.getLibelle() != null ? dto.getLibelle() : "Liaison " + agenceSource.getCodeAgence() + " -> " + agenceDestination.getCodeAgence());
        compteLiaison.setActif(dto.getActif() != null ? dto.getActif() : true);
        return compteLiaisonAgenceRepository.save(compteLiaison);
    }

    @Transactional
    public OperationDeplacee enregistrerOperationDeplacee(OperationDeplaceeRequestDTO dto) {
        Transaction transaction = transactionRepository.findById(dto.getIdTransaction())
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable"));
        Agence agenceOrigine = dto.getIdAgenceOrigine() == null
                ? extraireAgenceOrigineTransaction(transaction)
                : chargerAgence(dto.getIdAgenceOrigine());
        Agence agenceOperante = dto.getIdAgenceOperante() == null
                ? transaction.getAgenceOperation()
                : chargerAgence(dto.getIdAgenceOperante());
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
        operationDeplacee.setTypeOperation(dto.getTypeOperation() != null ? dto.getTypeOperation() : transaction.getTypeTransaction().getCodeTypeTransaction());
        operationDeplacee.setMontant(dto.getMontant() != null ? dto.getMontant() : transaction.getMontantGlobal());
        operationDeplacee.setDevise(dto.getDevise() != null ? dto.getDevise() : "XOF");
        operationDeplacee.setStatut(dto.getStatut() != null ? dto.getStatut() : "INITIEE");
        operationDeplacee.setReferenceOperation(dto.getReferenceOperation() != null ? dto.getReferenceOperation() : "OPD-" + randomSuffix());
        operationDeplacee.setDateOperation(LocalDateTime.now());
        operationDeplacee.setCommentaire(dto.getCommentaire());
        OperationDeplacee saved = operationDeplaceeRepository.save(operationDeplacee);

        if (dto.getTauxCommission() != null || dto.getMontantCommission() != null || dto.getIdCompteComptable() != null) {
            CommissionInterAgenceRequestDTO commissionDTO = new CommissionInterAgenceRequestDTO();
            commissionDTO.setTauxCommission(dto.getTauxCommission());
            commissionDTO.setMontantCommission(dto.getMontantCommission());
            commissionDTO.setIdCompteComptable(dto.getIdCompteComptable());
            commissionDTO.setStatutCommission(dto.getStatutCommission());
            commissionDTO.setReferencePiece(dto.getReferencePiece());
            commissionDTO.setDateComptabilisation(dto.getDateComptabilisation());
            enregistrerCommissionInterAgence(saved, commissionDTO);
        }
        return saved;
    }

    @Transactional
    public CommissionInterAgence enregistrerCommissionInterAgence(OperationDeplacee operationDeplacee, CommissionInterAgenceRequestDTO dto) {
        CommissionInterAgence commission = new CommissionInterAgence();
        commission.setOperationDeplacee(operationDeplacee);
        BigDecimal taux = dto.getTauxCommission() != null ? dto.getTauxCommission() : BigDecimal.ZERO;
        BigDecimal montant = dto.getMontantCommission() != null
                ? dto.getMontantCommission()
                : operationDeplacee.getMontant().multiply(taux).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        commission.setTauxCommission(taux);
        commission.setMontantCommission(montant);
        if (dto.getIdCompteComptable() != null) {
            CompteComptable compteComptable = compteComptableRepository.findById(dto.getIdCompteComptable())
                    .orElseThrow(() -> new EntityNotFoundException("Compte comptable de commission introuvable"));
            commission.setCompteComptable(compteComptable);
        }
        commission.setStatut(dto.getStatutCommission() != null ? dto.getStatutCommission() : "CALCULEE");
        commission.setReferencePiece(dto.getReferencePiece());
        commission.setDateCalcul(LocalDateTime.now());
        if (dto.getDateComptabilisation() != null) {
            commission.setDateComptabilisation(dto.getDateComptabilisation());
        }
        return commissionInterAgenceRepository.save(commission);
    }

    @Transactional
    public RapprochementInterAgence rapprocherInterAgences(RapprochementInterAgenceRequestDTO dto) {
        Agence agenceSource = chargerAgence(dto.getIdAgenceSource());
        Agence agenceDestination = chargerAgence(dto.getIdAgenceDestination());
        if (agenceSource.getIdAgence().equals(agenceDestination.getIdAgence())) {
            throw new IllegalStateException("Le rapprochement inter-agences exige deux agences distinctes");
        }
        authenticatedUserService.assertAgencyAccess(agenceSource.getIdAgence());
        authenticatedUserService.assertAgencyAccess(agenceDestination.getIdAgence());

        RapprochementInterAgence rapprochement = new RapprochementInterAgence();
        rapprochement.setAgenceSource(agenceSource);
        rapprochement.setAgenceDestination(agenceDestination);
        rapprochement.setPeriodeDebut(dto.getPeriodeDebut() != null ? dto.getPeriodeDebut() : LocalDate.now());
        rapprochement.setPeriodeFin(dto.getPeriodeFin() != null ? dto.getPeriodeFin() : LocalDate.now());
        if (rapprochement.getPeriodeFin().isBefore(rapprochement.getPeriodeDebut())) {
            throw new IllegalArgumentException("La periode de fin doit etre superieure ou egale a la periode de debut");
        }
        rapprochement.setDateRapprochement(LocalDateTime.now());
        rapprochement.setMontantDebit(dto.getMontantDebit() != null ? dto.getMontantDebit() : BigDecimal.ZERO);
        rapprochement.setMontantCredit(dto.getMontantCredit() != null ? dto.getMontantCredit() : BigDecimal.ZERO);
        rapprochement.setEcart(dto.getEcart() != null ? dto.getEcart() : rapprochement.getMontantDebit().subtract(rapprochement.getMontantCredit()));
        rapprochement.setStatut(dto.getStatut() != null ? dto.getStatut() : "BROUILLON");
        rapprochement.setCommentaire(dto.getCommentaire());
        if (dto.getIdValidateur() != null) {
            Utilisateur validateur = utilisateurRepository.findById(dto.getIdValidateur())
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
    public List<ReseauReportingDTO> reportingPerformanceReseau() {
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
            ReseauReportingDTO dto = new ReseauReportingDTO();
            dto.setIdAgence(idAgence);
            dto.setCodeAgence(agence.getCodeAgence());
            dto.setNomAgence(agence.getNomAgence());
            dto.setClients(clientsCount);
            dto.setComptes(comptesCount);
            dto.setCredits(creditsCount);
            dto.setVolumeTransactions(volumeTransactions);
            return dto;
        }).toList();
    }

    private Agence chargerAgence(Long idAgence) {
        return agenceRepository.findById(idAgence)
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
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
