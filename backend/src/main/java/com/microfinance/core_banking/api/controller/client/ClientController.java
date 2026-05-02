package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.client.CreationClientRequestDTO;
import com.microfinance.core_banking.dto.request.client.DecisionKycClientRequestDTO;
import com.microfinance.core_banking.dto.request.client.MiseAJourKycClientRequestDTO;
import com.microfinance.core_banking.dto.response.client.ClientResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.mapper.ClientMapper;
import com.microfinance.core_banking.service.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "API de gestion des clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    @Operation(
            summary = "Creer un client",
            description = "Cree un nouveau client a partir des informations du formulaire d'inscription"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees de creation invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflit metier (email, telephone ou code deja utilise)", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @AuditLog(action = "CLIENT_CREATE", resource = "CLIENT")
    public ResponseEntity<ClientResponseDTO> creerClient(@Valid @RequestBody CreationClientRequestDTO requestDTO) {
        Client client = clientMapper.toEntity(requestDTO);
        Client clientCree = clientService.creerClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toResponseDTO(clientCree));
    }

    @Operation(
            summary = "Lister les clients",
            description = "Retourne la liste paginee de tous les clients"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des clients"),
            @ApiResponse(responseCode = "400", description = "Parametres de pagination invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<Page<ClientResponseDTO>> listerClients(@ParameterObject Pageable pageable) {
        Page<ClientResponseDTO> pageClients = clientService.listerClients(pageable)
                .map(clientMapper::toResponseDTO);
        return ResponseEntity.ok(pageClients);
    }

    @Operation(
            summary = "Obtenir les details d'un client",
            description = "Retourne les informations detaillees d'un client a partir de son identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details du client"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{idClient}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    public ResponseEntity<ClientResponseDTO> obtenirDetailsClient(@PathVariable Long idClient) {
        Client client = clientService.obtenirDetailsClient(idClient);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    @Operation(
            summary = "Modifier le statut d'un client",
            description = "Met a jour le statut metier d'un client (ex: ACTIF, BLOQUE)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut client mis a jour"),
            @ApiResponse(responseCode = "400", description = "Nouveau statut invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{idClient}/statut")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "CLIENT_STATUS_UPDATE", resource = "CLIENT")
    public ResponseEntity<ClientResponseDTO> modifierStatutClient(
            @PathVariable Long idClient,
            @RequestParam String nouveauStatut
    ) {
        Client client = clientService.modifierStatutClient(idClient, nouveauStatut);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    @Operation(
            summary = "Soumettre ou mettre a jour le KYC",
            description = "Enregistre les pieces d'identite, justificatifs et informations conformite d'un client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dossier KYC mis a jour"),
            @ApiResponse(responseCode = "400", description = "Donnees KYC invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{idClient}/kyc")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "CLIENT_KYC_UPDATE", resource = "CLIENT")
    public ResponseEntity<ClientResponseDTO> mettreAJourKyc(
            @PathVariable Long idClient,
            @Valid @RequestBody MiseAJourKycClientRequestDTO requestDTO
    ) {
        Client client = clientService.mettreAJourKyc(idClient, requestDTO);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    @Operation(
            summary = "Traiter un dossier KYC",
            description = "Valide, retourne en revision ou rejette un dossier KYC avec niveau de risque"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Decision KYC enregistree"),
            @ApiResponse(responseCode = "400", description = "Decision KYC invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{idClient}/kyc/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @AuditLog(action = "CLIENT_KYC_DECISION", resource = "CLIENT")
    public ResponseEntity<ClientResponseDTO> traiterDossierKyc(
            @PathVariable Long idClient,
            @Valid @RequestBody DecisionKycClientRequestDTO requestDTO
    ) {
        Client client = clientService.traiterDossierKyc(idClient, requestDTO);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }
}
