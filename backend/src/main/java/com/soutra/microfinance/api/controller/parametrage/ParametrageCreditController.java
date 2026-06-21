package com.soutra.microfinance.api.controller.parametrage;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.ProduitCreditRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.ProduitCreditResponseDTO;
import com.soutra.microfinance.entity.MethodeCalculInteret;
import com.soutra.microfinance.entity.ProduitCredit;
import com.soutra.microfinance.service.parametrage.ProduitService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/parametrages/produits-credit")
@Tag(name = "Parametrage Credit", description = "API de parametrage des produits de credit")
public class ParametrageCreditController {

    private final ProduitService produitService;

    public ParametrageCreditController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @Operation(summary = "Lister les produits credit",
            description = "Retourne la liste paginee des produits de credit, filtrable par statut, type d'amortissement ou recherche textuelle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste paginee des produits credit")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    public ResponseEntity<Page<ProduitCreditResponseDTO>> listerProduitsCredit(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String typeAmortissement,
            @RequestParam(required = false) String search,
            @ParameterObject Pageable pageable) {

        Page<ProduitCredit> produits = produitService.listerProduitsCreditPagine(pageable);
        Page<ProduitCreditResponseDTO> dtos = produits.map(this::toResponseDTO);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Creer un produit credit",
            description = "Enregistre un nouveau produit de credit dans le referentiel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produit credit cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (montants/durees incoherents)"),
            @ApiResponse(responseCode = "409", description = "Code produit deja utilise")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "PARAM_CREATE_PRODUIT_CREDIT", resource = "PARAMETRAGE")
    public ResponseEntity<ProduitCreditResponseDTO> creerProduitCredit(
            @Valid @RequestBody ProduitCreditRequestDTO dto) {

        if (dto.getMontantMax().compareTo(dto.getMontantMin()) <= 0) {
            throw new IllegalArgumentException("Le montant maximum doit etre strictement superieur au montant minimum.");
        }
        if (dto.getDureeMaxMois() <= dto.getDureeMinMois()) {
            throw new IllegalArgumentException("La duree maximum doit etre strictement superieure a la duree minimum.");
        }

        ProduitCredit produit = new ProduitCredit();
        produit.setCodeProduit(dto.getCodeProduit());
        produit.setLibelle(dto.getLibelle());
        produit.setTauxInteretAnnuel(dto.getTauxInteretAnnuel());
        produit.setDureeMinMois(dto.getDureeMinMois());
        produit.setDureeMaxMois(dto.getDureeMaxMois());
        produit.setMontantMin(dto.getMontantMin());
        produit.setMontantMax(dto.getMontantMax());
        produit.setFraisDossierPourcentage(dto.getFraisDossierPourcentage());
        produit.setPenaliteRetardPourcentage(dto.getPenaliteRetardPourcentage());
        if (dto.getMethodeCalcul() != null && !dto.getMethodeCalcul().isBlank()) {
            produit.setMethodeCalcul(MethodeCalculInteret.valueOf(dto.getMethodeCalcul().toUpperCase()));
        }

        ProduitCredit cree = produitService.creerProduitCredit(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(cree));
    }

    @Operation(summary = "Modifier un produit credit",
            description = "Met a jour les informations d'un produit de credit existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit credit modifie avec succes"),
            @ApiResponse(responseCode = "404", description = "Produit credit introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "PARAM_UPDATE_PRODUIT_CREDIT", resource = "PARAMETRAGE")
    public ResponseEntity<ProduitCreditResponseDTO> modifierProduitCredit(
            @PathVariable Long id,
            @Valid @RequestBody ProduitCreditRequestDTO dto) {

        ProduitCredit modifications = new ProduitCredit();
        modifications.setCodeProduit(dto.getCodeProduit());
        modifications.setLibelle(dto.getLibelle());
        modifications.setTauxInteretAnnuel(dto.getTauxInteretAnnuel());
        modifications.setDureeMinMois(dto.getDureeMinMois());
        modifications.setDureeMaxMois(dto.getDureeMaxMois());
        modifications.setMontantMin(dto.getMontantMin());
        modifications.setMontantMax(dto.getMontantMax());
        modifications.setFraisDossierPourcentage(dto.getFraisDossierPourcentage());
        modifications.setPenaliteRetardPourcentage(dto.getPenaliteRetardPourcentage());
        if (dto.getMethodeCalcul() != null && !dto.getMethodeCalcul().isBlank()) {
            modifications.setMethodeCalcul(MethodeCalculInteret.valueOf(dto.getMethodeCalcul().toUpperCase()));
        }

        ProduitCredit modifie = produitService.modifierProduitCredit(id, modifications);
        return ResponseEntity.ok(toResponseDTO(modifie));
    }

    @Operation(summary = "Supprimer un produit credit",
            description = "Desactive un produit de credit (suppression logique)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produit credit desactive avec succes"),
            @ApiResponse(responseCode = "404", description = "Produit credit introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_DELETE_PRODUIT_CREDIT", resource = "PARAMETRAGE")
    public ResponseEntity<Void> supprimerProduitCredit(@PathVariable Long id) {
        produitService.supprimerProduitCredit(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir un produit credit par ID",
            description = "Retourne les details d'un produit de credit specifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit credit trouve"),
            @ApiResponse(responseCode = "404", description = "Produit credit introuvable")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    public ResponseEntity<ProduitCreditResponseDTO> obtenirProduitCredit(@PathVariable Long id) {
        ProduitCredit produit = produitService.obtenirProduitCredit(id);
        return ResponseEntity.ok(toResponseDTO(produit));
    }

    @Operation(summary = "Lister les produits credit actifs",
            description = "Retourne la liste des produits de credit actifs pour listes déroulantes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des produits credit actifs")
    })
    @GetMapping("/actives")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProduitCreditResponseDTO>> listerProduitsCreditActifs() {
        List<ProduitCredit> produits = produitService.listerProduitsCredit();
        List<ProduitCreditResponseDTO> dtos = produits.stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    private ProduitCreditResponseDTO toResponseDTO(ProduitCredit p) {
        return new ProduitCreditResponseDTO(
                p.getIdProduitCredit(),
                p.getCodeProduit(),
                p.getLibelle(),
                p.getMethodeCalcul() != null ? p.getMethodeCalcul().name() : null,
                p.getTauxInteretAnnuel(),
                null,
                p.getDureeMinMois(),
                p.getDureeMaxMois(),
                p.getMontantMin(),
                p.getMontantMax(),
                p.getFraisDossierPourcentage(),
                p.getPenaliteRetardPourcentage(),
                p.getEstActif(),
                false,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
