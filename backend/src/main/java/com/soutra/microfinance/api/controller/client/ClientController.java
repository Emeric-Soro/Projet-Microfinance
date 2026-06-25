package com.soutra.microfinance.api.controller.client;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.client.CreationClientRequestDTO;
import com.soutra.microfinance.dto.request.client.DecisionKycClientRequestDTO;
import com.soutra.microfinance.dto.request.client.MiseAJourKycClientRequestDTO;
import com.soutra.microfinance.dto.request.client.MiseAJourClientRequestDTO;
import com.soutra.microfinance.dto.response.client.ClientConfidentielResponseDTO;
import com.soutra.microfinance.dto.response.client.ClientResponseDTO;
import com.soutra.microfinance.dto.response.client.DocumentClientResponseDTO;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.DocumentClient;
import com.soutra.microfinance.mapper.ClientMapper;
import com.soutra.microfinance.service.client.ClientService;
import com.soutra.microfinance.service.client.DocumentClientService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clients", description = "API de gestion des clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final DocumentClientService documentClientService;

    public ClientController(ClientService clientService, ClientMapper clientMapper, DocumentClientService documentClientService) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
        this.documentClientService = documentClientService;
    }

    @Operation(
            summary = "Creer un client",
            description = "Cree un nouveau client a partir des informations du formulaire d'inscription"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees de creation invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit metier (email, telephone ou code deja utilise)")
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
            @ApiResponse(responseCode = "400", description = "Parametres de pagination invalides")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    public ResponseEntity<Page<ClientResponseDTO>> listerClients(
            @RequestParam(required = false) String recherche,
            @ParameterObject Pageable pageable
    ) {
        Page<Client> clients;
        if (recherche != null && !recherche.isBlank()) {
            clients = clientService.rechercherClients(recherche, pageable);
        } else {
            clients = clientService.listerClients(pageable);
        }
        Page<ClientResponseDTO> pageClients = clients.map(clientMapper::toResponseDTO);
        return ResponseEntity.ok(pageClients);
    }

    @Operation(
            summary = "Obtenir les details d'un client",
            description = "Retourne les informations detaillees d'un client a partir de son identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Details du client"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @GetMapping("/{idClient}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    public ResponseEntity<ClientResponseDTO> obtenirDetailsClient(@PathVariable Long idClient) {
        Client client = clientService.obtenirDetailsClient(idClient);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    @Operation(
            summary = "[ADMIN] Donnees confidentielles d'un client",
            description = "Retourne les donnees sensibles en clair (numero de piece d'identite non masque, etc.). " +
                    "Acces reserve aux administrateurs. Chaque acces est trace dans le journal d'audit."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donnees confidentielles du client"),
            @ApiResponse(responseCode = "403", description = "Acces refuse - role ADMIN requis"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @GetMapping("/{idClient}/confidentiel")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "CLIENT_CONFIDENTIEL_ACCESS", resource = "CLIENT")
    public ResponseEntity<ClientConfidentielResponseDTO> obtenirDonneesConfidentielles(@PathVariable Long idClient) {
        Client client = clientService.obtenirDetailsClient(idClient);
        return ResponseEntity.ok(clientMapper.toConfidentielDTO(client));
    }

    @Operation(
            summary = "Modifier le profil d'un client",
            description = "Met a jour toutes les informations civiles, de contact et d'identite d'un client (sans les pieces jointes)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil client mis a jour avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PutMapping("/{idClient}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CLIENT_PROFILE_UPDATE", resource = "CLIENT")
    public ResponseEntity<ClientResponseDTO> modifierProfilClient(
            @PathVariable Long idClient,
            @Valid @RequestBody MiseAJourClientRequestDTO requestDTO
    ) {
        Client client = clientService.modifierProfilClient(idClient, requestDTO);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    @Operation(
            summary = "Modifier le statut d'un client",
            description = "Met a jour le statut metier d'un client (ex: ACTIF, BLOQUE)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut client mis a jour"),
            @ApiResponse(responseCode = "400", description = "Nouveau statut invalide"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PutMapping("/{idClient}/statut")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
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
            @ApiResponse(responseCode = "400", description = "Donnees KYC invalides"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PutMapping("/{idClient}/kyc")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
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
            @ApiResponse(responseCode = "400", description = "Decision KYC invalide"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PutMapping("/{idClient}/kyc/decision")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "CLIENT_KYC_DECISION", resource = "CLIENT")
    public ResponseEntity<ClientResponseDTO> traiterDossierKyc(
            @PathVariable Long idClient,
            @Valid @RequestBody DecisionKycClientRequestDTO requestDTO
    ) {
        Client client = clientService.traiterDossierKyc(idClient, requestDTO);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }

    @Operation(
            summary = "Uploader un document client",
            description = "Upload un document (image ou PDF, max 5 Mo) rattache a un client"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document uploadé avec succes"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide ou manquant"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "413", description = "Fichier trop volumineux (> 5 Mo)"),
            @ApiResponse(responseCode = "415", description = "Type de fichier non supporte")
    })
    @PostMapping("/{idClient}/documents")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CLIENT_DOCUMENT_UPLOAD", resource = "CLIENT")
    public ResponseEntity<DocumentClientResponseDTO> uploadDocument(
            @PathVariable Long idClient,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(value = "categorie", required = false) String categorie
    ) {
        DocumentClient doc = documentClientService.uploadDocument(idClient, fichier, categorie, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentClientResponseDTO.fromEntity(doc));
    }
}
