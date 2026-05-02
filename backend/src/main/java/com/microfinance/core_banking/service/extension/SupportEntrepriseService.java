package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerBudgetServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCommandeServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerFournisseurServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerImmobilisationServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.GenererBulletinPaieServiceRequestDTO;
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
    public BudgetExploitation creerBudget(CreerBudgetServiceRequestDTO dto) {
        BudgetExploitation budget = new BudgetExploitation();
        budget.setCodeBudget(dto.getCodeBudget() == null ? "BDG-" + randomSuffix() : dto.getCodeBudget());
        budget.setAnnee(Integer.valueOf(dto.getAnnee()));
        budget.setMontantTotal(dto.getMontantTotal());
        budget.setStatut(dto.getStatut());
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(dto.getIdAgence()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            budget.setAgence(agence);
        }
        BudgetExploitation saved = budgetExploitationRepository.save(budget);

        List<Map<String, Object>> lignes = dto.getLignes();
        if (lignes != null) {
            for (Map<String, Object> lignePayload : lignes) {
                LigneBudget ligneBudget = new LigneBudget();
                ligneBudget.setBudget(saved);
                ligneBudget.setRubrique(String.valueOf(lignePayload.get("rubrique")));
                ligneBudget.setMontantPrevu(lignePayload.get("montantPrevu") == null ? BigDecimal.ZERO : new BigDecimal(lignePayload.get("montantPrevu").toString()));
                ligneBudget.setMontantEngage(lignePayload.get("montantEngage") == null ? BigDecimal.ZERO : new BigDecimal(lignePayload.get("montantEngage").toString()));
                ligneBudget.setMontantConsomme(lignePayload.get("montantConsomme") == null ? BigDecimal.ZERO : new BigDecimal(lignePayload.get("montantConsomme").toString()));
                ligneBudgetRepository.save(ligneBudget);
            }
        }
        return saved;
    }

    @Transactional
    public Fournisseur creerFournisseur(CreerFournisseurServiceRequestDTO dto) {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setCodeFournisseur(dto.getCodeFournisseur() == null ? "FRN-" + randomSuffix() : dto.getCodeFournisseur());
        fournisseur.setNom(dto.getNom());
        fournisseur.setContact(dto.getContact());
        fournisseur.setTelephone(dto.getTelephone());
        fournisseur.setEmail(dto.getEmail());
        fournisseur.setStatut(dto.getStatut());
        return fournisseurRepository.save(fournisseur);
    }

    @Transactional
    public CommandeAchat creerCommandeAchat(CreerCommandeServiceRequestDTO dto) {
        Fournisseur fournisseur = fournisseurRepository.findById(Long.valueOf(dto.getIdFournisseur()))
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable"));
        CommandeAchat commandeAchat = new CommandeAchat();
        commandeAchat.setReferenceCommande(dto.getReferenceCommande() == null ? "CDA-" + randomSuffix() : dto.getReferenceCommande());
        commandeAchat.setFournisseur(fournisseur);
        commandeAchat.setObjet(dto.getObjet());
        commandeAchat.setMontant(dto.getMontant());
        commandeAchat.setDateCommande(dto.getDateCommande() == null ? LocalDate.now() : LocalDate.parse(dto.getDateCommande()));
        commandeAchat.setStatut(dto.getStatut());
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(dto.getIdAgence()))
                    .orElseThrow(() -> new EntityNotFoundException("Agence introuvable"));
            authenticatedUserService.assertAgencyAccess(agence.getIdAgence());
            commandeAchat.setAgence(agence);
        }
        return commandeAchatRepository.save(commandeAchat);
    }

    @Transactional
    public BulletinPaie genererBulletinPaie(GenererBulletinPaieServiceRequestDTO dto) {
        Employe employe = employeRepository.findById(Long.valueOf(dto.getIdEmploye()))
                .orElseThrow(() -> new EntityNotFoundException("Employe introuvable"));
        if (employe.getAgence() != null) {
            authenticatedUserService.assertAgencyAccess(employe.getAgence().getIdAgence());
        }
        BulletinPaie bulletinPaie = new BulletinPaie();
        bulletinPaie.setEmploye(employe);
        bulletinPaie.setPeriode(dto.getPeriode());
        bulletinPaie.setSalaireBrut(dto.getSalaireBrut());
        bulletinPaie.setRetenues(dto.getRetenues());
        bulletinPaie.setSalaireNet(dto.getSalaireNet() == null
                ? bulletinPaie.getSalaireBrut().subtract(bulletinPaie.getRetenues())
                : dto.getSalaireNet());
        bulletinPaie.setStatut(dto.getStatut());
        return bulletinPaieRepository.save(bulletinPaie);
    }

    @Transactional
    public Immobilisation creerImmobilisation(CreerImmobilisationServiceRequestDTO dto) {
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setCodeImmobilisation(dto.getCodeImmobilisation() == null ? "IMM-" + randomSuffix() : dto.getCodeImmobilisation());
        immobilisation.setLibelle(dto.getLibelle());
        immobilisation.setValeurOrigine(dto.getValeurOrigine());
        immobilisation.setDureeAmortissementMois(Integer.valueOf(dto.getDureeAmortissementMois()));
        immobilisation.setAmortissementMensuel(immobilisation.getValeurOrigine()
                .divide(BigDecimal.valueOf(immobilisation.getDureeAmortissementMois()), 2, RoundingMode.HALF_UP));
        immobilisation.setValeurNette(dto.getValeurNette() == null ? immobilisation.getValeurOrigine() : dto.getValeurNette());
        immobilisation.setDateAcquisition(dto.getDateAcquisition() == null ? LocalDate.now() : LocalDate.parse(dto.getDateAcquisition()));
        immobilisation.setStatut(dto.getStatut());
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(Long.valueOf(dto.getIdAgence()))
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

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
