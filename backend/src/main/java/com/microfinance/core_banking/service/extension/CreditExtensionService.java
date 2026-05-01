package com.microfinance.core_banking.service.extension;

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
import java.util.Map;
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
    public ProduitCredit creerProduit(Map<String, Object> payload) {
        ProduitCredit produit = new ProduitCredit();
        produit.setCodeProduit(required(payload, "codeProduit"));
        produit.setLibelle(required(payload, "libelle"));
        produit.setCategorie(required(payload, "categorie"));
        produit.setTauxAnnuel(decimal(payload, "tauxAnnuel"));
        produit.setDureeMinMois(integer(payload, "dureeMinMois"));
        produit.setDureeMaxMois(integer(payload, "dureeMaxMois"));
        produit.setMontantMin(decimal(payload, "montantMin"));
        produit.setMontantMax(decimal(payload, "montantMax"));
        produit.setFraisDossier(optionalDecimal(payload, "fraisDossier"));
        produit.setAssuranceTaux(optionalDecimal(payload, "assuranceTaux"));
        produit.setStatut(defaulted(payload, "statut", "ACTIF"));
        return produitCreditRepository.save(produit);
    }

    @Transactional
    public DemandeCredit creerDemande(Map<String, Object> payload) {
        Client client = clientRepository.findById(longValue(payload, "idClient"))
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));
        verifierPerimetreClient(client);
        ProduitCredit produit = produitCreditRepository.findById(longValue(payload, "idProduitCredit"))
                .orElseThrow(() -> new EntityNotFoundException("Produit de credit introuvable"));

        DemandeCredit demande = new DemandeCredit();
        demande.setReferenceDossier("DCR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        demande.setClient(client);
        demande.setProduitCredit(produit);
        demande.setMontantDemande(decimal(payload, "montantDemande"));
        demande.setDureeMois(integer(payload, "dureeMois"));
        demande.setObjetCredit((String) payload.get("objetCredit"));
        demande.setScoreCredit(payload.get("scoreCredit") == null ? null : integer(payload, "scoreCredit"));
        demande.setStatut(defaulted(payload, "statut", "SOUMISE"));
        if (payload.get("idAgence") != null) {
            Agence agence = agenceRepository.findById(longValue(payload, "idAgence"))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            demande.setAgence(agence);
        }
        return demandeCreditRepository.save(demande);
    }

    @Transactional
    public DemandeCredit deciderDemande(Long idDemande, Map<String, Object> payload) {
        DemandeCredit demande = demandeCreditRepository.findById(idDemande)
                .orElseThrow(() -> new EntityNotFoundException("Demande de credit introuvable"));
        verifierPerimetreClient(demande.getClient());
        demande.setStatut(required(payload, "statut"));
        demande.setAvisComite((String) payload.get("avisComite"));
        demande.setDecisionFinale((String) payload.get("decisionFinale"));
        demande.setDateDecision(LocalDateTime.now());
        return demandeCreditRepository.save(demande);
    }

    @Transactional
    public Credit debloquerCredit(Long idDemande, Map<String, Object> payload) {
        DemandeCredit demande = demandeCreditRepository.findById(idDemande)
                .orElseThrow(() -> new EntityNotFoundException("Demande de credit introuvable"));
        verifierPerimetreClient(demande.getClient());
        if (!"APPROUVEE".equalsIgnoreCase(demande.getStatut()) && !"VALIDEE".equalsIgnoreCase(demande.getStatut())) {
            throw new IllegalStateException("La demande doit etre approuvee avant de creer un credit");
        }

        BigDecimal montantAccorde = payload.get("montantAccorde") == null
                ? demande.getMontantDemande()
                : decimal(payload, "montantAccorde");
        BigDecimal fraisDossier = safe(demande.getProduitCredit().getFraisDossier());
        BigDecimal assuranceInitiale = montantAccorde.multiply(safe(demande.getProduitCredit().getAssuranceTaux()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal fraisPreleves = fraisDossier.add(assuranceInitiale);
        BigDecimal mensualite = calculerMensualite(montantAccorde, demande.getProduitCredit().getTauxAnnuel(), demande.getDureeMois());
        String numCompteDestination = required(payload, "numCompteDestination");
        Long idUtilisateurOperateur = payload.get("idUtilisateurOperateur") == null
                ? authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                : Long.valueOf(payload.get("idUtilisateurOperateur").toString());
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
        credit.setStatut(defaulted(payload, "statut", "ACTIF"));
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
    public GarantieCredit enregistrerGarantie(Long idCredit, Map<String, Object> payload) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        GarantieCredit garantie = new GarantieCredit();
        garantie.setCredit(credit);
        garantie.setTypeGarantie(required(payload, "typeGarantie"));
        garantie.setDescription(required(payload, "description"));
        garantie.setValeur(decimal(payload, "valeur"));
        garantie.setValeurNantie(payload.get("valeurNantie") == null ? garantie.getValeur() : decimal(payload, "valeurNantie"));
        garantie.setStatut(defaulted(payload, "statut", "ACTIVE"));
        return garantieCreditRepository.save(garantie);
    }

    @Transactional
    public RemboursementCredit rembourserCredit(Long idCredit, Map<String, Object> payload) {
        Credit credit = creditRepository.findById(idCredit)
                .orElseThrow(() -> new EntityNotFoundException("Credit introuvable"));
        verifierPerimetreClient(credit.getClient());
        BigDecimal montant = decimal(payload, "montant");
        List<EcheanceCredit> echeances = echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(idCredit).stream()
                .filter(echeance -> !"REGLEE".equalsIgnoreCase(echeance.getStatut()))
                .toList();
        if (echeances.isEmpty()) {
            throw new IllegalStateException("Le credit ne comporte plus d'echeances a rembourser");
        }

        AllocationRemboursement allocation = calculerAllocation(echeances, montant);
        String referenceTransaction = defaulted(payload, "referenceTransaction", "CRPAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());
        RemboursementCredit remboursement = new RemboursementCredit();
        remboursement.setCredit(credit);
        remboursement.setReferenceRemboursement(defaulted(payload, "referenceRemboursement", "REM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase()));
        remboursement.setMontant(montant);
        remboursement.setCapitalPaye(allocation.capitalPaye());
        remboursement.setInteretPaye(allocation.interetPaye());
        remboursement.setAssurancePayee(allocation.assurancePayee());
        remboursement.setReferenceTransaction(referenceTransaction);
        remboursement.setDatePaiement(LocalDateTime.now());
        remboursement.setStatut("COMPTABILISE");
        remboursementCreditRepository.save(remboursement);

        Long idUtilisateurOperateur = payload.get("idUtilisateurOperateur") == null
                ? authenticatedUserService.getCurrentUserOrThrow().getIdUser()
                : Long.valueOf(payload.get("idUtilisateurOperateur").toString());
        transactionService.posterRetraitSysteme(
                required(payload, "numCompteSource"),
                montant,
                BigDecimal.ZERO,
                idUtilisateurOperateur,
                referenceTransaction,
                "CREDIT_REMBOURSEMENT"
        );

        LocalDate dateRegularisation = payload.get("datePaiement") == null ? LocalDate.now() : LocalDate.parse(payload.get("datePaiement").toString());
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
    public List<ImpayeCredit> detecterImpayes(Map<String, Object> payload) {
        LocalDate dateArrete = payload.get("dateArrete") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateArrete").toString());
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
    public List<ProvisionCredit> calculerProvisions(Map<String, Object> payload) {
        LocalDate dateCalcul = payload.get("dateCalcul") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateCalcul").toString());
        detecterImpayes(Map.of("dateArrete", dateCalcul.toString()));
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

    private BigDecimal decimal(Map<String, Object> payload, String key) {
        return new BigDecimal(required(payload, key));
    }

    private BigDecimal optionalDecimal(Map<String, Object> payload, String key) {
        return payload.get(key) == null ? BigDecimal.ZERO : new BigDecimal(payload.get(key).toString());
    }

    private Integer integer(Map<String, Object> payload, String key) {
        return Integer.valueOf(required(payload, key));
    }

    private Long longValue(Map<String, Object> payload, String key) {
        return Long.valueOf(required(payload, key));
    }

    private void genererEcheancier(Credit credit) {
        BigDecimal capitalRestant = credit.getMontantAccorde();
        BigDecimal tauxMensuel = credit.getTauxAnnuel() == null
                ? BigDecimal.ZERO
                : credit.getTauxAnnuel().divide(BigDecimal.valueOf(12 * 100L), 10, RoundingMode.HALF_UP);
        BigDecimal assuranceMensuelle = BigDecimal.ZERO;

        for (int index = 1; index <= credit.getDureeMois(); index++) {
            BigDecimal interet = capitalRestant.multiply(tauxMensuel).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = credit.getMensualite().subtract(interet).subtract(assuranceMensuelle).setScale(2, RoundingMode.HALF_UP);
            if (index == credit.getDureeMois()) {
                capital = capitalRestant;
            }
            EcheanceCredit echeance = new EcheanceCredit();
            echeance.setCredit(credit);
            echeance.setNumeroEcheance(index);
            echeance.setDateEcheance(credit.getDateDeblocage().toLocalDate().plusMonths(index));
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
