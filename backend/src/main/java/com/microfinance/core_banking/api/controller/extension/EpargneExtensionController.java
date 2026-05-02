package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CloturerDatRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerProduitEpargneRequestDTO;
import com.microfinance.core_banking.dto.request.extension.SouscrireDatRequestDTO;
import com.microfinance.core_banking.dto.request.extension.SouscrireDatServiceRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.DepotATermeResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ProduitEpargneResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.DepotATerme;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/epargne")
@Tag(name = "Épargne", description = "API de gestion des produits d'épargne et dépôts à terme")
public class EpargneExtensionController {

    private final EpargneExtensionService epargneExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;
    private final AuthenticatedUserService authenticatedUserService;

    public EpargneExtensionController(EpargneExtensionService epargneExtensionService, PendingActionSubmissionService pendingActionSubmissionService, AuthenticatedUserService authenticatedUserService) {
        this.epargneExtensionService = epargneExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_MANAGE)")
    @AuditLog(action = "SAVINGS_PRODUCT_CREATE", resource = "PRODUIT_EPARGNE")
    @Operation(summary = "Créer un produit d'épargne", description = "Soumet la création d'un nouveau produit d'épargne au workflow Maker-Checker. Le produit est défini par son code, son libellé, sa catégorie, son taux d'intérêt et sa fréquence de capitalisation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerProduit(@Valid @RequestBody CreerProduitEpargneRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PRODUIT_EPARGNE", "PRODUIT_EPARGNE", dto.getCodeProduit(), dto, "Creation produit epargne soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_VIEW)")
    @Operation(summary = "Lister les produits d'épargne", description = "Retourne la liste de tous les produits d'épargne disponibles. Chaque produit affiche son code, son libellé, sa catégorie, son taux d'intérêt et son statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits d'épargne", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProduitEpargneResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ProduitEpargneResponseDTO>> listerProduits() {
        return ResponseEntity.ok(epargneExtensionService.listerProduits().stream().map(this::toDto).toList());
    }

    @PostMapping("/depots-a-terme")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_MANAGE)")
    @AuditLog(action = "TERM_DEPOSIT_CREATE", resource = "DEPOT_A_TERME")
    @Operation(summary = "Souscrire un dépôt à terme", description = "Crée une nouvelle souscription de dépôt à terme (DAT) pour un client. Le montant, la durée en mois et le produit d'épargne associé sont requis pour le calcul des intérêts.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dépôt à terme souscrit avec succès", content = @Content(schema = @Schema(implementation = DepotATermeResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Client ou produit d'épargne non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<DepotATermeResponseDTO> souscrireDepotATerme(@Valid @RequestBody SouscrireDatRequestDTO dto) {
        SouscrireDatServiceRequestDTO serviceDto = new SouscrireDatServiceRequestDTO();
        serviceDto.setIdClient(dto.getIdClient());
        serviceDto.setIdProduitEpargne(dto.getIdProduitEpargne());
        serviceDto.setMontant(dto.getMontantSouscription().toString());
        serviceDto.setDureeMois(String.valueOf(dto.getDureeMois()));
        serviceDto.setDateSouscription(dto.getDateSouscription());
        serviceDto.setIdUtilisateurOperateur(dto.getIdGuichetier());
        serviceDto.setRenouvellementAuto(dto.getRenouvellementAuto());
        serviceDto.setNumCompteSupport(dto.getNumCompteSupport());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(epargneExtensionService.souscrireDepotATerme(serviceDto)));
    }

    @GetMapping("/depots-a-terme")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_VIEW)")
    @Operation(summary = "Lister les dépôts à terme", description = "Retourne la liste de tous les dépôts à terme souscrits. Chaque dépôt inclut sa référence, le montant, le taux appliqué, les intérêts estimés et la date d'échéance.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des dépôts à terme", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DepotATermeResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<DepotATermeResponseDTO>> listerDepotsATerme() {
        return ResponseEntity.ok(epargneExtensionService.listerDepotsATerme().stream().map(this::toDto).toList());
    }

    @Operation(
        summary = "Calculer les interets d'epargne",
        description = "Calcule et credite les interets sur tous les comptes epargne actifs"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Interets calcules et credites avec succes"),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acces interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/calcul-interets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR, T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_MANAGE)")
    @AuditLog(action = "SAVINGS_INTEREST_CALCULATE", resource = "PRODUIT_EPARGNE")
    public ResponseEntity<Integer> calculerInteretsEpargne(
            @RequestParam(required = false) LocalDate dateCalcul,
            @RequestParam(required = false) Long idUtilisateurOperateur) {
        LocalDate date = dateCalcul != null ? dateCalcul : LocalDate.now();
        Long userId = idUtilisateurOperateur != null ? idUtilisateurOperateur : authenticatedUserService.getCurrentUserOrThrow().getIdUser();
        int comptesCredites = epargneExtensionService.calculerInteretsEpargne(date, userId);
        return ResponseEntity.ok(comptesCredites);
    }

    @Operation(
        summary = "Soumettre une cloture de depot a terme",
        description = "Soumet une demande de cloture d'un depot a terme pour approbation superviseur"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Cloture DAT soumise en attente de validation"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acces interdit", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "DAT introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/depots-a-terme/{id}/cloture")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_MANAGE)")
    @AuditLog(action = "SAVINGS_DAT_CLOSE_SUBMIT", resource = "DEPOT_A_TERME")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreClotureDat(
            @PathVariable Long id,
            @Valid @RequestBody CloturerDatRequestDTO requestDTO) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CLOTURE_DAT", "DEPOT_A_TERME", String.valueOf(id), requestDTO, "Cloture DAT soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Consulter un depot a terme", description = "Retourne les details d'un depot a terme par son identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "DAT trouve", content = @Content(schema = @Schema(implementation = DepotATermeResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "DAT introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/depots-a-terme/{id}")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER, T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_VIEW)")
    public ResponseEntity<DepotATermeResponseDTO> consulterDepotATerme(@PathVariable Long id) {
        return epargneExtensionService.consulterDepotATerme(id)
            .map(this::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private ProduitEpargneResponseDTO toDto(ProduitEpargne produit) {
        ProduitEpargneResponseDTO dto = new ProduitEpargneResponseDTO();
        dto.setIdProduitEpargne(produit.getIdProduitEpargne());
        dto.setCodeProduit(produit.getCodeProduit());
        dto.setLibelle(produit.getLibelle());
        dto.setCategorie(produit.getCategorie());
        dto.setTauxInteret(produit.getTauxInteret());
        dto.setFrequenceInteret(produit.getFrequenceInteret());
        dto.setStatut(produit.getStatut());
        return dto;
    }

    private DepotATermeResponseDTO toDto(DepotATerme depot) {
        DepotATermeResponseDTO dto = new DepotATermeResponseDTO();
        dto.setIdDepotTerme(depot.getIdDepotTerme());
        dto.setReferenceDepot(depot.getReferenceDepot());
        dto.setIdClient(depot.getClient().getIdClient());
        dto.setProduit(depot.getProduitEpargne().getLibelle());
        dto.setMontant(depot.getMontant());
        dto.setTauxApplique(depot.getTauxApplique());
        dto.setInteretsEstimes(depot.getInteretsEstimes());
        dto.setDateEcheance(depot.getDateEcheance());
        dto.setCompteSupport(depot.getCompteSupport() == null ? null : depot.getCompteSupport().getNumCompte());
        dto.setReferenceTransactionSouscription(depot.getReferenceTransactionSouscription());
        dto.setStatut(depot.getStatut());
        return dto;
    }

    private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
