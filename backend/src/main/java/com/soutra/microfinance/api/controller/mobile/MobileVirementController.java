package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.mobile.MobileOtpRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileVirementRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileVirementResponseDTO;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.Utilisateur;
import jakarta.persistence.EntityNotFoundException;
import com.soutra.microfinance.service.operation.TransactionService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mobile/virements")
@Tag(name = "Mobile Virements", description = "API de gestion des virements pour l'application mobile")
public class MobileVirementController {

    private final TransactionService transactionService;

    public MobileVirementController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Initier un virement", description = "Initie un virement depuis l'application mobile.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Virement initie avec succes"),
            @ApiResponse(responseCode = "202", description = "Virement en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable"),
            @ApiResponse(responseCode = "409", description = "Fonds insuffisants")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_VIREMENT_INITIER", resource = "VIREMENT")
    public ResponseEntity<MobileVirementResponseDTO> initierVirement(
            @Valid @RequestBody MobileVirementRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();

        Transaction transaction = transactionService.faireVirement(
                requestDTO.getCompteSource(),
                requestDTO.getCompteDestination(),
                requestDTO.getMontant(),
                utilisateur.getIdUser()
        );

        MobileVirementResponseDTO response = toVirementResponse(transaction);
        HttpStatus status = transaction.getStatutOperation() == StatutOperation.EN_ATTENTE
                ? HttpStatus.ACCEPTED
                : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }

    @Operation(summary = "Statut d'un virement", description = "Retourne le statut d'un virement par sa reference.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Virement introuvable")
    })
    @GetMapping("/{reference}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_VIREMENT_DETAIL", resource = "VIREMENT")
    public ResponseEntity<MobileVirementResponseDTO> statutVirement(
            @PathVariable String reference,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Transaction transaction = transactionService.getDetailTransaction(reference);

        if (!transaction.getUtilisateur().getIdUser().equals(utilisateur.getIdUser())) {
            throw new EntityNotFoundException("Virement introuvable");
        }

        return ResponseEntity.ok(toVirementResponse(transaction));
    }

    @Operation(summary = "Confirmer un virement par OTP", description = "Confirme un virement sensible par code OTP.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Virement confirme avec succes"),
            @ApiResponse(responseCode = "401", description = "Code OTP invalide"),
            @ApiResponse(responseCode = "404", description = "Virement introuvable")
    })
    @PostMapping("/{reference}/confirmer-otp")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_VIREMENT_CONFIRMER_OTP", resource = "VIREMENT")
    public ResponseEntity<MobileVirementResponseDTO> confirmerVirementOtp(
            @PathVariable String reference,
            @Valid @RequestBody MobileOtpRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Transaction transaction = transactionService.getDetailTransaction(reference);

        if (!transaction.getUtilisateur().getIdUser().equals(utilisateur.getIdUser())) {
            throw new EntityNotFoundException("Virement introuvable");
        }

        Transaction approuvee = transactionService.approuverTransaction(reference, utilisateur.getIdUser());
        return ResponseEntity.ok(toVirementResponse(approuvee));
    }

    @Operation(summary = "Annuler un virement", description = "Annule un virement en attente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Virement annule avec succes"),
            @ApiResponse(responseCode = "404", description = "Virement introuvable or deja execute")
    })
    @PostMapping("/{reference}/annuler")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_VIREMENT_ANNULER", resource = "VIREMENT")
    public ResponseEntity<MobileVirementResponseDTO> annulerVirement(
            @PathVariable String reference,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Transaction transaction = transactionService.getDetailTransaction(reference);

        if (!transaction.getUtilisateur().getIdUser().equals(utilisateur.getIdUser())) {
            throw new EntityNotFoundException("Virement introuvable");
        }

        Transaction rejetee = transactionService.rejeterTransaction(
                reference,
                utilisateur.getIdUser(),
                "Annule par le client mobile"
        );

        return ResponseEntity.ok(toVirementResponse(rejetee));
    }

    private MobileVirementResponseDTO toVirementResponse(Transaction transaction) {
        String statut = transaction.getStatutOperation() != null
                ? transaction.getStatutOperation().name()
                : "INCONNU";
        String message = switch (transaction.getStatutOperation()) {
            case EXECUTEE -> "Virement execute avec succes";
            case EN_ATTENTE -> "Virement en attente de confirmation";
            case REJETEE -> "Virement annule";
            default -> "Virement en cours de traitement";
        };

        return new MobileVirementResponseDTO(
                transaction.getReferenceUnique(),
                transaction.getMontantGlobal(),
                transaction.getCompteSource() != null ? transaction.getCompteSource().getNumCompte() : null,
                transaction.getCompteDestination() != null ? transaction.getCompteDestination().getNumCompte() : null,
                statut,
                message
        );
    }

}
