package com.microfinance.core_banking.api.controller.journalentry;

import com.microfinance.core_banking.dto.request.journalentry.JournalEntryRequestDTO;
import com.microfinance.core_banking.dto.response.journalentry.JournalEntryResponseDTO;
import com.microfinance.core_banking.service.journalentry.JournalEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/journal-entries")
@Tag(name = "JournalEntries", description = "API de gestion des journaux/écritures comptables")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @Operation(
            summary = "Creer une journal entry",
            description = "Crée une journal entry"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "JournalEntry créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit métier")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<JournalEntryResponseDTO> creerJournalEntry(
            @Valid @RequestBody JournalEntryRequestDTO requestDTO) {
        JournalEntryResponseDTO response = journalEntryService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les journal entries",
            description = "Retourne la liste paginée des journal entries"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des journaux"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Page<JournalEntryResponseDTO>> listerJournalEntries(@ParameterObject Pageable pageable) {
        Page<JournalEntryResponseDTO> page = journalEntryService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Obtenir les details d'une JournalEntry",
            description = "Retourne les informations d'une journal entry par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détails de la JournalEntry"),
            @ApiResponse(responseCode = "404", description = "JournalEntry introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<JournalEntryResponseDTO> obtenirDetailsJournalEntry(@PathVariable Long id) {
        JournalEntryResponseDTO response = journalEntryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour une JournalEntry",
            description = "Met à jour les informations d'une journal entry existante"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "JournalEntry mis à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "JournalEntry introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<JournalEntryResponseDTO> mettreAJourJournalEntry(
            @PathVariable Long id,
            @Valid @RequestBody JournalEntryRequestDTO requestDTO) {
        JournalEntryResponseDTO response = journalEntryService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer une JournalEntry",
            description = "Supprime une journal entry par identifiant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "JournalEntry supprimée"),
            @ApiResponse(responseCode = "404", description = "JournalEntry introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<Void> supprimerJournalEntry(@PathVariable Long id) {
        journalEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
