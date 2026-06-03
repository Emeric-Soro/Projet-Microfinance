package com.soutra.microfinance.api.controller.parametrage;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.AgenceRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.AgenceResponseDTO;
import com.soutra.microfinance.entity.Agence;
import com.soutra.microfinance.service.parametrage.AgenceService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parametrages/agences")
@Tag(name = "Parametrage Agence", description = "API de parametrage des agences")
public class ParametrageAgenceController {

    private final AgenceService agenceService;

    public ParametrageAgenceController(AgenceService agenceService) {
        this.agenceService = agenceService;
    }

    @Operation(summary = "Lister les agences",
            description = "Retourne la liste paginee des agences")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste paginee des agences")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    public ResponseEntity<Page<AgenceResponseDTO>> listerAgences(@ParameterObject Pageable pageable) {
        Page<Agence> agences = agenceService.listerAgencesPagine(pageable);
        return ResponseEntity.ok(agences.map(this::toResponseDTO));
    }

    @Operation(summary = "Creer une agence",
            description = "Enregistre un nouveau point de service")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agence creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "409", description = "Code agence deja utilise")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_CREATE_AGENCE", resource = "PARAMETRAGE")
    public ResponseEntity<AgenceResponseDTO> creerAgence(@Valid @RequestBody AgenceRequestDTO dto) {
        Agence agence = new Agence();
        agence.setCodeAgence(dto.getCodeAgence());
        agence.setNom(dto.getNom());
        agence.setAdresse(dto.getAdresse());
        agence.setTelephone(dto.getTelephone());

        Agence cree = agenceService.creerAgence(agence);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(cree));
    }

    @Operation(summary = "Modifier une agence",
            description = "Met a jour les informations d'une agence existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence modifiee avec succes"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_UPDATE_AGENCE", resource = "PARAMETRAGE")
    public ResponseEntity<AgenceResponseDTO> modifierAgence(
            @PathVariable Long id,
            @Valid @RequestBody AgenceRequestDTO dto) {

        Agence modifications = new Agence();
        modifications.setCodeAgence(dto.getCodeAgence());
        modifications.setNom(dto.getNom());
        modifications.setAdresse(dto.getAdresse());
        modifications.setTelephone(dto.getTelephone());

        Agence modifie = agenceService.modifierAgence(id, modifications);
        return ResponseEntity.ok(toResponseDTO(modifie));
    }

    @Operation(summary = "Consulter une agence",
            description = "Retourne les details d'une agence par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvee"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
    public ResponseEntity<AgenceResponseDTO> obtenirAgence(@PathVariable Long id) {
        Agence agence = agenceService.obtenirAgence(id);
        return ResponseEntity.ok(toResponseDTO(agence));
    }

    @Operation(summary = "Lister les agences actives",
            description = "Retourne toutes les agences actuellement en activite (pour listes déroulantes)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des agences actives")
    })
    @GetMapping("/actives")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
    public ResponseEntity<List<AgenceResponseDTO>> listerAgencesActives() {
        List<Agence> agences = agenceService.listerAgencesActives();
        List<AgenceResponseDTO> dtos = agences.stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Desactiver une agence",
            description = "Desactive une agence sans la supprimer physiquement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence desactivee avec succes"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @PutMapping("/{id}/desactiver")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_DEACTIVATE_AGENCE", resource = "PARAMETRAGE")
    public ResponseEntity<AgenceResponseDTO> desactiverAgence(@PathVariable Long id) {
        Agence agence = agenceService.desactiverAgence(id);
        return ResponseEntity.ok(toResponseDTO(agence));
    }

    private AgenceResponseDTO toResponseDTO(Agence a) {
        return new AgenceResponseDTO(
                a.getIdAgence(),
                a.getCodeAgence(),
                a.getNom(),
                a.getAdresse(),
                a.getTelephone(),
                null,
                null,
                a.getEstActive(),
                a.getCreatedAt()
        );
    }
}
