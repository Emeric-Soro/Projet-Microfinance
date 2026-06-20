package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.response.mobile.MobileReleveResponseDTO;
import com.soutra.microfinance.dto.response.mobile.MobileSoldeResponseDTO;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.OperationMapper;
import com.soutra.microfinance.service.compte.CompteService;
import com.soutra.microfinance.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/comptes")
@Tag(name = "Mobile Comptes", description = "API de consultation des comptes pour l'application mobile")
public class MobileCompteController {

    private final CompteService compteService;
    private final TransactionService transactionService;
    private final OperationMapper operationMapper;

    public MobileCompteController(
            CompteService compteService,
            TransactionService transactionService,
            OperationMapper operationMapper
    ) {
        this.compteService = compteService;
        this.transactionService = transactionService;
        this.operationMapper = operationMapper;
    }

    @Operation(summary = "Lister les comptes", description = "Retourne la liste des comptes du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des comptes retournee avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_COMPTE_LIST", resource = "COMPTE")
    public ResponseEntity<List<MobileSoldeResponseDTO>> listerComptes(Authentication authentication) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        List<Compte> comptes = compteService.listerComptesClient(idClient, Pageable.unpaged()).getContent();

        List<MobileSoldeResponseDTO> response = comptes.stream()
                .map(this::toSoldeResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Detail d'un compte", description = "Retourne les informations detaillees d'un compte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail du compte retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{idCompte}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_COMPTE_DETAIL", resource = "COMPTE")
    public ResponseEntity<MobileSoldeResponseDTO> detailCompte(
            @PathVariable Long idCompte,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Compte compte = compteService.consulterCompte(idCompte);

        if (!compte.getClient().getIdClient().equals(utilisateur.getClient().getIdClient())) {
            throw new EntityNotFoundException("Compte introuvable");
        }

        return ResponseEntity.ok(toSoldeResponse(compte));
    }

    @Operation(summary = "Solde d'un compte", description = "Retourne le solde actuel d'un compte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solde retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{idCompte}/solde")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_COMPTE_SOLDE", resource = "COMPTE")
    public ResponseEntity<BigDecimal> consulterSolde(
            @PathVariable Long idCompte,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Compte compte = compteService.consulterCompte(idCompte);

        if (!compte.getClient().getIdClient().equals(utilisateur.getClient().getIdClient())) {
            throw new EntityNotFoundException("Compte introuvable");
        }

        return ResponseEntity.ok(compte.getSolde());
    }

    @Operation(summary = "Operations d'un compte", description = "Retourne les operations paginees d'un compte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operations retournees avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{idCompte}/operations")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_COMPTE_OPERATIONS", resource = "COMPTE")
    public ResponseEntity<Page<com.soutra.microfinance.dto.response.operation.LigneReleveResponseDTO>> operationsCompte(
            @PathVariable Long idCompte,
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Compte compte = compteService.consulterCompte(idCompte);

        if (!compte.getClient().getIdClient().equals(utilisateur.getClient().getIdClient())) {
            throw new EntityNotFoundException("Compte introuvable");
        }

        Page<LigneEcriture> pageLignes = transactionService.historiqueOperations(compte.getNumCompte(), pageable);
        Page<com.soutra.microfinance.dto.response.operation.LigneReleveResponseDTO> response =
                pageLignes.map(operationMapper::toLigneReleveResponseDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Releve de compte PDF", description = "Retourne une URL de releve de compte au format PDF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL du releve retournee avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{idCompte}/releve")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_COMPTE_RELEVE", resource = "COMPTE")
    public ResponseEntity<MobileReleveResponseDTO> releveCompte(
            @PathVariable Long idCompte,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Compte compte = compteService.consulterCompte(idCompte);

        if (!compte.getClient().getIdClient().equals(utilisateur.getClient().getIdClient())) {
            throw new EntityNotFoundException("Compte introuvable");
        }

        String urlReleve = "/api/v1/comptes/" + compte.getNumCompte() + "/releve?format=pdf";
        return ResponseEntity.ok(new MobileReleveResponseDTO(urlReleve));
    }

    private MobileSoldeResponseDTO toSoldeResponse(Compte compte) {
        String libelleType = compte.getTypeCompte() != null ? compte.getTypeCompte().getLibelle() : "Inconnu";
        String statut = extraireStatutCourant(compte);

        return new MobileSoldeResponseDTO(
                compte.getIdCompte(),
                compte.getNumCompte(),
                libelleType,
                compte.getSolde(),
                statut
        );
    }

    private String extraireStatutCourant(Compte compte) {
        if (compte.getStatutsCompte() == null || compte.getStatutsCompte().isEmpty()) {
            return "ACTIF";
        }
        return compte.getStatutsCompte().stream()
                .max(Comparator.comparing(StatutCompte::getDateStatut, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(StatutCompte::getLibelleStatut)
                .orElse("ACTIF");
    }

}
