package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CreerProduitEpargneRequestDTO;
import com.microfinance.core_banking.dto.request.extension.SouscrireDatRequestDTO;
import com.microfinance.core_banking.dto.request.extension.SouscrireDatServiceRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.DepotATermeResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ProduitEpargneResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.DepotATerme;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
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
@RequestMapping("/api/epargne")
public class EpargneExtensionController {

    private final EpargneExtensionService epargneExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public EpargneExtensionController(EpargneExtensionService epargneExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.epargneExtensionService = epargneExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_MANAGE)")
    @AuditLog(action = "SAVINGS_PRODUCT_CREATE", resource = "PRODUIT_EPARGNE")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerProduit(@Valid @RequestBody CreerProduitEpargneRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PRODUIT_EPARGNE", "PRODUIT_EPARGNE", dto.getCodeProduit(), dto, "Creation produit epargne soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_VIEW)")
    public ResponseEntity<List<ProduitEpargneResponseDTO>> listerProduits() {
        return ResponseEntity.ok(epargneExtensionService.listerProduits().stream().map(this::toDto).toList());
    }

    @PostMapping("/depots-a-terme")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_MANAGE)")
    @AuditLog(action = "TERM_DEPOSIT_CREATE", resource = "DEPOT_A_TERME")
    public ResponseEntity<DepotATermeResponseDTO> souscrireDepotATerme(@Valid @RequestBody SouscrireDatRequestDTO dto) {
        SouscrireDatServiceRequestDTO serviceDto = new SouscrireDatServiceRequestDTO();
        serviceDto.setIdClient(dto.getIdClient());
        serviceDto.setIdProduitEpargne(dto.getIdProduitEpargne());
        serviceDto.setMontant(dto.getMontantSouscription().toString());
        serviceDto.setDureeMois(String.valueOf(dto.getDureeMois()));
        serviceDto.setDateSouscription(dto.getDateSouscription());
        serviceDto.setIdUtilisateurOperateur(dto.getIdGuichetier());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(epargneExtensionService.souscrireDepotATerme(serviceDto)));
    }

    @GetMapping("/depots-a-terme")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SAVINGS_VIEW)")
    public ResponseEntity<List<DepotATermeResponseDTO>> listerDepotsATerme() {
        return ResponseEntity.ok(epargneExtensionService.listerDepotsATerme().stream().map(this::toDto).toList());
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
