package com.soutra.microfinance.api.controller.operation;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.operation.FermetureCaisseRequestDTO;
import com.soutra.microfinance.dto.request.operation.OuvertureCaisseRequestDTO;
import com.soutra.microfinance.dto.response.operation.CaisseResponseDTO;
import com.soutra.microfinance.entity.Caisse;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.operation.CaisseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/caisses")
@Tag(name = "Caisses", description = "Gestion des tiroirs-caisses guichetier")
public class CaisseController {

    private final CaisseService caisseService;

    public CaisseController(CaisseService caisseService) {
        this.caisseService = caisseService;
    }

    @Operation(summary = "Ouvrir une caisse", description = "Ouvre un tiroir-caisse pour le guichetier connecte")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Caisse ouverte avec succes"),
            @ApiResponse(responseCode = "400", description = "Caisse deja ouverte"),
            @ApiResponse(responseCode = "404", description = "Guichetier introuvable")
    })
    @PostMapping("/ouverture")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CAISSE_OPEN", resource = "CAISSE")
    public ResponseEntity<CaisseResponseDTO> ouvrirCaisse(
            @Valid @RequestBody OuvertureCaisseRequestDTO dto,
            Authentication authentication
    ) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Caisse caisse = caisseService.ouvrirCaisse(utilisateur.getIdUser(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(caisse));
    }

    @Operation(summary = "Fermer une caisse", description = "Ferme la caisse ouverte et calcule l'ecart entre le solde informatique et le solde physique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Caisse fermee avec succes"),
            @ApiResponse(responseCode = "400", description = "Aucune caisse ouverte a fermer")
    })
    @PostMapping("/fermeture")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "CAISSE_CLOSE", resource = "CAISSE")
    public ResponseEntity<CaisseResponseDTO> fermerCaisse(
            @Valid @RequestBody FermetureCaisseRequestDTO dto,
            Authentication authentication
    ) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Caisse caisse = caisseService.fermerCaisse(utilisateur.getIdUser(), dto);
        return ResponseEntity.ok(toResponseDTO(caisse));
    }

    @Operation(summary = "Consulter l'etat de la caisse", description = "Retourne le solde actuel de la caisse du guichetier connecte")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etat de la caisse"),
            @ApiResponse(responseCode = "400", description = "Aucune caisse ouverte")
    })
    @GetMapping("/etat")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    public ResponseEntity<CaisseResponseDTO> etatCaisse(Authentication authentication) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        Caisse caisse = caisseService.consulterCaisseOuverte(utilisateur.getIdUser());
        return ResponseEntity.ok(toResponseDTO(caisse));
    }

    private CaisseResponseDTO toResponseDTO(Caisse caisse) {
        Utilisateur guichetier = caisse.getUtilisateur();
        Long agenceId = null;
        String agenceNom = null;
        if (guichetier.getAgence() != null) {
            agenceId = guichetier.getAgence().getIdAgence();
            agenceNom = guichetier.getAgence().getNom();
        }

        CaisseResponseDTO dto = new CaisseResponseDTO();
        dto.setId(caisse.getIdCaisse());
        dto.setCodeGuichet(guichetier.getLogin());
        dto.setFondInitial(caisse.getSoldeOuverture());
        dto.setSoldeActuel(caisse.getSoldeCourant());
        dto.setEcartFermeture(caisse.getEcartFermeture());
        dto.setDateOuverture(caisse.getDateOuverture());
        dto.setDateFermeture(caisse.getDateFermeture());
        dto.setStatut(caisse.getStatut() != null ? caisse.getStatut().name() : null);
        dto.setAgenceId(agenceId);
        dto.setAgenceNom(agenceNom);
        dto.setGuichetierId(guichetier.getIdUser());
        dto.setGuichetierNom(guichetier.getLogin());
        dto.setCreatedAt(caisse.getCreatedAt());
        return dto;
    }
}
