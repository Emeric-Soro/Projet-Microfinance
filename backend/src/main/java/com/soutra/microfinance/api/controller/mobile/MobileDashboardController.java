package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.response.mobile.MobileDashboardResponseDTO;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.compte.CompteService;
import com.soutra.microfinance.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile")
@Tag(name = "Mobile Dashboard", description = "API du tableau de bord mobile")
public class MobileDashboardController {

    private final CompteService compteService;
    private final TransactionService transactionService;

    public MobileDashboardController(CompteService compteService, TransactionService transactionService) {
        this.compteService = compteService;
        this.transactionService = transactionService;
    }

    @Operation(summary = "Vue d'ensemble mobile", description = "Retourne le solde total, les dernieres operations et les alertes du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tableau de bord retourne avec succes")
    })
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_DASHBOARD_VIEW", resource = "DASHBOARD")
    public ResponseEntity<MobileDashboardResponseDTO> dashboard(Authentication authentication) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        List<Compte> comptes = compteService.listerComptesClient(idClient, PageRequest.of(0, 100)).getContent();
        BigDecimal soldeTotal = comptes.stream()
                .map(Compte::getSolde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PageRequest top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateHeureTransaction"));
        List<String> dernieresOperations = transactionService.listerTransactionsUtilisateur(utilisateur.getIdUser(), top5)
                .stream()
                .map(this::resumeOperation)
                .toList();

        List<String> alertes = new ArrayList<>();
        comptes.stream()
                .filter(c -> c.getSolde().compareTo(BigDecimal.ZERO) < 0)
                .forEach(c -> alertes.add("Compte " + c.getNumCompte() + " en decouvert (" + c.getSolde() + " FCFA)"));

        MobileDashboardResponseDTO response = new MobileDashboardResponseDTO(
                soldeTotal,
                comptes.size(),
                dernieresOperations,
                0,
                alertes
        );

        return ResponseEntity.ok(response);
    }

    private String resumeOperation(Transaction transaction) {
        return transaction.getTypeTransaction().getLibelle() + " - " + transaction.getMontantGlobal() + " FCFA";
    }

}
