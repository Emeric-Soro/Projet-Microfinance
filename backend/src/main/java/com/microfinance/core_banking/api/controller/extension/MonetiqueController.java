package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.dto.request.extension.BlocageCarteRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EmettreCarteRequestDTO;
import com.microfinance.core_banking.dto.request.extension.PaiementPosRequestDTO;
import com.microfinance.core_banking.dto.request.extension.PinRequestDTO;
import com.microfinance.core_banking.dto.response.extension.TokenCarteResponseDTO;
import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.service.extension.MonetiqueService;
import com.microfinance.core_banking.service.security.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monetique")
@Tag(name = "Monétique", description = "API de gestion des cartes bancaires, paiements POS, tokens et codes PIN")
public class MonetiqueController {

    private final MonetiqueService monetiqueService;

    public MonetiqueController(MonetiqueService monetiqueService) {
        this.monetiqueService = monetiqueService;
    }

    @PostMapping("/cartes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Émettre une carte bancaire", description = "Émet une nouvelle carte bancaire (Visa) liée à un compte client avec des plafonds journalier et mensuel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carte émise avec succès", content = @Content(schema = @Schema(implementation = CarteVisa.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CarteVisa> emettreCarte(@Valid @RequestBody EmettreCarteRequestDTO request) {
        return ResponseEntity.ok(monetiqueService.emettreCarte(
                request.getIdCompte(),
                request.getTypeCarte(),
                request.getPlafondJournalier(),
                request.getPlafondMensuel()));
    }

    @PostMapping("/cartes/{id}/pin")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_UPDATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Définir le code PIN d'une carte", description = "Définit ou modifie le code PIN d'une carte bancaire existante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PIN défini avec succès", content = @Content),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Carte non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> definirPin(@PathVariable Long id, @Valid @RequestBody PinRequestDTO request) {
        monetiqueService.definirPin(id, request.getPin());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/paiements/pos")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENT_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Effectuer un paiement POS", description = "Traite un paiement par carte au point de vente (POS) avec vérification du code PIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paiement autorisé avec succès", content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "PIN invalide ou carte bloquée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Carte non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Map<String, Object>> paiementPos(@Valid @RequestBody PaiementPosRequestDTO request) {
        if (!monetiqueService.verifierPin(request.getIdCarte(), request.getPin())) {
            return ResponseEntity.status(403).body(Map.of("erreur", "PIN invalide ou carte bloquee"));
        }
        return ResponseEntity.ok(Map.of("statut", "OK", "message", "Paiement autorise"));
    }

    @PutMapping("/cartes/{id}/blocage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_UPDATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Bloquer une carte bancaire", description = "Bloque une carte bancaire pour le motif spécifié (perte, vol, fraude, etc.)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carte bloquée avec succès", content = @Content(schema = @Schema(implementation = CarteVisa.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Carte non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CarteVisa> bloquerCarte(@PathVariable Long id, @Valid @RequestBody BlocageCarteRequestDTO request) {
        return ResponseEntity.ok(monetiqueService.bloquerCarte(id, request.getMotif()));
    }

    @PutMapping("/cartes/{id}/deblocage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_UPDATE_CARTE, "
            + "T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Débloquer une carte bancaire", description = "Débloque une carte bancaire précédemment bloquée")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carte débloquée avec succès", content = @Content(schema = @Schema(implementation = CarteVisa.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Carte non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<CarteVisa> debloquerCarte(@PathVariable Long id) {
        return ResponseEntity.ok(monetiqueService.debloquerCarte(id));
    }

    @PostMapping("/cartes/{id}/token")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @Operation(summary = "Tokeniser une carte bancaire", description = "Génère un token de remplacement pour une carte bancaire, utilisé pour les paiements sans contact ou mobiles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token généré avec succès", content = @Content(schema = @Schema(implementation = TokenCarteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Carte non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<TokenCarteResponseDTO> tokeniserCarte(@PathVariable Long id) {
        String token = monetiqueService.tokeniserCarte(id);
        return ResponseEntity.ok(new TokenCarteResponseDTO(token));
    }
}
