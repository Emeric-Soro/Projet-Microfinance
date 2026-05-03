package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ClotureCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerDemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerProduitCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DebloquerCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DeciderDemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EnregistrerGarantieRequestDTO;
import com.microfinance.core_banking.dto.request.extension.PassagePerteCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ReportEcheanceCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RembourserAnticipeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RembourserCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RestructurationCreditRequestDTO;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.EcheanceCredit;
import com.microfinance.core_banking.entity.GarantieCredit;
import com.microfinance.core_banking.entity.ImpayeCredit;
import com.microfinance.core_banking.entity.ProvisionCredit;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.RemboursementCredit;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.DemandeCreditRepository;
import com.microfinance.core_banking.repository.extension.EcheanceCreditRepository;
import com.microfinance.core_banking.repository.extension.GarantieCreditRepository;
import com.microfinance.core_banking.repository.extension.ImpayeCreditRepository;
import com.microfinance.core_banking.repository.extension.ProvisionCreditRepository;
import com.microfinance.core_banking.repository.extension.ProduitCreditRepository;
import com.microfinance.core_banking.repository.extension.RemboursementCreditRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.operation.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CreditExtensionService {

    private final ProduitCreditRepository produitCreditRepository;
    private final DemandeCreditRepository demandeCreditRepository;
    private final CreditRepository creditRepository;
    private final ClientRepository clientRepository;
    private final AgenceRepository agenceRepository;
    private final EcheanceCreditRepository echeanceCreditRepository;
    private final GarantieCreditRepository garantieCreditRepository;
    private final RemboursementCreditRepository remboursementCreditRepository;
    private final ImpayeCreditRepository impayeCreditRepository;
    private final ProvisionCreditRepository provisionCreditRepository;
    private final TransactionService transactionService;
    private final AuthenticatedUserService authenticatedUserService;
    private final ComptabiliteExtensionService comptabiliteExtensionService;

    public CreditExtensionService(
            ProduitCreditRepository produitCreditRepository,
            DemandeCreditRepository demandeCreditRepository,
            CreditRepository creditRepository,
            ClientRepository clientRepository,
            AgenceRepository agenceRepository,
            EcheanceCreditRepository echeanceCreditRepository,
            GarantieCreditRepository garantieCreditRepository,
            RemboursementCreditRepository remboursementCreditRepository,
            ImpayeCreditRepository impayeCreditRepository,
            ProvisionCreditRepository provisionCreditRepository,
            TransactionService transactionService,
            AuthenticatedUserService authenticatedUserService,
            ComptabiliteExtensionService comptabiliteExtensionService
    ) {
        this.produitCreditRepository = produitCreditRepository;
        this.demandeCreditRepository = demandeCreditRepository;
        this.creditRepository = creditRepository;
        this.clientRepository = clientRepository;
        this.agenceRepository = agenceRepository;
        this.echeanceCreditRepository = echeanceCreditRepository;
        this.garantieCreditRepository = garantieCreditRepository;
        this.remboursementCreditRepository = remboursementCreditRepository;
        this.impayeCreditRepository = impayeCreditRepository;
        this.provisionCreditRepository = provisionCreditRepository;
        this.transactionService = transactionService;
        this.authenticatedUserService = authenticatedUserService;
        this.comptabiliteExtensionService = comptabiliteExtensionService;
    }

    @Transactional
    public ProduitCredit creerProduit(CreerProduitCreditRequestDTO dto) {
        ProduitCredit produit = new ProduitCredit();
        produit.setCodeProduit(dto.getCodeProduit());
        produit.setLibelle(dto.getLibelle());
        produit.setCategorie(dto.getCategorie());
        produit.setTauxAnnuel(dto.getTauxAnnuel());
        produit.setDureeMinMois(dto.getDureeMinMois());
        produit.setDureeMaxMois(dto.getDureeMaxMois());
        produit.setMontantMin(dto.getMontantMin());
        produit.setMontantMax(dto.getMontantMax());
        produit.setFraisDossier(dto.getFraisDossier());
        produit.setAssuranceTaux(dto.getAssuranceTaux());
        produit.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        return produitCreditRepository.save(produit);
    }

    @Transactional
    public DemandeCredit creerDemande(CreerDemandeCreditRequestDTO dto) {
        Client client = clientRepository.findById(dto.getIdClient())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        verifierPerimetreClient(client);
        ProduitCredit produit = produitCreditRepository.findById(dto.getIdProduitCredit())
                .orElseThrow(() -> new EntityNotFoundException("Produit de credit introuvable"));

        DemandeCredit demande = new DemandeCredit();
        demande.setReferenceDossier("DCR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        demande.setClient(client);
        demande.setProduitCredit(produit);
        demande.setMontantDemande(dto.getMontantDemande());
        demande.setDureeMois(dto.getDureeMois());
        demande.setObjetCredit(dto.getObjetCredit());
        demande.setScoreCredit(dto.getScoreCredit());
        demande.setStatut(dto.getStatut() != null ? dto.getStatut() : "SOUMISE");
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            demande.setAgence(agence);
        }
        return demandeCreditRepository.save(demande);
    }

    @Transactional
    public DemandeCredit deciderDemande(Long idDemande, DeciderDemandeCreditRequestDTO dto) {
        DemandeCredit demande = demandeCreditRepository.findById(idDemande)
                .orElseThrow(() -> new EntityNotFoundException("Demande de credit introuvable"));
        verifierPerimetreClient(demande.getClient());
        demande.setStatut(dto.getStatut());
        demande.setAvisComite(dto.getAvisComite());
        demande.setDecisionFinale(dto.getDecisionFinale());
        demande.setDateDecision(LocalDateTime.now());
        return demandeCreditRepository.save(demande);
    }

    @Transactional
    public Credit debloquerCredit(Long idDemande, DebloquerCreditRequestDTO dto) {
        DemandeCredit demande = demandeCreditRepository.findById(idDemande)
                .orElseThrow(() -> new EntityNotFoundException("Demande de credit introuvable"));
        verifierPerimetreClient(demande.getClient());
        if (!"APPROUVEE".equalsIgnoreCase(demande.getStatut()) && !"VALIDEE".equalsIgnoreCase(demande.getStatut())) {
            throw new IllegalStateException("La demande doit etre approuvee avant de creer un credit");
        }

        BigDecimal montantAccorde = dto.getMontantAccorde() != null
                ? dto.getMontantAccorde()
                : demande.getMontantDemande();
        BigDecimal fraisDossier = safe(demande.getProduitCredit().getFraisDossier());
        BigDecimal assuranceInitiale = montantAccorde.multiply(safe(demande.getProduitCredit().getAssuranceTaux()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal fraisPreleves = fraisDossier.add(assuranceInitiale);
        BigDecimal mensualite = calculerMensualite(montantAccorde, demande.getProduitCredit().getTauxAnnuel(), demande.getDureeMois());
        String numCompteDestination = dto.getNumCompteDestination();
        Long idUtilisateurOperateur = dto.getIdUtilisateurOperateur() != null
                ? dto.getIdUtilisateurOperateur()
                : authenticatedUserService.getCurrentUserOrThrow().getIdUser();
        var transactionDeblocage = transactionService.posterDepotSysteme(
                numCompteDestination,
                montantAccorde,
                fraisPreleves,
                idUtilisateurOperateur,
                "CRDISB-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase(),
                "CREDIT_DEBLOCAGE"
        );

        Credit credit = new Credit();
        credit.setReferenceCredit("CR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        credit.setDemandeCredit(demande);
        credit.setClient(demande.getClient());
        credit.setMontantAccorde(montantAccorde);
        credit.setTauxAnnuel(demande.getProduitCredit().getTauxAnnuel());
        credit.setDureeMois(demande.getDureeMois());
        credit.setMensualite(mensualite);
        credit.setCapitalRestantDu(montantAccorde);
        credit.setFraisPreleves(fraisPreleves);
        credit.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        credit.setDateDeblocage(LocalDateTime.now());
        credit.setDateProchaineEcheance(LocalDate.now().plusMonths(1));
        credit.setReferenceTransactionDeblocage(transactionDeblocage.getReferenceUnique());
        Credit creditSauvegarde = creditRepository.save(credit);
        genererEcheancier(creditSauvegarde);
        return creditSauvegarde;
    }

    @Transactional(readOnly = true)
    public List<ProduitCredit> listerProduits() {
        return produitCreditRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DemandeCredit> listerDemandes() {
        return demandeCreditRepository.findAll().stream()
                .filter(this::estVisibleDansPerimetre)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Credit> listerCredits() {
        return creditRepository.findAll().stream()
                .filter(credit -> estVisibleDansPerimetre(credit.getDemandeCredit()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EcheanceCredit> listerEcheances(Long idCredit) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        return echeanceCreditRepository.findByCredit_IdCreditOrderByNumeroEcheanceAsc(idCredit);
    }

    @Transactional
    public GarantieCredit enregistrerGarantie(Long idCredit, EnregistrerGarantieRequestDTO dto) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        GarantieCredit garantie = new GarantieCredit();
        garantie.setCredit(credit);
        garantie.setTypeGarantie(dto.getTypeGarantie());
        garantie.setDescription(dto.getDescription());
        garantie.setValeur(dto.getValeur());
        garantie.setValeurNantie(dto.getValeurNantie() != null ? dto.getValeurNantie() : garantie.getValeur());
        garantie.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIVE");
        return garantieCreditRepository.save(garantie);
    }

    @Transactional
    public RemboursementCredit rembourserCredit(Long idCredit, RembourserCreditRequestDTO dto) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        BigDecimal montant = dto.getMontant();
        List<EcheanceCredit> echeances = echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(idCredit).stream()
                .filter(echeance -> !"REGLEE".equalsIgnoreCase(echeance.getStatut()))
                .toList();
        if (echeances.isEmpty()) {
            throw new IllegalStateException("Le credit ne comporte plus d'echeances a rembourser");
        }

        AllocationRemboursement allocation = calculerAllocation(echeances, montant);
        String referenceTransaction = dto.getReferenceTransaction() != null ? dto.getReferenceTransaction() : "CRPAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
        RemboursementCredit remboursement = new RemboursementCredit();
        remboursement.setCredit(credit);
        remboursement.setReferenceRemboursement(dto.getReferenceRemboursement() != null ? dto.getReferenceRemboursement() : "REM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
        remboursement.setMontant(montant);
        remboursement.setCapitalPaye(allocation.capitalPaye());
        remboursement.setInteretPaye(allocation.interetPaye());
        remboursement.setAssurancePayee(allocation.assurancePayee());
        remboursement.setReferenceTransaction(referenceTransaction);
        remboursement.setDatePaiement(LocalDateTime.now());
        remboursement.setStatut("COMPTABILISE");
        remboursementCreditRepository.save(remboursement);

        Long idUtilisateurOperateur = dto.getIdUtilisateurOperateur() != null
                ? dto.getIdUtilisateurOperateur()
                : authenticatedUserService.getCurrentUserOrThrow().getIdUser();
        transactionService.posterRetraitSysteme(
                dto.getNumCompteSource(),
                montant,
                BigDecimal.ZERO,
                idUtilisateurOperateur,
                referenceTransaction,
                "CREDIT_REMBOURSEMENT"
        );

        LocalDate dateRegularisation = dto.getDatePaiement() != null ? dto.getDatePaiement() : LocalDate.now();
        for (EcheanceCredit echeance : allocation.echeancesMaj()) {
            if (echeance.getCapitalPaye().add(echeance.getInteretPaye()).add(echeance.getAssurancePayee())
                    .compareTo(echeance.getTotalPrevu()) >= 0) {
                echeance.setStatut("REGLEE");
            } else if (echeance.getCapitalPaye().add(echeance.getInteretPaye()).add(echeance.getAssurancePayee()).compareTo(BigDecimal.ZERO) > 0) {
                echeance.setStatut("PARTIELLE");
            }
            echeance.setDateDerniereRegularisation(dateRegularisation);
            echeanceCreditRepository.save(echeance);
        }

        credit.setCapitalRestantDu(credit.getCapitalRestantDu().subtract(allocation.capitalPaye()).max(BigDecimal.ZERO));
        credit.setDateProchaineEcheance(echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(idCredit).stream()
                .filter(echeance -> !"REGLEE".equalsIgnoreCase(echeance.getStatut()))
                .map(EcheanceCredit::getDateEcheance)
                .findFirst()
                .orElse(null));
        if (credit.getCapitalRestantDu().compareTo(BigDecimal.ZERO) == 0) {
            credit.setStatut("REMBOURSE");
        }
        creditRepository.save(credit);
        return remboursement;
    }

    @Transactional
    public List<ImpayeCredit> detecterImpayes(DetecterImpayesRequestDTO dto) {
        LocalDate dateArrete = dto.getDateArrete() != null ? dto.getDateArrete() : LocalDate.now();
        List<ImpayeCredit> resultats = new java.util.ArrayList<>();
        for (Credit credit : listerCredits()) {
            for (EcheanceCredit echeance : echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(credit.getIdCredit())) {
                BigDecimal reste = montantRestant(echeance);
                ImpayeCredit impaye = impayeCreditRepository.findByEcheanceCredit_IdEcheanceCredit(echeance.getIdEcheanceCredit()).orElse(null);
                if (reste.compareTo(BigDecimal.ZERO) <= 0) {
                    cloturerImpayeSiNecessaire(echeance, impaye, dateArrete);
                    continue;
                }
                if (!echeance.getDateEcheance().isBefore(dateArrete)) {
                    continue;
                }
                int joursRetard = (int) java.time.temporal.ChronoUnit.DAYS.between(echeance.getDateEcheance(), dateArrete);
                String classeRisque = classeRisque(joursRetard);
                if (impaye == null) {
                    impaye = new ImpayeCredit();
                }
                impaye.setCredit(credit);
                impaye.setEcheanceCredit(echeance);
                impaye.setMontant(reste);
                impaye.setJoursRetard(joursRetard);
                impaye.setClasseRisque(classeRisque);
                impaye.setStatut("OUVERT");
                resultats.add(impayeCreditRepository.save(impaye));
                echeance.setStatut("IMPAYEE");
                echeanceCreditRepository.save(echeance);
            }
        }
        return resultats;
    }

    @Transactional
    public List<ProvisionCredit> calculerProvisions(CalculerProvisionsRequestDTO dto) {
        LocalDate dateCalcul = dto.getDateCalcul() != null ? dto.getDateCalcul() : LocalDate.now();
        DetecterImpayesRequestDTO detectDto = new DetecterImpayesRequestDTO();
        detectDto.setDateArrete(dateCalcul);
        detecterImpayes(detectDto);
        List<ProvisionCredit> provisions = new java.util.ArrayList<>();
        for (Credit credit : listerCredits()) {
            int retardMax = impayeCreditRepository.findByCredit_IdCreditAndStatutIgnoreCaseOrderByJoursRetardDesc(credit.getIdCredit(), "OUVERT").stream()
                    .map(ImpayeCredit::getJoursRetard)
                    .findFirst()
                    .orElse(0);
            BigDecimal tauxProvision = tauxProvision(retardMax);
            ProvisionCredit provision = provisionCreditRepository.findByCredit_IdCreditAndDateCalcul(credit.getIdCredit(), dateCalcul)
                    .orElseGet(ProvisionCredit::new);
            if (tauxProvision.compareTo(BigDecimal.ZERO) == 0) {
                if (provision.getIdProvisionCredit() != null) {
                    provision.setCredit(credit);
                    provision.setDateCalcul(dateCalcul);
                    provision.setTauxProvision(BigDecimal.ZERO);
                    provision.setMontantProvision(BigDecimal.ZERO);
                    provision.setReferencePieceComptable(null);
                    provision.setStatut("ANNULEE");
                    provisions.add(provisionCreditRepository.save(provision));
                }
                continue;
            }
            provision.setCredit(credit);
            provision.setDateCalcul(dateCalcul);
            provision.setTauxProvision(tauxProvision);
            provision.setMontantProvision(credit.getCapitalRestantDu()
                    .multiply(tauxProvision)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            provision.setStatut("CALCULEE");
            ProvisionCredit sauvegardee = provisionCreditRepository.save(provision);
            if (sauvegardee.getMontantProvision().compareTo(BigDecimal.ZERO) > 0) {
                comptabiliteExtensionService.comptabiliserProvisionCredit(sauvegardee);
            }
            provisions.add(sauvegardee);
        }
        return provisions;
    }

    @Transactional(readOnly = true)
    public List<GarantieCredit> listerGaranties(Long idCredit) {
        return garantieCreditRepository.findByCredit_IdCreditOrderByCreatedAtDesc(idCredit);
    }

    @Transactional(readOnly = true)
    public List<RemboursementCredit> listerRemboursements(Long idCredit) {
        return remboursementCreditRepository.findByCredit_IdCreditOrderByDatePaiementDesc(idCredit);
    }

    @Transactional(readOnly = true)
    public List<ImpayeCredit> listerImpayes(Long idCredit) {
        return impayeCreditRepository.findByCredit_IdCreditOrderByJoursRetardDesc(idCredit);
    }

    @Transactional(readOnly = true)
    public List<ProvisionCredit> listerProvisions(Long idCredit) {
        return provisionCreditRepository.findByCredit_IdCreditOrderByDateCalculDesc(idCredit);
    }

    @Transactional
    public Credit restructurerCredit(Long idCredit, RestructurationCreditRequestDTO dto) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        if ("PERTE".equalsIgnoreCase(credit.getStatut())) {
            throw new IllegalStateException("Un credit passe en perte ne peut plus etre restructure");
        }

        BigDecimal capitalRestant = credit.getCapitalRestantDu() == null ? BigDecimal.ZERO : credit.getCapitalRestantDu();
        if (capitalRestant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Aucun capital restant a restructurer");
        }

        BigDecimal taux = dto.getNouveauTauxAnnuel() != null ? dto.getNouveauTauxAnnuel() : credit.getTauxAnnuel();
        credit.setTauxAnnuel(taux);
        credit.setDureeMois(dto.getNouvelleDureeMois());
        credit.setMensualite(calculerMensualite(capitalRestant, taux, dto.getNouvelleDureeMois()));
        credit.setCapitalRestantDu(capitalRestant);
        credit.setStatut("RESTRUCTURE");
        credit.setDateProchaineEcheance(LocalDate.now().plusMonths(1));

        List<EcheanceCredit> echeances = echeanceCreditRepository.findByCredit_IdCreditOrderByNumeroEcheanceAsc(idCredit);
        for (EcheanceCredit echeance : echeances) {
            BigDecimal dejaPaye = echeance.getCapitalPaye().add(echeance.getInteretPaye()).add(echeance.getAssurancePayee());
            if (dejaPaye.compareTo(BigDecimal.ZERO) == 0) {
                echeanceCreditRepository.delete(echeance);
            } else if (!"REGLEE".equalsIgnoreCase(echeance.getStatut())) {
                echeance.setStatut("RESTRUCTUREE");
                echeanceCreditRepository.save(echeance);
            }
        }

        Credit sauvegarde = creditRepository.save(credit);
        genererEcheancierRestructure(sauvegarde, capitalRestant, dto.getNouvelleDureeMois(), taux);
        return sauvegarde;
    }

    @Transactional
    public EcheanceCredit reporterEcheance(Long idCredit, Long idEcheanceCredit, ReportEcheanceCreditRequestDTO dto) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        EcheanceCredit echeance = echeanceCreditRepository.findById(idEcheanceCredit)
                .orElseThrow(() -> new EntityNotFoundException("Echeance introuvable"));
        if (!idCredit.equals(echeance.getCredit().getIdCredit())) {
            throw new IllegalArgumentException("L'echeance ne correspond pas au credit cible");
        }
        if ("REGLEE".equalsIgnoreCase(echeance.getStatut())) {
            throw new IllegalStateException("Une echeance deja reglee ne peut pas etre reportee");
        }
        if (dto.getNouvelleDateEcheance().isBefore(echeance.getDateEcheance())) {
            throw new IllegalArgumentException("La nouvelle date d'echeance doit etre posterieure ou egale a la date actuelle");
        }

        echeance.setDateEcheance(dto.getNouvelleDateEcheance());
        echeance.setStatut("REPORTEE");
        echeance = echeanceCreditRepository.save(echeance);
        credit.setDateProchaineEcheance(echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(idCredit).stream()
                .filter(item -> !"REGLEE".equalsIgnoreCase(item.getStatut()))
                .map(EcheanceCredit::getDateEcheance)
                .min(LocalDate::compareTo)
                .orElse(dto.getNouvelleDateEcheance()));
        creditRepository.save(credit);
        return echeance;
    }

    @AuditLog(action = "CREDIT_CLOSE", resource = "CREDIT")
    @Transactional
    public Credit cloturerCredit(String referenceCredit, String commentaire) {
        Credit credit = creditRepository.findByReferenceCredit(referenceCredit)
                .orElseThrow(() -> new EntityNotFoundException("Crédit introuvable avec la référence : " + referenceCredit));
        if ("REMBOURSE".equalsIgnoreCase(credit.getStatut()) || "PERTE".equalsIgnoreCase(credit.getStatut())) {
            throw new IllegalStateException("Le crédit est déjà dans un état final (" + credit.getStatut() + ")");
        }
        if (credit.getCapitalRestantDu().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Le crédit n'est pas soldé. Capital restant dû : " + credit.getCapitalRestantDu());
        }
        credit.setStatut("REMBOURSE");
        return creditRepository.save(credit);
    }

    @Transactional
    public RemboursementCredit rembourserAnticipeCredit(Long idCredit, RembourserAnticipeCreditRequestDTO dto) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        if ("REMBOURSE".equalsIgnoreCase(credit.getStatut()) || "PERTE".equalsIgnoreCase(credit.getStatut())) {
            throw new IllegalStateException("Le credit est deja solde ou passe en perte");
        }

        BigDecimal capitalRestant = credit.getCapitalRestantDu();
        if (capitalRestant == null || capitalRestant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Le credit ne comporte plus de capital restant");
        }

        BigDecimal penaliteTaux = dto.getPenaliteTaux() != null ? dto.getPenaliteTaux() : BigDecimal.valueOf(2.0);
        BigDecimal penalite = capitalRestant.multiply(penaliteTaux).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal montantTotal = capitalRestant.add(penalite);

        String referenceTransaction = dto.getReferenceTransaction() != null ? dto.getReferenceTransaction()
                : "ANT-REM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();

        RemboursementCredit remboursement = new RemboursementCredit();
        remboursement.setCredit(credit);
        remboursement.setReferenceRemboursement(dto.getReferenceRemboursement() != null ? dto.getReferenceRemboursement()
                : "ANT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
        remboursement.setMontant(montantTotal);
        remboursement.setCapitalPaye(capitalRestant);
        remboursement.setInteretPaye(BigDecimal.ZERO);
        remboursement.setAssurancePayee(BigDecimal.ZERO);
        remboursement.setReferenceTransaction(referenceTransaction);
        remboursement.setDatePaiement(dto.getDatePaiement() != null ? dto.getDatePaiement().atStartOfDay() : LocalDateTime.now());
        remboursement.setStatut("COMPTABILISE");
        remboursementCreditRepository.save(remboursement);

        Long idUtilisateurOperateur = dto.getIdUtilisateurOperateur() != null
                ? dto.getIdUtilisateurOperateur()
                : authenticatedUserService.getCurrentUserOrThrow().getIdUser();

        transactionService.posterRetraitSysteme(
                dto.getNumCompteSource(),
                montantTotal,
                BigDecimal.ZERO,
                idUtilisateurOperateur,
                referenceTransaction,
                "CREDIT_REMBOURSEMENT"
        );

        List<EcheanceCredit> echeances = echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(idCredit);
        LocalDate dateRegularisation = dto.getDatePaiement() != null ? dto.getDatePaiement() : LocalDate.now();
        for (EcheanceCredit echeance : echeances) {
            if (!"REGLEE".equalsIgnoreCase(echeance.getStatut())) {
                echeance.setStatut("REGLEE");
                echeance.setDateDerniereRegularisation(dateRegularisation);
                echeance.setCapitalPaye(echeance.getCapitalPrevu());
                echeance.setInteretPaye(echeance.getInteretPrevu());
                echeance.setAssurancePayee(echeance.getAssurancePrevue());
                echeanceCreditRepository.save(echeance);
            }
        }

        credit.setCapitalRestantDu(BigDecimal.ZERO);
        credit.setStatut("REMBOURSE");
        credit.setDateProchaineEcheance(null);
        creditRepository.save(credit);

        return remboursement;
    }

    @Transactional
    public Credit passerEnPerte(Long idCredit, PassagePerteCreditRequestDTO dto) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        List<ImpayeCredit> impayes = impayeCreditRepository.findByCredit_IdCreditAndStatutIgnoreCaseOrderByJoursRetardDesc(idCredit, "OUVERT");
        int retardMax = impayes.stream().map(ImpayeCredit::getJoursRetard).findFirst().orElse(0);
        if (retardMax < 180) {
            throw new IllegalStateException("Le passage en perte exige au moins 180 jours de retard");
        }

        credit.setStatut("PERTE");
        for (ImpayeCredit impaye : impayes) {
            impaye.setClasseRisque("PERTE");
            impayeCreditRepository.save(impaye);
        }
        return creditRepository.save(credit);
    }

    private BigDecimal calculerMensualite(BigDecimal principal, BigDecimal tauxAnnuel, Integer dureeMois) {
        if (principal == null || dureeMois == null || dureeMois <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal tauxMensuel = tauxAnnuel == null
                ? BigDecimal.ZERO
                : tauxAnnuel.divide(BigDecimal.valueOf(12 * 100L), 10, RoundingMode.HALF_UP);
        if (tauxMensuel.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(dureeMois), 2, RoundingMode.HALF_UP);
        }
        double t = tauxMensuel.doubleValue();
        double n = dureeMois.doubleValue();
        double facteur = (t * Math.pow(1 + t, n)) / (Math.pow(1 + t, n) - 1);
        return principal.multiply(BigDecimal.valueOf(facteur)).setScale(2, RoundingMode.HALF_UP);
    }

    private void genererEcheancier(Credit credit) {
        BigDecimal capitalRestant = credit.getMontantAccorde();
        genererEcheancierRestructure(credit, capitalRestant, credit.getDureeMois(), credit.getTauxAnnuel());
    }

    private void genererEcheancierRestructure(Credit credit, BigDecimal capitalRestantInitial, Integer dureeMois, BigDecimal tauxAnnuel) {
        BigDecimal capitalRestant = capitalRestantInitial;
        BigDecimal tauxMensuel = tauxAnnuel == null
                ? BigDecimal.ZERO
                : tauxAnnuel.divide(BigDecimal.valueOf(12 * 100L), 10, RoundingMode.HALF_UP);
        BigDecimal assuranceMensuelle = BigDecimal.ZERO;

        int numeroDepart = echeanceCreditRepository.findByCredit_IdCreditOrderByNumeroEcheanceAsc(credit.getIdCredit()).stream()
                .map(EcheanceCredit::getNumeroEcheance)
                .max(Integer::compareTo)
                .orElse(0);

        for (int index = 1; index <= dureeMois; index++) {
            BigDecimal interet = capitalRestant.multiply(tauxMensuel).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = credit.getMensualite().subtract(interet).subtract(assuranceMensuelle).setScale(2, RoundingMode.HALF_UP);
            if (index == dureeMois) {
                capital = capitalRestant;
            }
            EcheanceCredit echeance = new EcheanceCredit();
            echeance.setCredit(credit);
            echeance.setNumeroEcheance(numeroDepart + index);
            LocalDate baseDate = credit.getDateProchaineEcheance() != null
                    ? credit.getDateProchaineEcheance().minusMonths(1)
                    : (credit.getDateDeblocage() != null ? credit.getDateDeblocage().toLocalDate() : LocalDate.now());
            echeance.setDateEcheance(baseDate.plusMonths(index));
            echeance.setCapitalPrevu(capital);
            echeance.setInteretPrevu(interet);
            echeance.setAssurancePrevue(assuranceMensuelle);
            echeance.setTotalPrevu(capital.add(interet).add(assuranceMensuelle));
            echeanceCreditRepository.save(echeance);
            capitalRestant = capitalRestant.subtract(capital).max(BigDecimal.ZERO);
        }
    }

    private boolean estVisibleDansPerimetre(DemandeCredit demande) {
        if (authenticatedUserService.hasGlobalScope()) {
            return true;
        }
        Long idAgence = authenticatedUserService.getCurrentAgencyId();
        if (idAgence == null) {
            return false;
        }
        if (demande.getAgence() != null && demande.getAgence().getIdAgence() != null) {
            return idAgence.equals(demande.getAgence().getIdAgence());
        }
        return demande.getClient() != null
                && demande.getClient().getAgence() != null
                && idAgence.equals(demande.getClient().getAgence().getIdAgence());
    }

    private void verifierPerimetreClient(Client client) {
        if (client == null || client.getAgence() == null) {
            return;
        }
        authenticatedUserService.assertAgencyAccess(client.getAgence().getIdAgence());
    }

    private AllocationRemboursement calculerAllocation(List<EcheanceCredit> echeances, BigDecimal montantDisponible) {
        BigDecimal restant = montantDisponible;
        BigDecimal capital = BigDecimal.ZERO;
        BigDecimal interet = BigDecimal.ZERO;
        BigDecimal assurance = BigDecimal.ZERO;

        for (EcheanceCredit echeance : echeances) {
            if (restant.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal interetReste = echeance.getInteretPrevu().subtract(echeance.getInteretPaye()).max(BigDecimal.ZERO);
            BigDecimal assuranceReste = echeance.getAssurancePrevue().subtract(echeance.getAssurancePayee()).max(BigDecimal.ZERO);
            BigDecimal capitalReste = echeance.getCapitalPrevu().subtract(echeance.getCapitalPaye()).max(BigDecimal.ZERO);

            BigDecimal paiementInteret = restant.min(interetReste);
            echeance.setInteretPaye(echeance.getInteretPaye().add(paiementInteret));
            interet = interet.add(paiementInteret);
            restant = restant.subtract(paiementInteret);

            BigDecimal paiementAssurance = restant.min(assuranceReste);
            echeance.setAssurancePayee(echeance.getAssurancePayee().add(paiementAssurance));
            assurance = assurance.add(paiementAssurance);
            restant = restant.subtract(paiementAssurance);

            BigDecimal paiementCapital = restant.min(capitalReste);
            echeance.setCapitalPaye(echeance.getCapitalPaye().add(paiementCapital));
            capital = capital.add(paiementCapital);
            restant = restant.subtract(paiementCapital);
        }

        if (restant.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Le montant depasse le reste a payer sur ce credit");
        }
        return new AllocationRemboursement(echeances, capital, interet, assurance);
    }

    private BigDecimal montantRestant(EcheanceCredit echeance) {
        return echeance.getTotalPrevu()
                .subtract(echeance.getCapitalPaye())
                .subtract(echeance.getInteretPaye())
                .subtract(echeance.getAssurancePayee())
                .max(BigDecimal.ZERO);
    }

    private String classeRisque(int joursRetard) {
        if (joursRetard <= 30) {
            return "SURVEILLE";
        }
        if (joursRetard <= 90) {
            return "SENSIBLE";
        }
        if (joursRetard <= 180) {
            return "DOUTEUX";
        }
        return "PERTE";
    }

    private void cloturerImpayeSiNecessaire(EcheanceCredit echeance, ImpayeCredit impaye, LocalDate dateRegularisation) {
        if (impaye != null && !"CLOTURE".equalsIgnoreCase(impaye.getStatut())) {
            impaye.setMontant(BigDecimal.ZERO);
            impaye.setJoursRetard(0);
            impaye.setStatut("CLOTURE");
            impayeCreditRepository.save(impaye);
        }
        if ("IMPAYEE".equalsIgnoreCase(echeance.getStatut())) {
            echeance.setStatut("REGLEE");
            echeance.setDateDerniereRegularisation(dateRegularisation);
            echeanceCreditRepository.save(echeance);
        }
    }

    private BigDecimal safe(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private BigDecimal tauxProvision(int joursRetard) {
        if (joursRetard <= 30) {
            return BigDecimal.valueOf(5);
        }
        if (joursRetard <= 90) {
            return BigDecimal.valueOf(25);
        }
        if (joursRetard <= 180) {
            return BigDecimal.valueOf(50);
        }
        if (joursRetard > 180) {
            return BigDecimal.valueOf(100);
        }
        return BigDecimal.ZERO;
    }

    private record AllocationRemboursement(
            List<EcheanceCredit> echeancesMaj,
            BigDecimal capitalPaye,
            BigDecimal interetPaye,
            BigDecimal assurancePayee
    ) {
    }
}
