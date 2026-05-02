package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerEmployeDigitalRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerPartenaireDigitalRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EnregistrerAppareilRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EnregistrerAppareilServiceRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.AppareilClientResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EmployeResponseDTO;
import com.microfinance.core_banking.dto.response.extension.PartenaireApiResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.AppareilClient;
import com.microfinance.core_banking.entity.Employe;
import com.microfinance.core_banking.entity.PartenaireApi;
import com.microfinance.core_banking.service.extension.DigitalExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
@Tag(name = "Extensions Digitales", description = "API de gestion des extensions digitales (appareils clients, partenaires API)")
public class DigitalExtensionController {

    private final DigitalExtensionService digitalExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public DigitalExtensionController(DigitalExtensionService digitalExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.digitalExtensionService = digitalExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/appareils-clients")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_DIGITAL_MANAGE)")
    @AuditLog(action = "CLIENT_DEVICE_REGISTER", resource = "APPAREIL_CLIENT")
    @Operation(summary = "Enregistrer un appareil client", description = "Enregistre un nouvel appareil associé à un client. L'appareil est identifié par son empreinte numérique et sa plateforme, et sera soumis à autorisation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appareil client enregistré avec succès", content = @Content(schema = @Schema(implementation = AppareilClientResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AppareilClientResponseDTO> enregistrerAppareil(@Valid @RequestBody EnregistrerAppareilRequestDTO dto) {
        EnregistrerAppareilServiceRequestDTO serviceDto = EnregistrerAppareilServiceRequestDTO.fromEnregistrerAppareilRequestDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(digitalExtensionService.enregistrerAppareil(serviceDto)));
    }

    @GetMapping("/appareils-clients")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_DIGITAL_VIEW)")
    @Operation(summary = "Lister les appareils clients", description = "Retourne la liste de tous les appareils clients enregistrés. Chaque appareil est identifiable par son empreinte, sa plateforme et son statut d'autorisation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des appareils clients", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppareilClientResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<AppareilClientResponseDTO>> listerAppareils() {
        return ResponseEntity.ok(digitalExtensionService.listerAppareils().stream().map(this::toDto).toList());
    }

    @PostMapping("/partenaires-api")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_DIGITAL_MANAGE)")
    @AuditLog(action = "API_PARTNER_CREATE", resource = "PARTENAIRE_API")
    @Operation(summary = "Créer un partenaire API", description = "Soumet la création d'un nouveau partenaire API au workflow Maker-Checker. Le partenaire est défini par son code, son nom, son type et ses quotas journaliers.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerPartenaire(@Valid @RequestBody CreerPartenaireDigitalRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PARTENAIRE_API", "PARTENAIRE_API", dto.getCodePartenaire(), dto, "Creation partenaire API soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/partenaires-api")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_DIGITAL_VIEW)")
    @Operation(summary = "Lister les partenaires API", description = "Retourne la liste de tous les partenaires API enregistrés. Chaque partenaire inclut son code, son nom, son type, son webhook et son statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des partenaires API", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PartenaireApiResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<PartenaireApiResponseDTO>> listerPartenaires() {
        return ResponseEntity.ok(digitalExtensionService.listerPartenaires().stream().map(this::toDto).toList());
    }

    @PostMapping("/employes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_DIGITAL_MANAGE)")
    @AuditLog(action = "EMPLOYEE_CREATE", resource = "EMPLOYE")
    @Operation(summary = "Créer un employé", description = "Soumet la création d'un nouvel employé au workflow Maker-Checker. L'employé est rattaché à une agence avec un matricule, un poste et un service.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerEmploye(@Valid @RequestBody CreerEmployeDigitalRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_EMPLOYE", "EMPLOYE", null, dto, "Creation employe soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/employes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_DIGITAL_VIEW)")
    @Operation(summary = "Lister les employés", description = "Retourne la liste de tous les employés enregistrés. Chaque employé affiche son matricule, son nom, son poste, son service, son agence et son statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des employés", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<EmployeResponseDTO>> listerEmployes() {
        return ResponseEntity.ok(digitalExtensionService.listerEmployes().stream().map(this::toDto).toList());
    }

    private AppareilClientResponseDTO toDto(AppareilClient appareil) {
        AppareilClientResponseDTO dto = new AppareilClientResponseDTO();
        dto.setIdAppareilClient(appareil.getIdAppareilClient());
        dto.setIdClient(appareil.getClient().getIdClient());
        dto.setEmpreinteAppareil(appareil.getEmpreinteAppareil());
        dto.setPlateforme(appareil.getPlateforme());
        dto.setNomAppareil(appareil.getNomAppareil());
        dto.setAutorise(appareil.getAutorise());
        dto.setDerniereConnexion(appareil.getDerniereConnexion());
        return dto;
    }

    private PartenaireApiResponseDTO toDto(PartenaireApi partenaire) {
        PartenaireApiResponseDTO dto = new PartenaireApiResponseDTO();
        dto.setIdPartenaireApi(partenaire.getIdPartenaireApi());
        dto.setCodePartenaire(partenaire.getCodePartenaire());
        dto.setNomPartenaire(partenaire.getNomPartenaire());
        dto.setTypePartenaire(partenaire.getTypePartenaire());
        dto.setWebhookUrl(partenaire.getWebhookUrl());
        dto.setStatut(partenaire.getStatut());
        dto.setQuotasJournaliers(partenaire.getQuotasJournaliers());
        return dto;
    }

    private EmployeResponseDTO toDto(Employe employe) {
        EmployeResponseDTO dto = new EmployeResponseDTO();
        dto.setIdEmploye(employe.getIdEmploye());
        dto.setMatricule(employe.getMatricule());
        dto.setNomComplet(employe.getNomComplet());
        dto.setPoste(employe.getPoste());
        dto.setService(employe.getService());
        dto.setStatut(employe.getStatut());
        dto.setAgence(employe.getAgence() == null ? null : employe.getAgence().getNomAgence());
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
