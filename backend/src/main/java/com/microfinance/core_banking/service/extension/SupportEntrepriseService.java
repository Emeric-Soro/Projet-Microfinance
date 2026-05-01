package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.BudgetExploitation;
import com.microfinance.core_banking.entity.BulletinPaie;
import com.microfinance.core_banking.entity.CommandeAchat;
import com.microfinance.core_banking.entity.Employe;
import com.microfinance.core_banking.entity.Fournisseur;
import com.microfinance.core_banking.entity.Immobilisation;
import com.microfinance.core_banking.entity.LigneBudget;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.BudgetExploitationRepository;
import com.microfinance.core_banking.repository.extension.BulletinPaieRepository;
import com.microfinance.core_banking.repository.extension.CommandeAchatRepository;
import com.microfinance.core_banking.repository.extension.EmployeRepository;
import com.microfinance.core_banking.repository.extension.FournisseurRepository;
import com.microfinance.core_banking.repository.extension.ImmobilisationRepository;
import com.microfinance.core_banking.repository.extension.LigneBudgetRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SupportEntrepriseService {

    private final BudgetExploitationRepository budgetExploitationRepository;
    private final LigneBudgetRepository ligneBudgetRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CommandeAchatRepository commandeAchatRepository;
    private final BulletinPaieRepository bulletinPaieRepository;
    private final ImmobilisationRepository immobilisationRepository;
    private final EmployeRepository employeRepository;
    private final AgenceRepository agenceRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public SupportEntrepriseService(
            BudgetExploitationRepository budgetExploitationRepository,
            LigneBudgetRepository ligneBudgetRepository,
            FournisseurRepository fournisseurRepository,
            CommandeAchatRepository commandeAchatRepository,
            BulletinPaieRepository bulletinPaieRepository,
            ImmobilisationRepository immobilisationRepository,
            EmployeRepository employeRepository,
            AgenceRepository agenceRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.budgetExploitationRepository = budgetExploitationRepository;
        this.ligneBudgetRepository = ligneBudgetRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.commandeAchatRepository = commandeAchatRepository;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.immobilisationRepository = immobilisationRepository;
        this.employeRepository = employeRepository;
        this.agenceRepository = agenceRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public BudgetExploitation creerBudget(Map<String, Object> payload) {
        BudgetExploitation budget = new BudgetExploitation();
        budget.setCodeBudget(defaulted(payload, "codeBudget", "BDG-" + randomSuffix()));
        budget.setAnnee(Integer.valueOf(required(payload, "annee")));
        budget.setMontantTotal(decimalOrZero(payload, "montantTotal"));
        budget.setStatut(defaulted(payload, "statut", "BROUILLON"));
        if (payload.get("idAgence") != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(payload.get("idAgence").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            budget.setAgence(agence);
        }
        BudgetExploitation saved = budgetExploitationRepository.save(budget);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lignes = (List<Map<String, Object>>) payload.get("lignes");
        if (lignes != null) {
            for (Map<String, Object> lignePayload : lignes) {
                LigneBudget ligneBudget = new LigneBudget();
                ligneBudget.setBudget(saved);
                ligneBudget.setRubrique(required(lignePayload, "rubrique"));
                ligneBudget.setMontantPrevu(decimalOrZero(lignePayload, "montantPrevu"));
                ligneBudget.setMontantEngage(decimalOrZero(lignePayload, "montantEngage"));
                ligneBudget.setMontantConsomme(decimalOrZero(lignePayload, "montantConsomme"));
                ligneBudgetRepository.save(ligneBudget);
            }
        }
        return saved;
    }

    @Transactional
    public Fournisseur creerFournisseur(Map<String, Object> payload) {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setCodeFournisseur(defaulted(payload, "codeFournisseur", "FRN-" + randomSuffix()));
        fournisseur.setNom(required(payload, "nom"));
        fournisseur.setContact((String) payload.get("contact"));
        fournisseur.setTelephone((String) payload.get("telephone"));
        fournisseur.setEmail((String) payload.get("email"));
        fournisseur.setStatut(defaulted(payload, "statut", "ACTIF"));
        return fournisseurRepository.save(fournisseur);
    }

    @Transactional
    public CommandeAchat creerCommandeAchat(Map<String, Object> payload) {
        Fournisseur fournisseur = fournisseurRepository.findById(Long.valueOf(required(payload, "idFournisseur")))
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable"));
        CommandeAchat commandeAchat = new CommandeAchat();
        commandeAchat.setReferenceCommande(defaulted(payload, "referenceCommande", "CDA-" + randomSuffix()));
        commandeAchat.setFournisseur(fournisseur);
        commandeAchat.setObjet(required(payload, "objet"));
        commandeAchat.setMontant(decimalOrZero(payload, "montant"));
        commandeAchat.setDateCommande(payload.get("dateCommande") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateCommande").toString()));
        commandeAchat.setStatut(defaulted(payload, "statut", "INITIEE"));
        if (payload.get("idAgence") != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(payload.get("idAgence").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            commandeAchat.setAgence(agence);
        }
        return commandeAchatRepository.save(commandeAchat);
    }

    @Transactional
    public BulletinPaie genererBulletinPaie(Map<String, Object> payload) {
        Employe employe = employeRepository.findById(Long.valueOf(required(payload, "idEmploye")))
                .orElseThrow(() -> new EntityNotFoundException("Employe introuvable"));
        if (employe.getAgence() != null) {
            authenticatedUserService.assertAgencyAccess(employe.getAgence().getIdAgence());
        }
        BulletinPaie bulletinPaie = new BulletinPaie();
        bulletinPaie.setEmploye(employe);
        bulletinPaie.setPeriode(required(payload, "periode"));
        bulletinPaie.setSalaireBrut(decimalOrZero(payload, "salaireBrut"));
        bulletinPaie.setRetenues(decimalOrZero(payload, "retenues"));
        bulletinPaie.setSalaireNet(payload.get("salaireNet") == null
                ? bulletinPaie.getSalaireBrut().subtract(bulletinPaie.getRetenues())
                : decimalOrZero(payload, "salaireNet"));
        bulletinPaie.setStatut(defaulted(payload, "statut", "BROUILLON"));
        return bulletinPaieRepository.save(bulletinPaie);
    }

    @Transactional
    public Immobilisation creerImmobilisation(Map<String, Object> payload) {
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setCodeImmobilisation(defaulted(payload, "codeImmobilisation", "IMM-" + randomSuffix()));
        immobilisation.setLibelle(required(payload, "libelle"));
        immobilisation.setValeurOrigine(decimalOrZero(payload, "valeurOrigine"));
        immobilisation.setDureeAmortissementMois(Integer.valueOf(required(payload, "dureeAmortissementMois")));
        immobilisation.setAmortissementMensuel(immobilisation.getValeurOrigine()
                .divide(BigDecimal.valueOf(immobilisation.getDureeAmortissementMois()), 2, RoundingMode.HALF_UP));
        immobilisation.setValeurNette(payload.get("valeurNette") == null ? immobilisation.getValeurOrigine() : decimalOrZero(payload, "valeurNette"));
        immobilisation.setDateAcquisition(payload.get("dateAcquisition") == null ? LocalDate.now() : LocalDate.parse(payload.get("dateAcquisition").toString()));
        immobilisation.setStatut(defaulted(payload, "statut", "ACTIVE"));
        if (payload.get("idAgence") != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(payload.get("idAgence").toString()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            immobilisation.setAgence(agence);
        }
        return immobilisationRepository.save(immobilisation);
    }

    @Transactional(readOnly = true)
    public List<BudgetExploitation> listerBudgets() {
        return budgetExploitationRepository.findAll().stream()
                .filter(budget -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && budget.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(budget.getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LigneBudget> listerLignesBudget(Long idBudget) {
        BudgetExploitation budget = budgetExploitationRepository.findById(idBudget)
                .orElseThrow(() -> new EntityNotFoundException("Budget introuvable"));
        if (budget.getAgence() != null) {
            authenticatedUserService.assertAgencyAccess(budget.getAgence().getIdAgence());
        }
        return ligneBudgetRepository.findByBudget_IdBudgetOrderByRubriqueAsc(idBudget);
    }

    @Transactional(readOnly = true)
    public List<Fournisseur> listerFournisseurs() {
        return fournisseurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CommandeAchat> listerCommandesAchat() {
        return commandeAchatRepository.findAll().stream()
                .filter(commande -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && commande.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(commande.getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BulletinPaie> listerBulletinsPaie() {
        return bulletinPaieRepository.findAll().stream()
                .filter(bulletin -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && bulletin.getEmploye() != null
                        && bulletin.getEmploye().getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(bulletin.getEmploye().getAgence().getIdAgence())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Immobilisation> listerImmobilisations() {
        return immobilisationRepository.findAll().stream()
                .filter(immobilisation -> authenticatedUserService.hasGlobalScope()
                        || (authenticatedUserService.getCurrentAgencyId() != null
                        && immobilisation.getAgence() != null
                        && authenticatedUserService.getCurrentAgencyId().equals(immobilisation.getAgence().getIdAgence())))
                .toList();
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

    private BigDecimal decimalOrZero(Map<String, Object> payload, String key) {
        return payload.get(key) == null ? BigDecimal.ZERO : new BigDecimal(payload.get(key).toString());
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
