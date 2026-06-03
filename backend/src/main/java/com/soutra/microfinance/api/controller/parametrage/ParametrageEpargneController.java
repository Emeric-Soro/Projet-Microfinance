package com.soutra.microfinance.api.controller.parametrage;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.parametrage.ProduitEpargneRequestDTO;
import com.soutra.microfinance.dto.response.parametrage.ProduitEpargneResponseDTO;
import com.soutra.microfinance.entity.ProduitEpargne;
import com.soutra.microfinance.service.parametrage.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parametrages/produits-epargne")
@Tag(name = "Parametrage Epargne", description = "API de parametrage des produits d'epargne")
public class ParametrageEpargneController {

    private final ProduitService produitService;

    public ParametrageEpargneController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @Operation(summary = "Lister les produits epargne",
            description = "Retourne la liste paginee des produits d'epargne")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste paginee des produits epargne")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    public ResponseEntity<Page<ProduitEpargneResponseDTO>> listerProduitsEpargne(@ParameterObject Pageable pageable) {
        Page<ProduitEpargne> produits = produitService.listerProduitsEpargnePagine(pageable);
        return ResponseEntity.ok(produits.map(this::toResponseDTO));
    }

    @Operation(summary = "Creer un produit epargne",
            description = "Enregistre un nouveau produit d'epargne dans le referentiel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produit epargne cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "409", description = "Code produit deja utilise")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "PARAM_CREATE_PRODUIT_EPARGNE", resource = "PARAMETRAGE")
    public ResponseEntity<ProduitEpargneResponseDTO> creerProduitEpargne(
            @Valid @RequestBody ProduitEpargneRequestDTO dto) {

        ProduitEpargne produit = new ProduitEpargne();
        produit.setCodeProduit(dto.getCodeProduit());
        produit.setLibelle(dto.getLibelle());
        produit.setTauxInteretAnnuel(dto.getTauxInteretAnnuel());
        produit.setMontantMinOuverture(dto.getMontantMinOuverture());
        produit.setPenaliteRetraitAnticipe(dto.getPenaliteRetraitAnticipe());
        produit.setDureeMinJours(dto.getDureeMinJours());

        ProduitEpargne cree = produitService.creerProduitEpargne(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(cree));
    }

    @Operation(summary = "Modifier un produit epargne",
            description = "Met a jour les informations d'un produit d'epargne existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit epargne modifie avec succes"),
            @ApiResponse(responseCode = "404", description = "Produit epargne introuvable")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "PARAM_UPDATE_PRODUIT_EPARGNE", resource = "PARAMETRAGE")
    public ResponseEntity<ProduitEpargneResponseDTO> modifierProduitEpargne(
            @PathVariable Long id,
            @Valid @RequestBody ProduitEpargneRequestDTO dto) {

        ProduitEpargne modifications = new ProduitEpargne();
        modifications.setCodeProduit(dto.getCodeProduit());
        modifications.setLibelle(dto.getLibelle());
        modifications.setTauxInteretAnnuel(dto.getTauxInteretAnnuel());
        modifications.setMontantMinOuverture(dto.getMontantMinOuverture());
        modifications.setPenaliteRetraitAnticipe(dto.getPenaliteRetraitAnticipe());
        modifications.setDureeMinJours(dto.getDureeMinJours());

        ProduitEpargne modifie = produitService.modifierProduitEpargne(id, modifications);
        return ResponseEntity.ok(toResponseDTO(modifie));
    }

    @Operation(summary = "Supprimer un produit epargne",
            description = "Desactive un produit d'epargne (suppression logique)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produit epargne desactive avec succes"),
            @ApiResponse(responseCode = "404", description = "Produit epargne introuvable")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @AuditLog(action = "PARAM_DELETE_PRODUIT_EPARGNE", resource = "PARAMETRAGE")
    public ResponseEntity<Void> supprimerProduitEpargne(@PathVariable Long id) {
        produitService.supprimerProduitEpargne(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lister les produits epargne actifs",
            description = "Retourne la liste des produits d'epargne actifs pour listes déroulantes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des produits epargne actifs")
    })
    @GetMapping("/actives")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProduitEpargneResponseDTO>> listerProduitsEpargneActifs() {
        List<ProduitEpargne> produits = produitService.listerProduitsEpargne();
        List<ProduitEpargneResponseDTO> dtos = produits.stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    private ProduitEpargneResponseDTO toResponseDTO(ProduitEpargne p) {
        return new ProduitEpargneResponseDTO(
                p.getIdProduitEpargne(),
                p.getCodeProduit(),
                p.getLibelle(),
                p.getTauxInteretAnnuel(),
                p.getMontantMinOuverture(),
                null,
                p.getDureeMinJours(),
                false,
                p.getEstActif(),
                p.getCreatedAt()
        );
    }
}
