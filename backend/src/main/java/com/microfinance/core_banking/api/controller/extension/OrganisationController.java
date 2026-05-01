package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.AffectationUtilisateurAgence;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.CommissionInterAgence;
import com.microfinance.core_banking.entity.CompteLiaisonAgence;
import com.microfinance.core_banking.entity.Guichet;
import com.microfinance.core_banking.entity.MutationPersonnel;
import com.microfinance.core_banking.entity.OperationDeplacee;
import com.microfinance.core_banking.entity.ParametreAgence;
import com.microfinance.core_banking.entity.Region;
import com.microfinance.core_banking.entity.RapprochementInterAgence;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organisation")
public class OrganisationController {

    private final OrganisationService organisationService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public OrganisationController(OrganisationService organisationService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.organisationService = organisationService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/regions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "REGION_CREATE", resource = "REGION")
    public ResponseEntity<Map<String, Object>> creerRegion(@Valid @RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toRegionMap(organisationService.creerRegion(payload)));
    }

    @GetMapping("/regions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerRegions() {
        return ResponseEntity.ok(organisationService.listerRegions().stream().map(this::toRegionMap).toList());
    }

    @PostMapping("/agences")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "AGENCE_CREATE", resource = "AGENCE")
    public ResponseEntity<Map<String, Object>> creerAgence(@Valid @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_AGENCE", "AGENCE", (String) payload.get("codeAgence"), payload, "Creation agence soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/agences")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerAgences() {
        return ResponseEntity.ok(organisationService.listerAgences().stream().map(this::toAgenceMap).toList());
    }

    @PostMapping("/guichets")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "GUICHET_CREATE", resource = "GUICHET")
    public ResponseEntity<Map<String, Object>> creerGuichet(@Valid @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GUICHET", "GUICHET", (String) payload.get("codeGuichet"), payload, "Creation guichet soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/guichets")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerGuichets() {
        return ResponseEntity.ok(organisationService.listerGuichets().stream().map(this::toGuichetMap).toList());
    }

    @PostMapping("/affectations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "USER_AGENCY_ASSIGN", resource = "AFFECTATION")
    public ResponseEntity<Map<String, Object>> affecterUtilisateur(@Valid @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("ASSIGN_USER_AGENCE", "AFFECTATION", String.valueOf(payload.get("idUtilisateur")), payload, "Affectation agence soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/parametres-agence")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "AGENCY_PARAMETER_CREATE", resource = "PARAMETRE_AGENCE")
    public ResponseEntity<Map<String, Object>> creerParametreAgence(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PARAMETRE_AGENCE", "PARAMETRE_AGENCE", (String) payload.get("codeParametre"), payload, "Parametrage agence soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/parametres-agence")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerParametresAgence() {
        return ResponseEntity.ok(organisationService.listerParametresAgence().stream().map(this::toParametreAgenceMap).toList());
    }

    @PostMapping("/mutations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "STAFF_MUTATION_CREATE", resource = "MUTATION_PERSONNEL")
    public ResponseEntity<Map<String, Object>> creerMutation(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_MUTATION_PERSONNEL", "MUTATION_PERSONNEL", String.valueOf(payload.get("idEmploye")), payload, "Mutation personnel soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PutMapping("/mutations/{idMutation}/decision")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "STAFF_MUTATION_DECISION", resource = "MUTATION_PERSONNEL")
    public ResponseEntity<Map<String, Object>> validerMutation(@PathVariable Long idMutation, @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(toMutationMap(organisationService.validerMutationPersonnel(idMutation, payload)));
    }

    @GetMapping("/mutations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerMutations() {
        return ResponseEntity.ok(organisationService.listerMutations().stream().map(this::toMutationMap).toList());
    }

    @PostMapping("/comptes-liaison")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "INTER_AGENCY_LIAISON_CREATE", resource = "COMPTE_LIAISON_AGENCE")
    public ResponseEntity<Map<String, Object>> creerCompteLiaison(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMPTE_LIAISON_AGENCE", "COMPTE_LIAISON_AGENCE", payload.get("idAgenceSource") + "->" + payload.get("idAgenceDestination"), payload, "Compte de liaison inter-agences soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/comptes-liaison")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerComptesLiaison() {
        return ResponseEntity.ok(organisationService.listerComptesLiaison().stream().map(this::toCompteLiaisonMap).toList());
    }

    @PostMapping("/operations-deplacees")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_MANAGE')")
    @AuditLog(action = "MOVED_OPERATION_RECORD", resource = "OPERATION_DEPLACEE")
    public ResponseEntity<Map<String, Object>> enregistrerOperationDeplacee(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toOperationDeplaceeMap(organisationService.enregistrerOperationDeplacee(payload)));
    }

    @GetMapping("/operations-deplacees")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerOperationsDeplacees() {
        return ResponseEntity.ok(organisationService.listerOperationsDeplacees().stream().map(this::toOperationDeplaceeMap).toList());
    }

    @GetMapping("/commissions-inter-agences")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerCommissionsInterAgences() {
        return ResponseEntity.ok(organisationService.listerCommissionsInterAgences().stream().map(this::toCommissionMap).toList());
    }

    @PostMapping("/rapprochements-inter-agences")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_MANAGE')")
    @AuditLog(action = "INTER_AGENCY_RECONCILIATION_CREATE", resource = "RAPPROCHEMENT_INTER_AGENCE")
    public ResponseEntity<Map<String, Object>> rapprocherInterAgences(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toRapprochementMap(organisationService.rapprocherInterAgences(payload)));
    }

    @GetMapping("/rapprochements-inter-agences")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerRapprochementsInterAgences() {
        return ResponseEntity.ok(organisationService.listerRapprochementsInterAgences().stream().map(this::toRapprochementMap).toList());
    }

    @GetMapping("/reporting/reseau")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','ORGANIZATION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> reportingReseau() {
        return ResponseEntity.ok(organisationService.reportingPerformanceReseau());
    }

    private Map<String, Object> toRegionMap(Region region) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idRegion", region.getIdRegion());
        response.put("codeRegion", region.getCodeRegion());
        response.put("nomRegion", region.getNomRegion());
        response.put("statut", region.getStatut());
        return response;
    }

    private Map<String, Object> toAgenceMap(Agence agence) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idAgence", agence.getIdAgence());
        response.put("codeAgence", agence.getCodeAgence());
        response.put("nomAgence", agence.getNomAgence());
        response.put("adresse", agence.getAdresse());
        response.put("telephone", agence.getTelephone());
        response.put("statut", agence.getStatut());
        response.put("region", agence.getRegion() == null ? null : agence.getRegion().getNomRegion());
        return response;
    }

    private Map<String, Object> toGuichetMap(Guichet guichet) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idGuichet", guichet.getIdGuichet());
        response.put("codeGuichet", guichet.getCodeGuichet());
        response.put("nomGuichet", guichet.getNomGuichet());
        response.put("statut", guichet.getStatut());
        response.put("agence", guichet.getAgence() == null ? null : guichet.getAgence().getNomAgence());
        return response;
    }

    private Map<String, Object> toAffectationMap(AffectationUtilisateurAgence affectation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idAffectation", affectation.getIdAffectation());
        response.put("idUtilisateur", affectation.getUtilisateur().getIdUser());
        response.put("agence", affectation.getAgence().getNomAgence());
        response.put("roleOperatoire", affectation.getRoleOperatoire());
        response.put("dateDebut", affectation.getDateDebut());
        response.put("dateFin", affectation.getDateFin());
        response.put("actif", affectation.getActif());
        return response;
    }

    private Map<String, Object> toParametreAgenceMap(ParametreAgence parametre) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idParametreAgence", parametre.getIdParametreAgence());
        response.put("idAgence", parametre.getAgence().getIdAgence());
        response.put("codeAgence", parametre.getAgence().getCodeAgence());
        response.put("codeParametre", parametre.getCodeParametre());
        response.put("valeurParametre", parametre.getValeurParametre());
        response.put("typeValeur", parametre.getTypeValeur());
        response.put("dateEffet", parametre.getDateEffet());
        response.put("dateFin", parametre.getDateFin());
        response.put("versionParametre", parametre.getVersionParametre());
        response.put("actif", parametre.getActif());
        return response;
    }

    private Map<String, Object> toMutationMap(MutationPersonnel mutation) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idMutationPersonnel", mutation.getIdMutationPersonnel());
        response.put("idEmploye", mutation.getEmploye().getIdEmploye());
        response.put("agenceSource", mutation.getAgenceSource().getCodeAgence());
        response.put("agenceDestination", mutation.getAgenceDestination().getCodeAgence());
        response.put("dateMutation", mutation.getDateMutation());
        response.put("motif", mutation.getMotif());
        response.put("statut", mutation.getStatut());
        response.put("idValidateur", mutation.getValidateur() == null ? null : mutation.getValidateur().getIdUser());
        response.put("dateValidation", mutation.getDateValidation());
        return response;
    }

    private Map<String, Object> toCompteLiaisonMap(CompteLiaisonAgence compteLiaison) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCompteLiaisonAgence", compteLiaison.getIdCompteLiaisonAgence());
        response.put("agenceSource", compteLiaison.getAgenceSource().getCodeAgence());
        response.put("agenceDestination", compteLiaison.getAgenceDestination().getCodeAgence());
        response.put("idCompteComptable", compteLiaison.getCompteComptable().getIdCompteComptable());
        response.put("numeroCompteComptable", compteLiaison.getCompteComptable().getNumeroCompte());
        response.put("libelle", compteLiaison.getLibelle());
        response.put("actif", compteLiaison.getActif());
        return response;
    }

    private Map<String, Object> toOperationDeplaceeMap(OperationDeplacee operationDeplacee) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idOperationDeplacee", operationDeplacee.getIdOperationDeplacee());
        response.put("idTransaction", operationDeplacee.getTransaction().getIdTransaction());
        response.put("referenceTransaction", operationDeplacee.getTransaction().getReferenceUnique());
        response.put("agenceOrigine", operationDeplacee.getAgenceOrigine().getCodeAgence());
        response.put("agenceOperante", operationDeplacee.getAgenceOperante().getCodeAgence());
        response.put("typeOperation", operationDeplacee.getTypeOperation());
        response.put("montant", operationDeplacee.getMontant());
        response.put("devise", operationDeplacee.getDevise());
        response.put("referenceOperation", operationDeplacee.getReferenceOperation());
        response.put("statut", operationDeplacee.getStatut());
        response.put("dateOperation", operationDeplacee.getDateOperation());
        return response;
    }

    private Map<String, Object> toCommissionMap(CommissionInterAgence commission) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idCommissionInterAgence", commission.getIdCommissionInterAgence());
        response.put("idOperationDeplacee", commission.getOperationDeplacee().getIdOperationDeplacee());
        response.put("tauxCommission", commission.getTauxCommission());
        response.put("montantCommission", commission.getMontantCommission());
        response.put("idCompteComptable", commission.getCompteComptable() == null ? null : commission.getCompteComptable().getIdCompteComptable());
        response.put("statut", commission.getStatut());
        response.put("dateCalcul", commission.getDateCalcul());
        response.put("dateComptabilisation", commission.getDateComptabilisation());
        return response;
    }

    private Map<String, Object> toRapprochementMap(RapprochementInterAgence rapprochement) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idRapprochementInterAgence", rapprochement.getIdRapprochementInterAgence());
        response.put("agenceSource", rapprochement.getAgenceSource().getCodeAgence());
        response.put("agenceDestination", rapprochement.getAgenceDestination().getCodeAgence());
        response.put("periodeDebut", rapprochement.getPeriodeDebut());
        response.put("periodeFin", rapprochement.getPeriodeFin());
        response.put("montantDebit", rapprochement.getMontantDebit());
        response.put("montantCredit", rapprochement.getMontantCredit());
        response.put("ecart", rapprochement.getEcart());
        response.put("statut", rapprochement.getStatut());
        response.put("idValidateur", rapprochement.getValidateur() == null ? null : rapprochement.getValidateur().getIdUser());
        response.put("dateRapprochement", rapprochement.getDateRapprochement());
        response.put("commentaire", rapprochement.getCommentaire());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("statut", action.getStatut());
        return response;
    }
}
