package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.dto.request.client.CreationClientRequestDTO;
import com.microfinance.core_banking.dto.response.client.ClientResponseDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.mapper.ClientMapper;
import com.microfinance.core_banking.service.client.ClientService;
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
            @ApiResponse(responseCode = "400", description = "Donnees de creation invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit metier (email, telephone ou code deja utilise)")
    })
    @PostMapping
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
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @GetMapping("/{idClient}")
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
            @ApiResponse(responseCode = "400", description = "Nouveau statut invalide"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PutMapping("/{idClient}/statut")
    public ResponseEntity<ClientResponseDTO> modifierStatutClient(
            @PathVariable Long idClient,
            @RequestParam String nouveauStatut
    ) {
        Client client = clientService.modifierStatutClient(idClient, nouveauStatut);
        return ResponseEntity.ok(clientMapper.toResponseDTO(client));
    }
}
