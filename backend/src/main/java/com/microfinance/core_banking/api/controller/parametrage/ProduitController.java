package com.microfinance.core_banking.api.controller.parametrage;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.parametrage.ProduitCreditRequestDTO;
import com.microfinance.core_banking.dto.request.parametrage.ProduitEpargneRequestDTO;
import com.microfinance.core_banking.entity.MethodeCalculInteret;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.service.parametrage.ProduitService;
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
@RequestMapping("/api/produits")
@Tag(name = "Produits", description = "API de parametrage des produits de credit et d'epargne")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    // ────────────────────────────────── PRODUITS DE CRÉDIT ──────────────────────────────────

    @Operation(
            summary = "Creer un produit de credit",
            description = "Enregistre un nouveau produit de credit dans le referentiel"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produit de credit cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides (montants/durees incohérents)"),
            @ApiResponse(responseCode = "409", description = "Code produit deja utilise")
    })
    @PostMapping("/credits")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "PRODUCT_CREATE", resource = "PRODUIT_CREDIT")
    public ResponseEntity<ProduitCredit> creerProduitCredit(@Valid @RequestBody ProduitCreditRequestDTO dto) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.creerProduitCredit(produit));
    }

    @Operation(
            summary = "Lister les produits de credit actifs",
            description = "Retourne tous les produits de credit disponibles pour le frontend"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des produits de credit actifs")
    })
    @GetMapping("/credits")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProduitCredit>> listerProduitsCreditActifs() {
        return ResponseEntity.ok(produitService.listerProduitsCredit());
    }

    // ────────────────────────────────── PRODUITS D'ÉPARGNE ──────────────────────────────────

    @Operation(
            summary = "Creer un produit d'epargne",
            description = "Enregistre un nouveau produit d'epargne dans le referentiel"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produit d'epargne cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "409", description = "Code produit deja utilise")
    })
    @PostMapping("/epargnes")
    @PreAuthorize("hasAnyAuthority('ADMIN','DIRECTEUR')")
    @AuditLog(action = "PRODUCT_CREATE", resource = "PRODUIT_EPARGNE")
    public ResponseEntity<ProduitEpargne> creerProduitEpargne(@Valid @RequestBody ProduitEpargneRequestDTO dto) {
        ProduitEpargne produit = new ProduitEpargne();
        produit.setCodeProduit(dto.getCodeProduit());
        produit.setLibelle(dto.getLibelle());
        produit.setTauxInteretAnnuel(dto.getTauxInteretAnnuel());
        produit.setMontantMinOuverture(dto.getMontantMinOuverture());
        produit.setPenaliteRetraitAnticipe(dto.getPenaliteRetraitAnticipe());
        produit.setDureeMinJours(dto.getDureeMinJours());
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.creerProduitEpargne(produit));
    }

    @Operation(
            summary = "Lister les produits d'epargne actifs",
            description = "Retourne tous les produits d'epargne disponibles pour le frontend"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des produits d'epargne actifs")
    })
    @GetMapping("/epargnes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProduitEpargne>> listerProduitsEpargneActifs() {
        return ResponseEntity.ok(produitService.listerProduitsEpargne());
    }
}
