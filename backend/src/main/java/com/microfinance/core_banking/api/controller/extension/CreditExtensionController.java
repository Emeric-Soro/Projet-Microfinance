package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerDemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerProduitCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DebloquerCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DeciderDemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EnregistrerGarantieRequestDTO;
import com.microfinance.core_banking.dto.request.extension.PassagePerteCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ReportEcheanceCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RembourserCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ReportEcheanceCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RestructurationCreditRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.DemandeCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EcheanceCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.GarantieCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ImpayeCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ProduitCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ProvisionCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RemboursementCreditResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.EcheanceCredit;
import com.microfinance.core_banking.entity.GarantieCredit;
import com.microfinance.core_banking.entity.ImpayeCredit;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.ProvisionCredit;
import com.microfinance.core_banking.entity.RemboursementCredit;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/credits")
@Tag(name = "Crédit", description = "API de gestion des crédits, produits, demandes, échéances, garanties, impayés et provisions")
public class CreditExtensionController {

    private final CreditExtensionService creditExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public CreditExtensionController(CreditExtensionService creditExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.creditExtensionService = creditExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(summary = "Créer un produit de crédit", description = "Crée un nouveau produit de crédit définissant les paramètres de prêt (taux, montants, durée). Transit par le workflow Maker-Checker pour validation avant activation. Une fois approuvé, le produit pourra être utilisé lors des demandes de crédit.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création produit crédit soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_PRODUCT_CREATE", resource = "PRODUIT_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerProduit(@Valid @RequestBody CreerProduitCreditRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PRODUIT_CREDIT", "PRODUIT_CREDIT", dto.getCodeProduit(), dto, "Creation produit credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les produits de crédit", description = "Retourne la liste de tous les produits de crédit actifs et inactifs. Permet aux chargés de crédit et superviseurs de consulter les paramètres des offres de prêt disponibles dans l'institution.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits retournée avec succès", content = @Content(schema = @Schema(implementation = ProduitCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<ProduitCreditResponseDTO>> listerProduits() {
        return ResponseEntity.ok(creditExtensionService.listerProduits().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Créer une demande de crédit", description = "Enregistre une nouvelle demande de crédit soumise par un client. La demande est créée avec un statut initial et doit transiter par les étapes d'analyse, décision et déblocage. Le dossier de crédit est automatiquement référencé et associé au produit sélectionné.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Demande de crédit créée avec succès", content = @Content(schema = @Schema(implementation = DemandeCreditResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Client introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/demandes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_REQUEST_CREATE", resource = "DEMANDE_CREDIT")
    public ResponseEntity<DemandeCreditResponseDTO> creerDemande(@Valid @RequestBody CreerDemandeCreditRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(creditExtensionService.creerDemande(dto)));
    }

    @Operation(summary = "Lister les demandes de crédit", description = "Retourne la liste de toutes les demandes de crédit enregistrées. Permet aux agents et superviseurs de suivre l'état d'avancement des dossiers depuis la soumission jusqu'à la décision finale.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des demandes retournée avec succès", content = @Content(schema = @Schema(implementation = DemandeCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/demandes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<DemandeCreditResponseDTO>> listerDemandes() {
        return ResponseEntity.ok(creditExtensionService.listerDemandes().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Décider une demande de crédit", description = "Permet d'approuver ou de rejeter une demande de crédit après analyse du dossier. La décision est soumise au workflow Maker-Checker pour validation par un superviseur. Un score de crédit et un avis du comité peuvent être renseignés. Effet comptable : la décision conditionne le déblocage ultérieur des fonds.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Décision soumise en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Demande introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - demande déjà traitée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/demandes/{idDemande}/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_REQUEST_DECISION", resource = "DEMANDE_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> deciderDemande(@PathVariable Long idDemande, @Valid @RequestBody DeciderDemandeCreditRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DECISION_DEMANDE_CREDIT", "DEMANDE_CREDIT", String.valueOf(idDemande), dto, "Decision demande credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Débloquer un crédit", description = "Débloque les fonds d'un crédit approuvé et génère les écritures comptables de décaissement. Transit par le workflow Maker-Checker pour validation. Effet comptable : création d'une écriture au débit du compte de prêt et au crédit du compte de trésorerie. Le capital est mis à disposition du client.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Déblocage soumis en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Demande introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - crédit déjà débloqué", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/demandes/{idDemande}/deblocage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_DISBURSE", resource = "CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> debloquerCredit(@PathVariable Long idDemande, @Valid @RequestBody DebloquerCreditRequestDTO dto) {
        dto.setIdDemande(idDemande);
        ActionEnAttente action = pendingActionSubmissionService.submit("DEBLOCAGE_CREDIT", "CREDIT", String.valueOf(idDemande), dto, "Deblocage credit soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Restructurer un crédit", description = "Soumet une demande de restructuration d'un crédit existant (révision des termes : durée, taux, mensualité). Transit par le workflow Maker-Checker pour approbation. Précondition : le crédit doit être en cours. Effet comptable : annulation des anciennes échéances et création d'un nouveau plan d'amortissement.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Restructuration soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - restructuration non applicable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{idCredit}/restructuration")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_RESTRUCTURE_SUBMIT", resource = "CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> restructurerCredit(@PathVariable Long idCredit, @Valid @RequestBody RestructurationCreditRequestDTO dto) {
        dto.setIdCredit(idCredit);
        ActionEnAttente action = pendingActionSubmissionService.submit("RESTRUCTURATION_CREDIT", "CREDIT", String.valueOf(idCredit), dto, "Restructuration credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Reporter une échéance de crédit", description = "Soumet un report d'échéance pour un crédit en cours, permettant de décaler la date d'une mensualité. Transit par le workflow Maker-Checker pour approbation. Précondition : l'échéance doit être impayée ou à venir. Effet comptable : modification du plan d'amortissement et impact sur les provisions.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Report d'échéance soumis en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit ou échéance introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - report non autorisé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{idCredit}/echeances/{idEcheanceCredit}/report")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_INSTALLMENT_DEFER_SUBMIT", resource = "ECHEANCE_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> reporterEcheance(@PathVariable Long idCredit, @PathVariable Long idEcheanceCredit, @Valid @RequestBody ReportEcheanceCreditRequestDTO dto) {
        dto.setIdCredit(idCredit);
        dto.setIdEcheanceCredit(idEcheanceCredit);
        ActionEnAttente action = pendingActionSubmissionService.submit("REPORT_ECHEANCE_CREDIT", "ECHEANCE_CREDIT", String.valueOf(idEcheanceCredit), dto, "Report d'echeance credit soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Passer un crédit en perte", description = "Soumet un passage en perte (write-off) d'un crédit jugé irrécouvrable. Transit par le workflow Maker-Checker pour validation. Précondition : le crédit doit être en situation d'impayé depuis la période réglementaire. Effet comptable : sortie du bilan du capital restant dû et constatation de la perte dans les comptes de résultat.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Passage en perte soumis en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - conditions de passage en perte non remplies", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{idCredit}/passage-perte")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_WRITE_OFF_SUBMIT", resource = "CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> passerEnPerte(@PathVariable Long idCredit, @Valid @RequestBody PassagePerteCreditRequestDTO dto) {
        dto.setIdCredit(idCredit);
        ActionEnAttente action = pendingActionSubmissionService.submit("PASSAGE_PERTE_CREDIT", "CREDIT", String.valueOf(idCredit), dto, "Passage en perte soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les crédits", description = "Retourne la liste de tous les crédits accordés dans l'institution. Permet de visualiser l'encours global, le statut de chaque crédit, le capital restant dû et les mensualités.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des crédits retournée avec succès", content = @Content(schema = @Schema(implementation = CreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<CreditResponseDTO>> listerCredits() {
        return ResponseEntity.ok(creditExtensionService.listerCredits().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Lister les échéances d'un crédit", description = "Retourne le calendrier prévisionnel des échéances d'un crédit avec le détail capital, intérêts et assurance. Permet de suivre le plan d'amortissement et l'état de chaque mensualité (payée, impayée, à venir).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des échéances retournée avec succès", content = @Content(schema = @Schema(implementation = EcheanceCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idCredit}/echeances")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<EcheanceCreditResponseDTO>> listerEcheances(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerEcheances(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Enregistrer une garantie", description = "Enregistre une garantie (sûreté réelle ou personnelle) associée à un crédit pour sécuriser le prêt. Transit par le workflow Maker-Checker pour validation. La valeur de la garantie et le type sont enregistrés pour le suivi des risques.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Création garantie soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/{idCredit}/garanties")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_COLLATERAL_CREATE", resource = "GARANTIE_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> enregistrerGarantie(@PathVariable Long idCredit, @Valid @RequestBody EnregistrerGarantieRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GARANTIE_CREDIT", "GARANTIE_CREDIT", String.valueOf(idCredit), dto, "Creation garantie credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les garanties d'un crédit", description = "Retourne la liste des garanties associées à un crédit avec leur type, description et valeur. Permet d'évaluer le niveau de couverture des sûretés pour chaque prêt accordé.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des garanties retournée avec succès", content = @Content(schema = @Schema(implementation = GarantieCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idCredit}/garanties")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<GarantieCreditResponseDTO>> listerGaranties(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerGaranties(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Rembourser un crédit", description = "Effectue un remboursement sur un crédit en répartissant le paiement entre le capital, les intérêts et l'assurance. Précondition : le crédit doit être en cours. Effet comptable : génération d'une écriture comptable au crédit du compte de trésorerie et au débit des comptes de créance et produits. Met à jour le plan d'amortissement.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Remboursement effectué avec succès", content = @Content(schema = @Schema(implementation = RemboursementCreditResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - montant supérieur au solde dû", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/{idCredit}/remboursements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_REPAYMENT_CREATE", resource = "REMBOURSEMENT_CREDIT")
    public ResponseEntity<RemboursementCreditResponseDTO> rembourserCredit(@PathVariable Long idCredit, @Valid @RequestBody RembourserCreditRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(creditExtensionService.rembourserCredit(idCredit, dto)));
    }

    @Operation(summary = "Lister les remboursements d'un crédit", description = "Retourne l'historique complet des remboursements effectués sur un crédit. Permet de consulter le détail de chaque versement (capital, intérêts, assurance) et la référence de transaction associée.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des remboursements retournée avec succès", content = @Content(schema = @Schema(implementation = RemboursementCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idCredit}/remboursements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<RemboursementCreditResponseDTO>> listerRemboursements(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerRemboursements(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Détecter les impayés", description = "Lance la détection automatique des impayés sur l'ensemble des crédits en cours à une date donnée. Transit par le workflow Maker-Checker pour validation. Effet comptable : génération des enregistrements d'impayés et calcul des pénalités de retard. Les impayés servent de base au calcul des provisions réglementaires.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Détection impayés soumise en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/impayes/detection")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_OVERDUE_DETECT", resource = "IMPAYE_CREDIT")
    public ResponseEntity<List<ActionEnAttenteResponseDTO>> detecterImpayes(@Valid @RequestBody DetecterImpayesRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DETECTION_IMPAYE_CREDIT", "IMPAYE_CREDIT", null, dto, "Detection impayes soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(List.of(toActionDto(action)));
    }

    @Operation(summary = "Lister les impayés d'un crédit", description = "Retourne la liste des impayés enregistrés pour un crédit avec le montant dû, les jours de retard et la classe de risque. Permet le suivi du portefeuille à risque et alimente le calcul des provisions.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des impayés retournée avec succès", content = @Content(schema = @Schema(implementation = ImpayeCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idCredit}/impayes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<ImpayeCreditResponseDTO>> listerImpayes(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerImpayes(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Calculer les provisions", description = "Lance le calcul des provisions réglementaires sur les crédits en fonction des impayés et de la classification des risques. Transit par le workflow Maker-Checker pour validation. Effet comptable : génération d'une écriture de provisionnement au débit des charges et au crédit des comptes de provisions. Met à jour le taux de couverture du portefeuille.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Calcul provisions soumis en attente", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/provisions/calcul")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_PROVISION_CALCULATE", resource = "PROVISION_CREDIT")
    public ResponseEntity<List<ActionEnAttenteResponseDTO>> calculerProvisions(@Valid @RequestBody CalculerProvisionsRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CALCUL_PROVISION_CREDIT", "PROVISION_CREDIT", null, dto, "Calcul provisions soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(List.of(toActionDto(action)));
    }

    @Operation(summary = "Lister les provisions d'un crédit", description = "Retourne l'historique des provisions constituées pour un crédit avec le taux appliqué, le montant provisionné et la référence de la pièce comptable. Permet d'auditer la couverture des risques de crédit.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des provisions retournée avec succès", content = @Content(schema = @Schema(implementation = ProvisionCreditResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refus - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Crédit introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idCredit}/provisions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<ProvisionCreditResponseDTO>> listerProvisions(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerProvisions(idCredit).stream().map(this::toDto).toList());
    }

    private ProduitCreditResponseDTO toDto(ProduitCredit produit) {
        ProduitCreditResponseDTO dto = new ProduitCreditResponseDTO();
        dto.setIdProduitCredit(produit.getIdProduitCredit());
        dto.setCodeProduit(produit.getCodeProduit());
        dto.setLibelle(produit.getLibelle());
        dto.setCategorie(produit.getCategorie());
        dto.setTauxAnnuel(produit.getTauxAnnuel());
        dto.setMontantMin(produit.getMontantMin());
        dto.setMontantMax(produit.getMontantMax());
        dto.setStatut(produit.getStatut());
        return dto;
    }

    private DemandeCreditResponseDTO toDto(DemandeCredit demande) {
        DemandeCreditResponseDTO dto = new DemandeCreditResponseDTO();
        dto.setIdDemandeCredit(demande.getIdDemandeCredit());
        dto.setReferenceDossier(demande.getReferenceDossier());
        dto.setIdClient(demande.getClient().getIdClient());
        dto.setProduit(demande.getProduitCredit().getLibelle());
        dto.setMontantDemande(demande.getMontantDemande());
        dto.setDureeMois(demande.getDureeMois());
        dto.setStatut(demande.getStatut());
        dto.setScoreCredit(demande.getScoreCredit());
        dto.setAvisComite(demande.getAvisComite());
        dto.setDecisionFinale(demande.getDecisionFinale());
        dto.setDateDecision(demande.getDateDecision());
        return dto;
    }

    private CreditResponseDTO toDto(Credit credit) {
        CreditResponseDTO dto = new CreditResponseDTO();
        dto.setIdCredit(credit.getIdCredit());
        dto.setReferenceCredit(credit.getReferenceCredit());
        dto.setIdClient(credit.getClient().getIdClient());
        dto.setMontantAccorde(credit.getMontantAccorde());
        dto.setTauxAnnuel(credit.getTauxAnnuel());
        dto.setMensualite(credit.getMensualite());
        dto.setCapitalRestantDu(credit.getCapitalRestantDu());
        dto.setFraisPreleves(credit.getFraisPreleves());
        dto.setReferenceTransactionDeblocage(credit.getReferenceTransactionDeblocage());
        dto.setStatut(credit.getStatut());
        return dto;
    }

    private EcheanceCreditResponseDTO toDto(EcheanceCredit echeance) {
        EcheanceCreditResponseDTO dto = new EcheanceCreditResponseDTO();
        dto.setIdEcheanceCredit(echeance.getIdEcheanceCredit());
        dto.setNumeroEcheance(echeance.getNumeroEcheance());
        dto.setDateEcheance(echeance.getDateEcheance());
        dto.setCapitalPrevu(echeance.getCapitalPrevu());
        dto.setInteretPrevu(echeance.getInteretPrevu());
        dto.setAssurancePrevue(echeance.getAssurancePrevue());
        dto.setTotalPrevu(echeance.getTotalPrevu());
        dto.setCapitalPaye(echeance.getCapitalPaye());
        dto.setInteretPaye(echeance.getInteretPaye());
        dto.setAssurancePayee(echeance.getAssurancePayee());
        dto.setStatut(echeance.getStatut());
        return dto;
    }

    private GarantieCreditResponseDTO toDto(GarantieCredit garantie) {
        GarantieCreditResponseDTO dto = new GarantieCreditResponseDTO();
        dto.setIdGarantieCredit(garantie.getIdGarantieCredit());
        dto.setTypeGarantie(garantie.getTypeGarantie());
        dto.setDescription(garantie.getDescription());
        dto.setValeur(garantie.getValeur());
        dto.setValeurNantie(garantie.getValeurNantie());
        dto.setStatut(garantie.getStatut());
        return dto;
    }

    private RemboursementCreditResponseDTO toDto(RemboursementCredit remboursement) {
        RemboursementCreditResponseDTO dto = new RemboursementCreditResponseDTO();
        dto.setIdRemboursementCredit(remboursement.getIdRemboursementCredit());
        dto.setReferenceRemboursement(remboursement.getReferenceRemboursement());
        dto.setMontant(remboursement.getMontant());
        dto.setCapitalPaye(remboursement.getCapitalPaye());
        dto.setInteretPaye(remboursement.getInteretPaye());
        dto.setAssurancePayee(remboursement.getAssurancePayee());
        dto.setReferenceTransaction(remboursement.getReferenceTransaction());
        dto.setDatePaiement(remboursement.getDatePaiement());
        dto.setStatut(remboursement.getStatut());
        return dto;
    }

    private ImpayeCreditResponseDTO toDto(ImpayeCredit impaye) {
        ImpayeCreditResponseDTO dto = new ImpayeCreditResponseDTO();
        dto.setIdImpayeCredit(impaye.getIdImpayeCredit());
        dto.setIdCredit(impaye.getCredit().getIdCredit());
        dto.setIdEcheanceCredit(impaye.getEcheanceCredit().getIdEcheanceCredit());
        dto.setMontant(impaye.getMontant());
        dto.setJoursRetard(impaye.getJoursRetard());
        dto.setClasseRisque(impaye.getClasseRisque());
        dto.setStatut(impaye.getStatut());
        return dto;
    }

    private ProvisionCreditResponseDTO toDto(ProvisionCredit provision) {
        ProvisionCreditResponseDTO dto = new ProvisionCreditResponseDTO();
        dto.setIdProvisionCredit(provision.getIdProvisionCredit());
        dto.setIdCredit(provision.getCredit().getIdCredit());
        dto.setDateCalcul(provision.getDateCalcul());
        dto.setTauxProvision(provision.getTauxProvision());
        dto.setMontantProvision(provision.getMontantProvision());
        dto.setReferencePieceComptable(provision.getReferencePieceComptable());
        dto.setStatut(provision.getStatut());
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
