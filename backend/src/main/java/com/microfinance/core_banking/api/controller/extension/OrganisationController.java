package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.AffecterUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerCompteLiaisonRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerGuichetRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerMutationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerParametreAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerRegionRequestDTO;
import com.microfinance.core_banking.dto.request.extension.OperationDeplaceeRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RapprochementInterAgenceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ValiderMutationRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.AgenceResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CommissionInterAgenceResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CompteLiaisonResponseDTO;
import com.microfinance.core_banking.dto.response.extension.GuichetResponseDTO;
import com.microfinance.core_banking.dto.response.extension.MutationPersonnelResponseDTO;
import com.microfinance.core_banking.dto.response.extension.OperationDeplaceeResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ParametreAgenceResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RapprochementResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RegionResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ReseauReportingDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Agence;
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

import java.util.List;

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
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "REGION_CREATE", resource = "REGION")
    public ResponseEntity<Region> creerRegion(@Valid @RequestBody CreerRegionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.creerRegion(dto));
    }

    @GetMapping("/regions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<RegionResponseDTO>> listerRegions() {
        return ResponseEntity.ok(organisationService.listerRegions().stream().map(this::toRegionDto).toList());
    }

    @PostMapping("/agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "AGENCE_CREATE", resource = "AGENCE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerAgence(@Valid @RequestBody CreerAgenceRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_AGENCE", "AGENCE", dto.getCodeAgence(), dto, "Creation agence soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<AgenceResponseDTO>> listerAgences() {
        return ResponseEntity.ok(organisationService.listerAgences().stream().map(this::toAgenceDto).toList());
    }

    @PostMapping("/guichets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "GUICHET_CREATE", resource = "GUICHET")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerGuichet(@Valid @RequestBody CreerGuichetRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GUICHET", "GUICHET", dto.getCodeGuichet(), dto, "Creation guichet soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/guichets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<GuichetResponseDTO>> listerGuichets() {
        return ResponseEntity.ok(organisationService.listerGuichets().stream().map(this::toGuichetDto).toList());
    }

    @PostMapping("/affectations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "USER_AGENCY_ASSIGN", resource = "AFFECTATION")
    public ResponseEntity<ActionEnAttenteResponseDTO> affecterUtilisateur(@Valid @RequestBody AffecterUtilisateurRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("ASSIGN_USER_AGENCE", "AFFECTATION", String.valueOf(dto.getIdUtilisateur()), dto, "Affectation agence soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @PostMapping("/parametres-agence")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "AGENCY_PARAMETER_CREATE", resource = "PARAMETRE_AGENCE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerParametreAgence(@Valid @RequestBody CreerParametreAgenceRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PARAMETRE_AGENCE", "PARAMETRE_AGENCE", dto.getCodeParametre(), dto, "Parametrage agence soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/parametres-agence")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<ParametreAgenceResponseDTO>> listerParametresAgence() {
        return ResponseEntity.ok(organisationService.listerParametresAgence().stream().map(this::toParametreAgenceDto).toList());
    }

    @PostMapping("/mutations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "STAFF_MUTATION_CREATE", resource = "MUTATION_PERSONNEL")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerMutation(@Valid @RequestBody CreerMutationRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_MUTATION_PERSONNEL", "MUTATION_PERSONNEL", String.valueOf(dto.getIdEmploye()), dto, "Mutation personnel soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @PutMapping("/mutations/{idMutation}/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "STAFF_MUTATION_DECISION", resource = "MUTATION_PERSONNEL")
    public ResponseEntity<MutationPersonnelResponseDTO> validerMutation(@PathVariable Long idMutation, @Valid @RequestBody ValiderMutationRequestDTO dto) {
        return ResponseEntity.ok(toMutationDto(organisationService.validerMutationPersonnel(idMutation, dto)));
    }

    @GetMapping("/mutations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<MutationPersonnelResponseDTO>> listerMutations() {
        return ResponseEntity.ok(organisationService.listerMutations().stream().map(this::toMutationDto).toList());
    }

    @PostMapping("/comptes-liaison")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "INTER_AGENCY_LIAISON_CREATE", resource = "COMPTE_LIAISON_AGENCE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCompteLiaison(@Valid @RequestBody CreerCompteLiaisonRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMPTE_LIAISON_AGENCE", "COMPTE_LIAISON_AGENCE", dto.getIdAgenceSource() + "->" + dto.getIdAgenceDestination(), dto, "Compte de liaison inter-agences soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/comptes-liaison")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<CompteLiaisonResponseDTO>> listerComptesLiaison() {
        return ResponseEntity.ok(organisationService.listerComptesLiaison().stream().map(this::toCompteLiaisonDto).toList());
    }

    @PostMapping("/operations-deplacees")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "MOVED_OPERATION_RECORD", resource = "OPERATION_DEPLACEE")
    public ResponseEntity<OperationDeplaceeResponseDTO> enregistrerOperationDeplacee(@Valid @RequestBody OperationDeplaceeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toOperationDeplaceeDto(organisationService.enregistrerOperationDeplacee(dto)));
    }

    @GetMapping("/operations-deplacees")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<OperationDeplaceeResponseDTO>> listerOperationsDeplacees() {
        return ResponseEntity.ok(organisationService.listerOperationsDeplacees().stream().map(this::toOperationDeplaceeDto).toList());
    }

    @GetMapping("/commissions-inter-agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<CommissionInterAgenceResponseDTO>> listerCommissionsInterAgences() {
        return ResponseEntity.ok(organisationService.listerCommissionsInterAgences().stream().map(this::toCommissionDto).toList());
    }

    @PostMapping("/rapprochements-inter-agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "INTER_AGENCY_RECONCILIATION_CREATE", resource = "RAPPROCHEMENT_INTER_AGENCE")
    public ResponseEntity<RapprochementResponseDTO> rapprocherInterAgences(@Valid @RequestBody RapprochementInterAgenceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toRapprochementDto(organisationService.rapprocherInterAgences(dto)));
    }

    @GetMapping("/rapprochements-inter-agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<RapprochementResponseDTO>> listerRapprochementsInterAgences() {
        return ResponseEntity.ok(organisationService.listerRapprochementsInterAgences().stream().map(this::toRapprochementDto).toList());
    }

    @GetMapping("/reporting/reseau")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    public ResponseEntity<List<ReseauReportingDTO>> reportingReseau() {
        return ResponseEntity.ok(organisationService.reportingPerformanceReseau());
    }

    private RegionResponseDTO toRegionDto(Region region) {
        RegionResponseDTO dto = new RegionResponseDTO();
        dto.setIdRegion(region.getIdRegion());
        dto.setCodeRegion(region.getCodeRegion());
        dto.setNomRegion(region.getNomRegion());
        dto.setStatut(region.getStatut());
        return dto;
    }

    private AgenceResponseDTO toAgenceDto(Agence agence) {
        AgenceResponseDTO dto = new AgenceResponseDTO();
        dto.setIdAgence(agence.getIdAgence());
        dto.setCodeAgence(agence.getCodeAgence());
        dto.setNomAgence(agence.getNomAgence());
        dto.setAdresse(agence.getAdresse());
        dto.setTelephone(agence.getTelephone());
        dto.setStatut(agence.getStatut());
        dto.setNomRegion(agence.getRegion() == null ? null : agence.getRegion().getNomRegion());
        return dto;
    }

    private GuichetResponseDTO toGuichetDto(Guichet guichet) {
        GuichetResponseDTO dto = new GuichetResponseDTO();
        dto.setIdGuichet(guichet.getIdGuichet());
        dto.setCodeGuichet(guichet.getCodeGuichet());
        dto.setNomGuichet(guichet.getNomGuichet());
        dto.setStatut(guichet.getStatut());
        dto.setNomAgence(guichet.getAgence() == null ? null : guichet.getAgence().getNomAgence());
        return dto;
    }

    private ParametreAgenceResponseDTO toParametreAgenceDto(ParametreAgence parametre) {
        ParametreAgenceResponseDTO dto = new ParametreAgenceResponseDTO();
        dto.setIdParametreAgence(parametre.getIdParametreAgence());
        dto.setIdAgence(parametre.getAgence().getIdAgence());
        dto.setCodeAgence(parametre.getAgence().getCodeAgence());
        dto.setCodeParametre(parametre.getCodeParametre());
        dto.setValeurParametre(parametre.getValeurParametre());
        dto.setTypeValeur(parametre.getTypeValeur());
        dto.setDateEffet(parametre.getDateEffet());
        dto.setDateFin(parametre.getDateFin());
        dto.setVersionParametre(parametre.getVersionParametre());
        dto.setActif(parametre.getActif());
        return dto;
    }

    private MutationPersonnelResponseDTO toMutationDto(MutationPersonnel mutation) {
        MutationPersonnelResponseDTO dto = new MutationPersonnelResponseDTO();
        dto.setIdMutationPersonnel(mutation.getIdMutationPersonnel());
        dto.setIdEmploye(mutation.getEmploye().getIdEmploye());
        dto.setAgenceSource(mutation.getAgenceSource().getCodeAgence());
        dto.setAgenceDestination(mutation.getAgenceDestination().getCodeAgence());
        dto.setDateMutation(mutation.getDateMutation());
        dto.setMotif(mutation.getMotif());
        dto.setStatut(mutation.getStatut());
        dto.setIdValidateur(mutation.getValidateur() == null ? null : mutation.getValidateur().getIdUser());
        dto.setDateValidation(mutation.getDateValidation() != null ? mutation.getDateValidation().toLocalDate() : null);
        return dto;
    }

    private CompteLiaisonResponseDTO toCompteLiaisonDto(CompteLiaisonAgence compteLiaison) {
        CompteLiaisonResponseDTO dto = new CompteLiaisonResponseDTO();
        dto.setIdCompteLiaisonAgence(compteLiaison.getIdCompteLiaisonAgence());
        dto.setAgenceSource(compteLiaison.getAgenceSource().getCodeAgence());
        dto.setAgenceDestination(compteLiaison.getAgenceDestination().getCodeAgence());
        dto.setIdCompteComptable(compteLiaison.getCompteComptable().getIdCompteComptable());
        dto.setNumeroCompteComptable(compteLiaison.getCompteComptable().getNumeroCompte());
        dto.setLibelle(compteLiaison.getLibelle());
        dto.setActif(compteLiaison.getActif());
        return dto;
    }

    private OperationDeplaceeResponseDTO toOperationDeplaceeDto(OperationDeplacee operationDeplacee) {
        OperationDeplaceeResponseDTO dto = new OperationDeplaceeResponseDTO();
        dto.setIdOperationDeplacee(operationDeplacee.getIdOperationDeplacee());
        dto.setIdTransaction(operationDeplacee.getTransaction().getIdTransaction());
        dto.setReferenceTransaction(operationDeplacee.getTransaction().getReferenceUnique());
        dto.setAgenceOrigine(operationDeplacee.getAgenceOrigine().getCodeAgence());
        dto.setAgenceOperante(operationDeplacee.getAgenceOperante().getCodeAgence());
        dto.setTypeOperation(operationDeplacee.getTypeOperation());
        dto.setMontant(operationDeplacee.getMontant());
        dto.setDevise(operationDeplacee.getDevise());
        dto.setReferenceOperation(operationDeplacee.getReferenceOperation());
        dto.setStatut(operationDeplacee.getStatut());
        dto.setDateOperation(operationDeplacee.getDateOperation());
        return dto;
    }

    private CommissionInterAgenceResponseDTO toCommissionDto(CommissionInterAgence commission) {
        CommissionInterAgenceResponseDTO dto = new CommissionInterAgenceResponseDTO();
        dto.setIdCommissionInterAgence(commission.getIdCommissionInterAgence());
        dto.setIdOperationDeplacee(commission.getOperationDeplacee().getIdOperationDeplacee());
        dto.setTauxCommission(commission.getTauxCommission());
        dto.setMontantCommission(commission.getMontantCommission());
        dto.setIdCompteComptable(commission.getCompteComptable() == null ? null : commission.getCompteComptable().getIdCompteComptable());
        dto.setStatut(commission.getStatut());
        dto.setDateCalcul(commission.getDateCalcul() != null ? commission.getDateCalcul().toLocalDate() : null);
        dto.setDateComptabilisation(commission.getDateComptabilisation());
        return dto;
    }

    private RapprochementResponseDTO toRapprochementDto(RapprochementInterAgence rapprochement) {
        RapprochementResponseDTO dto = new RapprochementResponseDTO();
        dto.setIdRapprochementInterAgence(rapprochement.getIdRapprochementInterAgence());
        dto.setAgenceSource(rapprochement.getAgenceSource().getCodeAgence());
        dto.setAgenceDestination(rapprochement.getAgenceDestination().getCodeAgence());
        dto.setPeriodeDebut(rapprochement.getPeriodeDebut());
        dto.setPeriodeFin(rapprochement.getPeriodeFin());
        dto.setMontantDebit(rapprochement.getMontantDebit());
        dto.setMontantCredit(rapprochement.getMontantCredit());
        dto.setEcart(rapprochement.getEcart());
        dto.setStatut(rapprochement.getStatut());
        dto.setIdValidateur(rapprochement.getValidateur() == null ? null : rapprochement.getValidateur().getIdUser());
        dto.setDateRapprochement(rapprochement.getDateRapprochement());
        dto.setCommentaire(rapprochement.getCommentaire());
        return dto;
    }

    private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
