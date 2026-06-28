package com.soutra.microfinance.api.controller.operation;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.operation.PaiementCarteRequestDTO;
import com.soutra.microfinance.dto.request.operation.TransactionSimpleRequestDTO;
import com.soutra.microfinance.dto.request.operation.ValidationTransactionRequestDTO;
import com.soutra.microfinance.dto.request.operation.VirementRequestDTO;
import com.soutra.microfinance.dto.response.operation.LigneReleveResponseDTO;
import com.soutra.microfinance.dto.response.operation.RecuTransactionResponseDTO;
import com.soutra.microfinance.dto.response.operation.TransactionDetailResponseDTO;
import com.soutra.microfinance.dto.response.operation.TransactionEnAttenteResponseDTO;
import com.soutra.microfinance.dto.request.operation.MobileMoneyRequestDTO;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.OperationMapper;
import com.soutra.microfinance.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "API des operations bancaires")
public class TransactionController {

    private final TransactionService transactionService;
    private final OperationMapper operationMapper;

    public TransactionController(TransactionService transactionService, OperationMapper operationMapper) {
        this.transactionService = transactionService;
        this.operationMapper = operationMapper;
    }

    @Operation(
            summary = "Effectuer un depot",
            description = "Initie un depot; les montants sensibles passent par une validation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Depot execute avec succes"),
            @ApiResponse(responseCode = "202", description = "Depot en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit metier")
    })
    @PostMapping("/depot")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "TRANSACTION_DEPOSIT", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> faireDepot(
            @Valid @RequestBody TransactionSimpleRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        SoutraSecurityHelper.verifierCorrespondanceUtilisateur(requestDTO.getIdGuichetier(), utilisateurAuthentifie.getIdUser(), "guichetier");
        Transaction transaction = transactionService.faireDepot(
                requestDTO.getNumCompte(),
                requestDTO.getMontant(),
                utilisateurAuthentifie.getIdUser()
        );
        return construireReponseTransaction(transaction);
    }

    @Operation(
            summary = "Effectuer un retrait",
            description = "Initie un retrait; les montants sensibles passent par une validation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retrait execute avec succes"),
            @ApiResponse(responseCode = "202", description = "Retrait en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
            @ApiResponse(responseCode = "409", description = "Fonds insuffisants ou conflit metier")
    })
    @PostMapping("/retrait")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "TRANSACTION_WITHDRAWAL", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> faireRetrait(
            @Valid @RequestBody TransactionSimpleRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        SoutraSecurityHelper.verifierCorrespondanceUtilisateur(requestDTO.getIdGuichetier(), utilisateurAuthentifie.getIdUser(), "guichetier");
        Transaction transaction = transactionService.faireRetrait(
                requestDTO.getNumCompte(),
                requestDTO.getMontant(),
                utilisateurAuthentifie.getIdUser(),
                requestDTO.getNumeroCarte()
        );
        return construireReponseTransaction(transaction);
    }

    @Operation(
            summary = "Effectuer un virement",
            description = "Initie un virement; les montants sensibles passent par un workflow 4-eyes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Virement execute avec succes"),
            @ApiResponse(responseCode = "202", description = "Virement en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
            @ApiResponse(responseCode = "409", description = "Fonds insuffisants ou conflit metier")
    })
    @PostMapping("/virement")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "TRANSACTION_TRANSFER", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> faireVirement(
            @Valid @RequestBody VirementRequestDTO requestDTO,
            @RequestParam Long idGuichetier,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        SoutraSecurityHelper.verifierCorrespondanceUtilisateur(idGuichetier, utilisateurAuthentifie.getIdUser(), "guichetier");
        Transaction transaction = transactionService.faireVirement(
                requestDTO.getCompteSource(),
                requestDTO.getCompteDestination(),
                requestDTO.getMontant(),
                utilisateurAuthentifie.getIdUser()
        );
        return construireReponseTransaction(transaction);
    }

    @Operation(
            summary = "Approuver une transaction en attente",
            description = "Execute une transaction sensible apres validation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction approuvee et executee"),
            @ApiResponse(responseCode = "404", description = "Transaction ou superviseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Workflow de validation incompatible")
    })
    @PutMapping("/{referenceUnique}/approbation")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "TRANSACTION_APPROVAL", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> approuverTransaction(
            @PathVariable String referenceUnique,
            @Valid @RequestBody ValidationTransactionRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        SoutraSecurityHelper.verifierCorrespondanceUtilisateur(requestDTO.getIdSuperviseur(), utilisateurAuthentifie.getIdUser(), "superviseur");
        Transaction transaction = transactionService.approuverTransaction(referenceUnique, utilisateurAuthentifie.getIdUser());
        return ResponseEntity.ok(operationMapper.toRecuResponseDTO(transaction));
    }

    @Operation(
            summary = "Rejeter une transaction en attente",
            description = "Rejette une transaction sensible via le superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction rejetee"),
            @ApiResponse(responseCode = "404", description = "Transaction ou superviseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Workflow de validation incompatible")
    })
    @PutMapping("/{referenceUnique}/rejet")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "TRANSACTION_REJECTION", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> rejeterTransaction(
            @PathVariable String referenceUnique,
            @Valid @RequestBody ValidationTransactionRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        SoutraSecurityHelper.verifierCorrespondanceUtilisateur(requestDTO.getIdSuperviseur(), utilisateurAuthentifie.getIdUser(), "superviseur");
        Transaction transaction = transactionService.rejeterTransaction(
                referenceUnique,
                utilisateurAuthentifie.getIdUser(),
                requestDTO.getMotif()
        );
        return ResponseEntity.ok(operationMapper.toRecuResponseDTO(transaction));
    }

    @Operation(
            summary = "Paiement par carte VISA",
            description = "Debite le compte lie a la carte; verifie le statut, la date d'expiration et le plafond journalier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement execute avec succes"),
            @ApiResponse(responseCode = "202", description = "Paiement en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Carte invalide, expiree ou plafond depasse"),
            @ApiResponse(responseCode = "404", description = "Carte introuvable"),
            @ApiResponse(responseCode = "409", description = "Fonds insuffisants")
    })
    @PostMapping("/paiement-carte")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "TRANSACTION_CARD_PAYMENT", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> fairePaiementCarte(
            @Valid @RequestBody PaiementCarteRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        SoutraSecurityHelper.verifierCorrespondanceUtilisateur(requestDTO.getIdGuichetier(), utilisateurAuthentifie.getIdUser(), "guichetier");
        Transaction transaction = transactionService.fairePaiementCarte(
                requestDTO.getNumeroCarte(),
                requestDTO.getMontant(),
                utilisateurAuthentifie.getIdUser()
        );
        return construireReponseTransaction(transaction);
    }

    @Operation(
            summary = "Consulter l'historique d'un compte",
            description = "Retourne les lignes d'ecriture paginees associees a un compte"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique retourne avec succes"),
            @ApiResponse(responseCode = "400", description = "Parametres invalides"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/comptes/{numCompte}/historique")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER') or (hasAuthority('CLIENT') and @accountAccessSecurity.canAccessAccount(authentication, #numCompte))")
    public ResponseEntity<Page<LigneReleveResponseDTO>> consulterHistorique(
            @PathVariable String numCompte,
            @ParameterObject Pageable pageable
    ) {
        Page<LigneEcriture> pageLignes = transactionService.historiqueOperations(numCompte, pageable);
        Page<LigneReleveResponseDTO> pageReleve = pageLignes.map(operationMapper::toLigneReleveResponseDTO);
        return ResponseEntity.ok(pageReleve);
    }

    // ========== ENDPOINTS AVANCES (PRD-02) ==========

    @Operation(summary = "Lister toutes les transactions", description = "Retourne la liste paginée de toutes les transactions")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Liste des transactions") })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER','DIRECTEUR')")
    public ResponseEntity<Page<TransactionDetailResponseDTO>> listerToutesLesTransactions(@ParameterObject Pageable pageable) {
        Page<Transaction> transactions = transactionService.listerToutes(pageable);
        return ResponseEntity.ok(transactions.map(operationMapper::toDetailResponseDTO));
    }

    @Operation(summary = "Lister les transactions en attente 4-yeux")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Liste des transactions en attente") })
    @GetMapping("/en-attente")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
    public ResponseEntity<Page<TransactionEnAttenteResponseDTO>> listerEnAttente(@ParameterObject Pageable pageable) {
        Page<Transaction> transactions = transactionService.listerEnAttente(pageable);
        return ResponseEntity.ok(transactions.map(operationMapper::toEnAttenteResponseDTO));
    }

    @Operation(summary = "Detail d'une transaction", description = "Retourne les informations completes d'une transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail de la transaction"),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable")
    })
    @GetMapping("/{referenceUnique}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER')")
    public ResponseEntity<TransactionDetailResponseDTO> detailTransaction(@PathVariable String referenceUnique) {
        Transaction transaction = transactionService.getDetailTransaction(referenceUnique);
        return ResponseEntity.ok(operationMapper.toDetailResponseDTO(transaction));
    }

    @Operation(summary = "Reverser/annuler une transaction", description = "Inverse les effets comptables d'une transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction reversee"),
            @ApiResponse(responseCode = "400", description = "Transaction non reversible")
    })
    @PostMapping("/{referenceUnique}/reverser")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    @AuditLog(action = "TRANSACTION_REVERSE", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> reverserTransaction(
            @PathVariable String referenceUnique,
            @RequestParam(required = false) String motif,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Transaction transaction = transactionService.reverserTransaction(referenceUnique, utilisateur.getIdUser(), motif);
        return ResponseEntity.ok(operationMapper.toRecuResponseDTO(transaction));
    }

    @Operation(summary = "Generer le recu d'une transaction", description = "Retourne le recu formatte d'une transaction")
    @GetMapping("/{referenceUnique}/recu")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','CLIENT')")
    public ResponseEntity<RecuTransactionResponseDTO> recuTransaction(@PathVariable String referenceUnique) {
        Transaction transaction = transactionService.getDetailTransaction(referenceUnique);
        return ResponseEntity.ok(operationMapper.toRecuResponseDTO(transaction));
    }

    @Operation(summary = "Exporter les transactions", description = "Export CSV/PDF des transactions")
    @GetMapping("/export")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<byte[]> exporterTransactions(
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) LocalDateTime dateDebut,
            @RequestParam(required = false) LocalDateTime dateFin
    ) {
        String csv = transactionService.exporterTransactions(format, dateDebut, dateFin);
        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "transactions.csv");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @Operation(summary = "Depot Mobile Money", description = "Effectue un depot via Mobile Money (Wave, Orange Money, MTN)")
    @PostMapping("/mobile-money/depot")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "TRANSACTION_MOBILE_MONEY_DEPOSIT", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> depotMobileMoney(
            @Valid @RequestBody MobileMoneyRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Transaction transaction = transactionService.faireDepotMobileMoney(
                requestDTO.getNumCompte(), requestDTO.getMontant(),
                utilisateur.getIdUser(), requestDTO.getOperateur(), requestDTO.getTelephone()
        );
        return construireReponseTransaction(transaction);
    }

    @Operation(summary = "Retrait Mobile Money", description = "Effectue un retrait via Mobile Money")
    @PostMapping("/mobile-money/retrait")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "TRANSACTION_MOBILE_MONEY_WITHDRAWAL", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> retraitMobileMoney(
            @Valid @RequestBody MobileMoneyRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Transaction transaction = transactionService.faireRetraitMobileMoney(
                requestDTO.getNumCompte(), requestDTO.getMontant(),
                utilisateur.getIdUser(), requestDTO.getOperateur(), requestDTO.getTelephone()
        );
        return construireReponseTransaction(transaction);
    }

    @Operation(summary = "Lister les transactions Mobile Money")
    @GetMapping("/mobile-money")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
    public ResponseEntity<Page<RecuTransactionResponseDTO>> listerMobileMoney(@ParameterObject Pageable pageable) {
        Page<Transaction> transactions = transactionService.listerMobileMoney(pageable);
        return ResponseEntity.ok(transactions.map(operationMapper::toRecuResponseDTO));
    }

    private ResponseEntity<RecuTransactionResponseDTO> construireReponseTransaction(Transaction transaction) {
        HttpStatus status = transaction.getStatutOperation() == StatutOperation.EN_ATTENTE
                ? HttpStatus.ACCEPTED
                : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(operationMapper.toRecuResponseDTO(transaction));
    }
}
