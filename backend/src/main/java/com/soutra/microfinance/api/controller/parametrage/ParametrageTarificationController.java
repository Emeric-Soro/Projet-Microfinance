package com.soutra.microfinance.api.controller.parametrage;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.TarificationRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.TarificationResponseDTO;
import com.soutra.microfinance.entity.TarificationParametre;
import com.soutra.microfinance.service.tarification.TarificationParametreService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/parametrages/tarification")
@Tag(name = "Parametrage Tarification", description = "API de parametrage des tarifications (frais, commissions, seuils)")
public class ParametrageTarificationController {

    private final TarificationParametreService tarificationParametreService;

    public ParametrageTarificationController(TarificationParametreService tarificationParametreService) {
        this.tarificationParametreService = tarificationParametreService;
    }

    @Operation(summary = "Lister les parametres de tarification",
            description = "Retourne tous les parametres de tarification (frais, commissions, seuils)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des parametres de tarification")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    public ResponseEntity<List<TarificationResponseDTO>> listerTarification() {
        List<TarificationParametre> parametres = tarificationParametreService.listerTousParametres();
        List<TarificationResponseDTO> dtos = parametres.stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Creer un parametre de tarification",
            description = "Ajoute un nouveau parametre de tarification dans le referentiel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parametre de tarification cree avec succes"),
            @ApiResponse(responseCode = "409", description = "Code parametre deja utilise")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_CREATE_TARIFICATION", resource = "PARAMETRAGE")
    public ResponseEntity<TarificationResponseDTO> creerTarification(
            @Valid @RequestBody TarificationRequestDTO dto) {

        TarificationParametre parametre = new TarificationParametre();
        parametre.setCleParametre(dto.getCode());
        parametre.setValeurParametre(dto.getValeur());
        parametre.setDescriptionParametre(dto.getLibelle());

        TarificationParametre cree = tarificationParametreService.creerParametre(parametre);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(cree));
    }

    private TarificationResponseDTO toResponseDTO(TarificationParametre p) {
        return new TarificationResponseDTO(
                p.getIdParametre(),
                p.getCleParametre(),
                p.getDescriptionParametre(),
                null,
                p.getValeurParametre(),
                null,
                true,
                p.getCreatedAt()
        );
    }
}
