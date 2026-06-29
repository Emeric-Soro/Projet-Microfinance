package com.soutra.microfinance.api.controller.client;

import com.soutra.microfinance.dto.request.client.AddBlacklistRequestDTO;
import com.soutra.microfinance.dto.request.client.RemoveBlacklistRequestDTO;
import com.soutra.microfinance.dto.response.client.ClientBlacklistHistoryResponseDTO;
import com.soutra.microfinance.dto.response.client.ClientBlacklistResponseDTO;
import com.soutra.microfinance.entity.ClientBlacklist;
import com.soutra.microfinance.entity.ClientBlacklistHistory;
import com.soutra.microfinance.service.client.ClientBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.soutra.microfinance.audit.AuditLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client Blacklist", description = "Endpoints de gestion de la liste noire des clients exclus")
public class ClientBlacklistController {

    private final ClientBlacklistService clientBlacklistService;

    @Operation(summary = "Lister les clients blacklistes", description = "Retourne la liste des clients actuellement exclus")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des clients blacklistes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/blacklist")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    public ResponseEntity<Page<ClientBlacklistResponseDTO>> listerBlacklist(@ParameterObject Pageable pageable) {
        Page<ClientBlacklist> page = clientBlacklistService.listerBlacklist(pageable);
        Page<ClientBlacklistResponseDTO> dtos = page.map(bl -> new ClientBlacklistResponseDTO(
                bl.getClient().getIdClient(),
                bl.getClient().getIdClient(),
                bl.getClient().getCodeClient(),
                bl.getClient().getNom(),
                bl.getClient().getPrenom(),
                bl.getMotif(),
                bl.getDetails(),
                bl.getDateBlacklist(),
                bl.getBlacklistePar()
        ));
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Journal historique des operations de la blacklist", description = "Retourne l'historique des ajouts/retraits de la blacklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique de la blacklist"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/blacklist/historique")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
    public ResponseEntity<Page<ClientBlacklistHistoryResponseDTO>> obtenirHistorique(@ParameterObject Pageable pageable) {
        Page<ClientBlacklistHistory> page = clientBlacklistService.obtenirHistorique(pageable);
        Page<ClientBlacklistHistoryResponseDTO> dtos = page.map(h -> new ClientBlacklistHistoryResponseDTO(
                h.getIdHistory(),
                h.getIdClient(),
                h.getAction(),
                h.getClientNom(),
                h.getClientPrenom(),
                h.getNumeroClient(),
                h.getMotif(),
                h.getDetails(),
                h.getDateAction(),
                h.getOperateur()
        ));
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Ajouter un client a la blacklist", description = "Ajoute un client a la blacklist, met a jour son statut a BLOQUE et cree une entree dans l'historique")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client blacklistee avec succes"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation ou client deja blacklistee"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @AuditLog(action = "CLIENT_BLACKLIST_ADD", resource = "CLIENT")
    @PostMapping("/{idClient}/blacklist")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<ClientBlacklistResponseDTO> ajouterABlacklist(
            @PathVariable Long idClient,
            @Valid @RequestBody AddBlacklistRequestDTO requestDTO,
            Authentication authentication
    ) {
        String operateur = authentication != null ? authentication.getName() : "system";
        ClientBlacklist bl = clientBlacklistService.ajouterABlacklist(idClient, requestDTO, operateur);
        ClientBlacklistResponseDTO dto = new ClientBlacklistResponseDTO(
                bl.getClient().getIdClient(),
                bl.getClient().getIdClient(),
                bl.getClient().getCodeClient(),
                bl.getClient().getNom(),
                bl.getClient().getPrenom(),
                bl.getMotif(),
                bl.getDetails(),
                bl.getDateBlacklist(),
                bl.getBlacklistePar()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Retirer un client de la blacklist", description = "Retire un client de la blacklist, restaure son statut a ACTIF et cree une entree de retrait dans l'historique")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client retire de la blacklist"),
            @ApiResponse(responseCode = "404", description = "Client introuvable ou non blacklistee")
    })
    @AuditLog(action = "CLIENT_BLACKLIST_REMOVE", resource = "CLIENT")
    @DeleteMapping("/{idClient}/blacklist")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<Void> retirerDeBlacklist(
            @PathVariable Long idClient,
            @RequestBody(required = false) RemoveBlacklistRequestDTO requestDTO,
            Authentication authentication
    ) {
        String operateur = authentication != null ? authentication.getName() : "system";
        clientBlacklistService.retirerDeBlacklist(idClient, requestDTO, operateur);
        return ResponseEntity.noContent().build();
    }
}
