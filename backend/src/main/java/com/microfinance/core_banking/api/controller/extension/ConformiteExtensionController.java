package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.ConsulterBicRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerAlerteConformiteRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerRapportConformiteRequestDTO;
import com.microfinance.core_banking.dto.request.extension.GenererRapportFiscalRequestDTO;
import com.microfinance.core_banking.dto.request.extension.GenererRapportPrudentielRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RescannerClientRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.AlerteConformiteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RapportReglementaireResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.AlerteConformite;
import com.microfinance.core_banking.entity.RapportReglementaire;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conformite")
public class ConformiteExtensionController {

    private final ConformiteExtensionService conformiteExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public ConformiteExtensionController(ConformiteExtensionService conformiteExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.conformiteExtensionService = conformiteExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/alertes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "COMPLIANCE_ALERT_CREATE", resource = "ALERTE_CONFORMITE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerAlerte(@Valid @RequestBody CreerAlerteConformiteRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_ALERTE_CONFORMITE", "ALERTE_CONFORMITE", null, dto, "Creation alerte conformite soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_VIEW)")
    public ResponseEntity<List<AlerteConformiteResponseDTO>> listerAlertes() {
        return ResponseEntity.ok(conformiteExtensionService.listerAlertes().stream().map(this::toAlerteDto).toList());
    }

    @PostMapping("/rapports")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "REGULATORY_REPORT_CREATE", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerRapport(@Valid @RequestBody CreerRapportConformiteRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_RAPPORT_REGLEMENTAIRE", "RAPPORT_REGLEMENTAIRE", null, dto, "Creation rapport reglementaire soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @GetMapping("/rapports")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_VIEW)")
    public ResponseEntity<List<RapportReglementaireResponseDTO>> listerRapports() {
        return ResponseEntity.ok(conformiteExtensionService.listerRapports().stream().map(this::toRapportDto).toList());
    }

    @PostMapping("/clients/{idClient}/rescan")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "COMPLIANCE_CLIENT_RESCAN", resource = "ALERTE_CONFORMITE")
    public ResponseEntity<ActionEnAttenteResponseDTO> rescannerClient(@PathVariable Long idClient, @Valid @RequestBody RescannerClientRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RESCAN_CLIENT_CONFORMITE", "ALERTE_CONFORMITE", String.valueOf(idClient), dto, "Rescan client soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @PostMapping("/transactions/{idTransaction}/rescan")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "COMPLIANCE_TRANSACTION_RESCAN", resource = "ALERTE_CONFORMITE")
    public ResponseEntity<ActionEnAttenteResponseDTO> rescannerTransaction(@PathVariable Long idTransaction) {
        CreerAlerteConformiteRequestDTO dto = new CreerAlerteConformiteRequestDTO();
        dto.setDescription(String.valueOf(idTransaction));
        ActionEnAttente action = pendingActionSubmissionService.submit("RESCAN_TRANSACTION_CONFORMITE", "ALERTE_CONFORMITE", String.valueOf(idTransaction), dto, "Rescan transaction soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @PostMapping("/bic/consultations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "COMPLIANCE_BIC_INQUIRY", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<ActionEnAttenteResponseDTO> consulterBic(@Valid @RequestBody ConsulterBicRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("BIC_CONSULTATION", "RAPPORT_REGLEMENTAIRE", null, dto, "Consultation BIC soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @PostMapping("/rapports/prudentiels")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "REGULATORY_PRUDENTIAL_REPORT", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<ActionEnAttenteResponseDTO> genererRapportPrudentiel(@Valid @RequestBody GenererRapportPrudentielRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RAPPORT_PRUDENTIEL", "RAPPORT_REGLEMENTAIRE", null, dto, "Rapport prudentiel soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    @PostMapping("/rapports/fiscalite")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_COMPLIANCE_MANAGE)")
    @AuditLog(action = "REGULATORY_FISCAL_REPORT", resource = "RAPPORT_REGLEMENTAIRE")
    public ResponseEntity<ActionEnAttenteResponseDTO> genererRapportFiscal(@Valid @RequestBody GenererRapportFiscalRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("RAPPORT_FISCAL", "RAPPORT_REGLEMENTAIRE", null, dto, "Rapport fiscal soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionEnAttenteDto(action));
    }

    private ActionEnAttenteResponseDTO toActionEnAttenteDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setReferenceRessource(action.getReferenceRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }

    private AlerteConformiteResponseDTO toAlerteDto(AlerteConformite alerte) {
        AlerteConformiteResponseDTO dto = new AlerteConformiteResponseDTO();
        dto.setIdAlerteConformite(alerte.getIdAlerteConformite());
        dto.setReferenceAlerte(alerte.getReferenceAlerte());
        dto.setTypeAlerte(alerte.getTypeAlerte());
        dto.setNiveauRisque(alerte.getNiveauRisque());
        dto.setStatut(alerte.getStatut());
        dto.setResume(alerte.getResume());
        dto.setDateDetection(alerte.getDateDetection());
        return dto;
    }

    private RapportReglementaireResponseDTO toRapportDto(RapportReglementaire rapport) {
        RapportReglementaireResponseDTO dto = new RapportReglementaireResponseDTO();
        dto.setIdRapportReglementaire(rapport.getIdRapportReglementaire());
        dto.setCodeRapport(rapport.getCodeRapport());
        dto.setTypeRapport(rapport.getTypeRapport());
        dto.setPeriode(rapport.getPeriode());
        dto.setStatut(rapport.getStatut());
        dto.setCheminFichier(rapport.getCheminFichier());
        dto.setDateGeneration(rapport.getDateGeneration());
        return dto;
    }
}
