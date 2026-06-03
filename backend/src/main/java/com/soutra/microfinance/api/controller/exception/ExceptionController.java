package com.soutra.microfinance.api.controller.exception;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.*;
import com.soutra.microfinance.dto.response.common.*;
import com.soutra.microfinance.service.exception.ExceptionService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exceptions")
@Tag(name = "Exceptions", description = "API de gestion des derogations et escalades")
public class ExceptionController {

    private final ExceptionService exceptionService;

    public ExceptionController(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @Operation(
            summary = "Creer une derogation",
            description = "Cree une demande de derogation pour une operation exceptionnelle"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Derogation creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/derogations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
    @AuditLog(action = "EXCEPTION_CREATE_DEROGATION", resource = "EXCEPTION")
    public ResponseEntity<DerogationResponseDTO> creerDerogation(
            @Valid @RequestBody DerogationRequestDTO requestDTO,
            Authentication authentication
    ) {
        String creePar = authentication != null ? authentication.getName() : "SYSTEME";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exceptionService.creerDerogation(requestDTO, creePar));
    }

    @Operation(
            summary = "Lister les derogations",
            description = "Retourne la liste paginee des derogations"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des derogations retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/derogations")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "EXCEPTION_LIST_DEROGATIONS", resource = "EXCEPTION")
    public ResponseEntity<Page<DerogationResponseDTO>> listerDerogations(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(exceptionService.listerDerogations(pageable));
    }

    @Operation(
            summary = "Traiter une derogation",
            description = "Approuve ou rejette une demande de derogation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Derogation traitee avec succes"),
            @ApiResponse(responseCode = "404", description = "Derogation introuvable"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PutMapping("/derogations/{id}/statut")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "EXCEPTION_TRAITER_DEROGATION", resource = "EXCEPTION")
    public ResponseEntity<DerogationResponseDTO> traiterDerogation(
            @PathVariable Long id,
            @Valid @RequestBody TraiterDerogationRequestDTO requestDTO,
            Authentication authentication
    ) {
        String traitePar = authentication != null ? authentication.getName() : "SYSTEME";
        return ResponseEntity.ok(exceptionService.traiterDerogation(id, requestDTO, traitePar));
    }

    @Operation(
            summary = "Creer une escalade",
            description = "Cree une escalade pour un cas necessitant une attention particuliere"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Escalade creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/escalades")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
    @AuditLog(action = "EXCEPTION_CREATE_ESCALADE", resource = "EXCEPTION")
    public ResponseEntity<EscaladeResponseDTO> creerEscalade(
            @Valid @RequestBody EscaladeRequestDTO requestDTO,
            Authentication authentication
    ) {
        String creePar = authentication != null ? authentication.getName() : "SYSTEME";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exceptionService.creerEscalade(requestDTO, creePar));
    }

    @Operation(
            summary = "Lister les escalades",
            description = "Retourne la liste paginee des escalades"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des escalades retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/escalades")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "EXCEPTION_LIST_ESCALADES", resource = "EXCEPTION")
    public ResponseEntity<Page<EscaladeResponseDTO>> listerEscalades(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(exceptionService.listerEscalades(pageable));
    }

    @Operation(
            summary = "Detail d'une escalade",
            description = "Retourne le detail d'une escalade specifique"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Escalade trouvee"),
            @ApiResponse(responseCode = "404", description = "Escalade introuvable"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/escalades/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<EscaladeResponseDTO> getEscaladeById(@PathVariable Long id) {
        return ResponseEntity.ok(exceptionService.getEscaladeById(id));
    }

    @Operation(
            summary = "Traiter une escalade",
            description = "Traite une escalade et la fait progresser au niveau suivant (N1->N2->N3->N4)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Escalade traitee avec succes"),
            @ApiResponse(responseCode = "404", description = "Escalade introuvable"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PutMapping("/escalades/{id}/traiter")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "EXCEPTION_TRAITER_ESCALADE", resource = "EXCEPTION")
    public ResponseEntity<EscaladeResponseDTO> traiterEscalade(
            @PathVariable Long id,
            @Valid @RequestBody TraiterEscaladeRequestDTO requestDTO,
            Authentication authentication
    ) {
        String traitePar = authentication != null ? authentication.getName() : "SYSTEME";
        return ResponseEntity.ok(exceptionService.traiterEscalade(id, requestDTO, traitePar));
    }

    @Operation(
            summary = "Lister les regles",
            description = "Retourne la liste des regles de derogation et d'escalade"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des regles retournee avec succes"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/regles")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @AuditLog(action = "EXCEPTION_LIST_REGLES", resource = "EXCEPTION")
    public ResponseEntity<List<RegleDerogationEscaladeResponseDTO>> listerRegles() {
        return ResponseEntity.ok(exceptionService.listerRegles());
    }
}
