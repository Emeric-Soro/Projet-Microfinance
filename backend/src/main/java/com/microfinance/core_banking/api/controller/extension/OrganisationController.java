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
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
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
@RequestMapping("/api/organisation")
@Tag(name = "Organisation", description = "API de gestion de la structure organisationnelle (régions, agences, guichets, affectations, mutations, inter-agences)")
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
    @Operation(summary = "Créer une région", description = "Enregistre une nouvelle région dans le système. Retourne l'entité région créée avec ses informations.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Région créée avec succès", content = @Content(schema = @Schema(implementation = Region.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - une région avec le même code existe déjà", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Region> creerRegion(@Valid @RequestBody CreerRegionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.creerRegion(dto));
    }

    @GetMapping("/regions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les régions", description = "Retourne la liste de toutes les régions enregistrées dans le système.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des régions récupérée avec succès", content = @Content(schema = @Schema(implementation = RegionResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<RegionResponseDTO>> listerRegions() {
        return ResponseEntity.ok(organisationService.listerRegions().stream().map(this::toRegionDto).toList());
    }

    @PostMapping("/agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "AGENCE_CREATE", resource = "AGENCE")
    @Operation(summary = "Créer une agence (Maker-Checker)", description = "Soumet une demande de création d'agence via le workflow Maker-Checker. La création effective nécessite une validation par un superviseur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création d'agence soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerAgence(@Valid @RequestBody CreerAgenceRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_AGENCE", "AGENCE", dto.getCodeAgence(), dto, "Creation agence soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les agences", description = "Retourne la liste de toutes les agences enregistrées dans le système avec leurs informations détaillées.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des agences récupérée avec succès", content = @Content(schema = @Schema(implementation = AgenceResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<AgenceResponseDTO>> listerAgences() {
        return ResponseEntity.ok(organisationService.listerAgences().stream().map(this::toAgenceDto).toList());
    }

    @PostMapping("/guichets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "GUICHET_CREATE", resource = "GUICHET")
    @Operation(summary = "Créer un guichet (Maker-Checker)", description = "Soumet une demande de création de guichet via le workflow Maker-Checker. La création effective nécessite une validation par un superviseur.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de guichet soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerGuichet(@Valid @RequestBody CreerGuichetRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GUICHET", "GUICHET", dto.getCodeGuichet(), dto, "Creation guichet soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/guichets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les guichets", description = "Retourne la liste de tous les guichets enregistrés dans le système avec leurs informations.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des guichets récupérée avec succès", content = @Content(schema = @Schema(implementation = GuichetResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<GuichetResponseDTO>> listerGuichets() {
        return ResponseEntity.ok(organisationService.listerGuichets().stream().map(this::toGuichetDto).toList());
    }

    @PostMapping("/affectations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "USER_AGENCY_ASSIGN", resource = "AFFECTATION")
    @Operation(summary = "Affecter un utilisateur à une agence (Maker-Checker)", description = "Soumet une demande d'affectation d'un utilisateur à une agence via le workflow Maker-Checker. L'affectation effective nécessite une validation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande d'affectation soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> affecterUtilisateur(@Valid @RequestBody AffecterUtilisateurRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("ASSIGN_USER_AGENCE", "AFFECTATION", String.valueOf(dto.getIdUtilisateur()), dto, "Affectation agence soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @PostMapping("/parametres-agence")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "AGENCY_PARAMETER_CREATE", resource = "PARAMETRE_AGENCE")
    @Operation(summary = "Créer un paramètre d'agence (Maker-Checker)", description = "Soumet une demande de création d'un paramètre pour une agence via le workflow Maker-Checker. La création effective nécessite une validation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de paramétrage soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerParametreAgence(@Valid @RequestBody CreerParametreAgenceRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PARAMETRE_AGENCE", "PARAMETRE_AGENCE", dto.getCodeParametre(), dto, "Parametrage agence soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/parametres-agence")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les paramètres d'agence", description = "Retourne la liste de tous les paramètres configurés pour les agences dans le système.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des paramètres d'agence récupérée avec succès", content = @Content(schema = @Schema(implementation = ParametreAgenceResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ParametreAgenceResponseDTO>> listerParametresAgence() {
        return ResponseEntity.ok(organisationService.listerParametresAgence().stream().map(this::toParametreAgenceDto).toList());
    }

    @PostMapping("/mutations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "STAFF_MUTATION_CREATE", resource = "MUTATION_PERSONNEL")
    @Operation(summary = "Créer une mutation de personnel (Maker-Checker)", description = "Soumet une demande de mutation d'un employé d'une agence source vers une agence destination via le workflow Maker-Checker.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de mutation soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerMutation(@Valid @RequestBody CreerMutationRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_MUTATION_PERSONNEL", "MUTATION_PERSONNEL", String.valueOf(dto.getIdEmploye()), dto, "Mutation personnel soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @PutMapping("/mutations/{idMutation}/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "STAFF_MUTATION_DECISION", resource = "MUTATION_PERSONNEL")
    @Operation(summary = "Valider ou rejeter une mutation", description = "Permet à un superviseur de valider ou rejeter une demande de mutation de personnel en attente. Le corps de la requête contient la décision et un motif éventuel.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Décision appliquée avec succès - mutation validée ou rejetée", content = @Content(schema = @Schema(implementation = MutationPersonnelResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Mutation non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<MutationPersonnelResponseDTO> validerMutation(@PathVariable Long idMutation, @Valid @RequestBody ValiderMutationRequestDTO dto) {
        return ResponseEntity.ok(toMutationDto(organisationService.validerMutationPersonnel(idMutation, dto)));
    }

    @GetMapping("/mutations")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les mutations de personnel", description = "Retourne la liste de toutes les demandes de mutation de personnel avec leur statut et leurs détails.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des mutations récupérée avec succès", content = @Content(schema = @Schema(implementation = MutationPersonnelResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<MutationPersonnelResponseDTO>> listerMutations() {
        return ResponseEntity.ok(organisationService.listerMutations().stream().map(this::toMutationDto).toList());
    }

    @PostMapping("/comptes-liaison")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "INTER_AGENCY_LIAISON_CREATE", resource = "COMPTE_LIAISON_AGENCE")
    @Operation(summary = "Créer un compte de liaison inter-agences (Maker-Checker)", description = "Soumet une demande de création d'un compte de liaison entre deux agences via le workflow Maker-Checker. Utilisé pour les opérations inter-agences.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Demande de création de compte de liaison soumise et en attente de validation", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerCompteLiaison(@Valid @RequestBody CreerCompteLiaisonRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_COMPTE_LIAISON_AGENCE", "COMPTE_LIAISON_AGENCE", dto.getIdAgenceSource() + "->" + dto.getIdAgenceDestination(), dto, "Compte de liaison inter-agences soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/comptes-liaison")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les comptes de liaison inter-agences", description = "Retourne la liste de tous les comptes de liaison configurés entre les agences du réseau.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des comptes de liaison récupérée avec succès", content = @Content(schema = @Schema(implementation = CompteLiaisonResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<CompteLiaisonResponseDTO>> listerComptesLiaison() {
        return ResponseEntity.ok(organisationService.listerComptesLiaison().stream().map(this::toCompteLiaisonDto).toList());
    }

    @PostMapping("/operations-deplacees")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "MOVED_OPERATION_RECORD", resource = "OPERATION_DEPLACEE")
    @Operation(summary = "Enregistrer une opération déplacée", description = "Enregistre une opération effectuée dans une agence différente de l'agence d'origine du client. Utilisé pour les opérations inter-agences.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Opération déplacée enregistrée avec succès", content = @Content(schema = @Schema(implementation = OperationDeplaceeResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<OperationDeplaceeResponseDTO> enregistrerOperationDeplacee(@Valid @RequestBody OperationDeplaceeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toOperationDeplaceeDto(organisationService.enregistrerOperationDeplacee(dto)));
    }

    @GetMapping("/operations-deplacees")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les opérations déplacées", description = "Retourne la liste des opérations enregistrées comme déplacées entre agences.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des opérations déplacées récupérée avec succès", content = @Content(schema = @Schema(implementation = OperationDeplaceeResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<OperationDeplaceeResponseDTO>> listerOperationsDeplacees() {
        return ResponseEntity.ok(organisationService.listerOperationsDeplacees().stream().map(this::toOperationDeplaceeDto).toList());
    }

    @GetMapping("/commissions-inter-agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les commissions inter-agences", description = "Retourne la liste des commissions calculées pour les opérations effectuées entre agences.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commissions inter-agences récupérée avec succès", content = @Content(schema = @Schema(implementation = CommissionInterAgenceResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<CommissionInterAgenceResponseDTO>> listerCommissionsInterAgences() {
        return ResponseEntity.ok(organisationService.listerCommissionsInterAgences().stream().map(this::toCommissionDto).toList());
    }

    @PostMapping("/rapprochements-inter-agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_MANAGE)")
    @AuditLog(action = "INTER_AGENCY_RECONCILIATION_CREATE", resource = "RAPPROCHEMENT_INTER_AGENCE")
    @Operation(summary = "Effectuer un rapprochement inter-agences", description = "Crée un nouveau rapprochement entre deux agences pour une période donnée, en comparant les montants débit et crédit pour détecter les écarts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rapprochement créé avec succès", content = @Content(schema = @Schema(implementation = RapprochementResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erreur de validation ou requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflit métier - un rapprochement existe déjà pour cette période", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RapprochementResponseDTO> rapprocherInterAgences(@Valid @RequestBody RapprochementInterAgenceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toRapprochementDto(organisationService.rapprocherInterAgences(dto)));
    }

    @GetMapping("/rapprochements-inter-agences")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Lister les rapprochements inter-agences", description = "Retourne la liste de tous les rapprochements inter-agences effectués, avec leur statut et les écarts constatés.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des rapprochements récupérée avec succès", content = @Content(schema = @Schema(implementation = RapprochementResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<RapprochementResponseDTO>> listerRapprochementsInterAgences() {
        return ResponseEntity.ok(organisationService.listerRapprochementsInterAgences().stream().map(this::toRapprochementDto).toList());
    }

    @GetMapping("/reporting/reseau")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_ORGANIZATION_VIEW)")
    @Operation(summary = "Reporting performance du réseau", description = "Retourne les indicateurs de performance pour l'ensemble du réseau d'agences (régions, agences, volumes d'activité).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Données de reporting récupérées avec succès", content = @Content(schema = @Schema(implementation = ReseauReportingDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
