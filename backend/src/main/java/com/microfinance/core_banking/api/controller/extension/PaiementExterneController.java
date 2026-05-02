package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.ChangerStatutOrdreRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerLotCompensationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOperateurRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOrdreCompensationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerTransactionMobileMoneyRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerWalletRequestDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.LotCompensationResponseDTO;
import com.microfinance.core_banking.dto.response.extension.OperateurMobileMoneyResponseDTO;
import com.microfinance.core_banking.dto.response.extension.OrdrePaiementExterneResponseDTO;
import com.microfinance.core_banking.dto.response.extension.TransactionMobileMoneyResponseDTO;
import com.microfinance.core_banking.dto.response.extension.WalletClientResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.LotCompensation;
import com.microfinance.core_banking.entity.OperateurMobileMoney;
import com.microfinance.core_banking.entity.OrdrePaiementExterne;
import com.microfinance.core_banking.entity.TransactionMobileMoney;
import com.microfinance.core_banking.entity.WalletClient;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paiements-externes")
@Tag(name = "Paiements Externes", description = "API de gestion des paiements externes, mobile money, wallets, compensation et ordres de paiement")
public class PaiementExterneController {

    private final PaiementExterneService paiementExterneService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public PaiementExterneController(
            PaiementExterneService paiementExterneService,
            PendingActionSubmissionService pendingActionSubmissionService
    ) {
        this.paiementExterneService = paiementExterneService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @PostMapping("/operateurs-mobile-money")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_OPERATOR_CREATE", resource = "OPERATEUR_MOBILE_MONEY")
    @Operation(summary = "Créer un opérateur mobile money", description = "Soumet la création d'un nouvel opérateur mobile money au workflow Maker-Checker. L'opérateur est identifié par son code et son nom.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerOperateur(@Valid @RequestBody CreerOperateurRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_OPERATEUR_MOBILE_MONEY",
                "OPERATEUR_MOBILE_MONEY",
                dto.getCodeOperateur(),
                dto,
                "Creation operateur mobile money soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/operateurs-mobile-money")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_VIEW)")
    @Operation(summary = "Lister les opérateurs mobile money", description = "Retourne la liste de tous les opérateurs mobile money configurés. Chaque opérateur inclut son code, son nom et son statut opérationnel.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des opérateurs mobile money", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperateurMobileMoneyResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<OperateurMobileMoneyResponseDTO>> listerOperateurs() {
        return ResponseEntity.ok(paiementExterneService.listerOperateurs().stream().map(this::toDto).toList());
    }

    @PostMapping("/wallets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_WALLET_CREATE", resource = "WALLET_CLIENT")
    @Operation(summary = "Créer un wallet client", description = "Soumet la création d'un wallet client au workflow Maker-Checker. Le wallet est lié à un client et à un opérateur mobile money.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerWallet(@Valid @RequestBody CreerWalletRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_WALLET_CLIENT",
                "WALLET_CLIENT",
                null,
                dto,
                "Creation wallet client soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/wallets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_VIEW)")
    @Operation(summary = "Lister les wallets clients", description = "Retourne la liste de tous les wallets clients. Chaque wallet affiche le client associé, l'opérateur mobile money, le numéro de wallet et le compte support.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des wallets clients", content = @Content(array = @ArraySchema(schema = @Schema(implementation = WalletClientResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<WalletClientResponseDTO>> listerWallets() {
        return ResponseEntity.ok(paiementExterneService.listerWallets().stream().map(this::toDto).toList());
    }

    @PostMapping("/mobile-money/transactions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_TRANSACTION_CREATE", resource = "TRANSACTION_MOBILE_MONEY")
    @Operation(summary = "Créer une transaction mobile money", description = "Soumet la création d'une transaction mobile money au workflow Maker-Checker. La transaction peut être un dépôt, un retrait ou un transfert sur le wallet du client.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerTransactionMobileMoney(@Valid @RequestBody CreerTransactionMobileMoneyRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_TRANSACTION_MOBILE_MONEY",
                "TRANSACTION_MOBILE_MONEY",
                null,
                dto,
                "Creation transaction mobile money soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/mobile-money/transactions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_VIEW)")
    @Operation(summary = "Lister les transactions mobile money", description = "Retourne la liste de toutes les transactions mobile money. Chaque transaction inclut sa référence, le wallet, le type, le montant, les frais et son statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des transactions mobile money", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionMobileMoneyResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<TransactionMobileMoneyResponseDTO>> listerTransactionsMobileMoney() {
        return ResponseEntity.ok(paiementExterneService.listerTransactionsMobileMoney().stream().map(this::toDto).toList());
    }

    @PutMapping("/mobile-money/transactions/{idTransactionMobileMoney}/statut")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_TRANSACTION_STATUS_UPDATE", resource = "TRANSACTION_MOBILE_MONEY")
    @Operation(summary = "Changer le statut d'une transaction mobile money", description = "Soumet le changement de statut d'une transaction mobile money au workflow Maker-Checker. Permet de marquer une transaction comme confirmée, échouée ou remboursée.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - modification soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Transaction mobile money non trouvée", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> changerStatutTransactionMobileMoney(
            @PathVariable Long idTransactionMobileMoney,
            @Valid @RequestBody ChangerStatutOrdreRequestDTO dto
    ) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "UPDATE_STATUT_TRANSACTION_MOBILE_MONEY",
                "TRANSACTION_MOBILE_MONEY",
                String.valueOf(idTransactionMobileMoney),
                dto,
                "Changement de statut mobile money soumis"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @PostMapping("/lots-compensation")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "COMPENSATION_BATCH_CREATE", resource = "LOT_COMPENSATION")
    @Operation(summary = "Créer un lot de compensation", description = "Soumet la création d'un lot de compensation au workflow Maker-Checker. Le lot regroupe plusieurs ordres de paiement pour un traitement groupé.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerLot(@Valid @RequestBody CreerLotCompensationRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_LOT_COMPENSATION",
                "LOT_COMPENSATION",
                null,
                dto,
                "Creation lot de compensation soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/lots-compensation")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_VIEW)")
    @Operation(summary = "Lister les lots de compensation", description = "Retourne la liste de tous les lots de compensation. Chaque lot affiche sa référence, son type, sa date de traitement, son commentaire et son statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des lots de compensation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LotCompensationResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<LotCompensationResponseDTO>> listerLots() {
        return ResponseEntity.ok(paiementExterneService.listerLotsCompensation().stream().map(this::toDto).toList());
    }

    @PostMapping("/ordres-paiement")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "EXTERNAL_PAYMENT_ORDER_CREATE", resource = "ORDRE_PAIEMENT_EXTERNE")
    @Operation(summary = "Créer un ordre de paiement externe", description = "Soumet la création d'un ordre de paiement externe au workflow Maker-Checker. L'ordre précise le type de flux, le montant, les frais et les détails de destination.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - création soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> creerOrdre(@Valid @RequestBody CreerOrdreCompensationRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_ORDRE_PAIEMENT_EXTERNE",
                "ORDRE_PAIEMENT_EXTERNE",
                null,
                dto,
                "Creation ordre de paiement externe soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @PutMapping("/ordres-paiement/{idOrdre}/statut")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "EXTERNAL_PAYMENT_ORDER_UPDATE", resource = "ORDRE_PAIEMENT_EXTERNE")
    @Operation(summary = "Changer le statut d'un ordre de paiement", description = "Soumet le changement de statut d'un ordre de paiement externe au workflow Maker-Checker. Permet de faire évoluer l'ordre vers les statuts suivants : initié, traité, réglé ou annulé.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Accepté - modification soumise au workflow Maker-Checker", content = @Content(schema = @Schema(implementation = ActionEnAttenteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide - erreur de validation", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Ordre de paiement non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ActionEnAttenteResponseDTO> changerStatut(@PathVariable Long idOrdre, @Valid @RequestBody ChangerStatutOrdreRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "UPDATE_STATUT_ORDRE_PAIEMENT_EXTERNE",
                "ORDRE_PAIEMENT_EXTERNE",
                String.valueOf(idOrdre),
                dto,
                "Changement de statut ordre externe soumis"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @GetMapping("/ordres-paiement")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_VIEW)")
    @Operation(summary = "Lister les ordres de paiement", description = "Retourne la liste de tous les ordres de paiement externes. Chaque ordre inclut sa référence, le type de flux, le sens, le montant, les frais, la destination et le statut.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des ordres de paiement", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrdrePaiementExterneResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<OrdrePaiementExterneResponseDTO>> listerOrdres() {
        return ResponseEntity.ok(paiementExterneService.listerOrdresPaiement().stream().map(this::toDto).toList());
    }

    private OperateurMobileMoneyResponseDTO toDto(OperateurMobileMoney operateur) {
        OperateurMobileMoneyResponseDTO dto = new OperateurMobileMoneyResponseDTO();
        dto.setIdOperateurMobileMoney(operateur.getIdOperateurMobileMoney());
        dto.setCodeOperateur(operateur.getCodeOperateur());
        dto.setNomOperateur(operateur.getNomOperateur());
        dto.setStatut(operateur.getStatut());
        return dto;
    }

    private WalletClientResponseDTO toDto(WalletClient wallet) {
        WalletClientResponseDTO dto = new WalletClientResponseDTO();
        dto.setIdWalletClient(wallet.getIdWalletClient());
        dto.setIdClient(wallet.getClient().getIdClient());
        dto.setClient(wallet.getClient().getNom() + " " + wallet.getClient().getPrenom());
        dto.setOperateur(wallet.getOperateurMobileMoney().getNomOperateur());
        dto.setNumeroWallet(wallet.getNumeroWallet());
        dto.setCompteSupport(wallet.getCompte().getNumCompte());
        dto.setStatut(wallet.getStatut());
        return dto;
    }

    private TransactionMobileMoneyResponseDTO toDto(TransactionMobileMoney transactionMobileMoney) {
        TransactionMobileMoneyResponseDTO dto = new TransactionMobileMoneyResponseDTO();
        dto.setIdTransactionMobileMoney(transactionMobileMoney.getIdTransactionMobileMoney());
        dto.setReferenceTransaction(transactionMobileMoney.getReferenceTransaction());
        dto.setWallet(transactionMobileMoney.getWalletClient().getNumeroWallet());
        dto.setTypeTransaction(transactionMobileMoney.getTypeTransaction());
        dto.setMontant(transactionMobileMoney.getMontant());
        dto.setFrais(transactionMobileMoney.getFrais());
        dto.setReferenceTransactionInterne(transactionMobileMoney.getReferenceTransactionInterne());
        dto.setStatut(transactionMobileMoney.getStatut());
        return dto;
    }

    private LotCompensationResponseDTO toDto(LotCompensation lot) {
        LotCompensationResponseDTO dto = new LotCompensationResponseDTO();
        dto.setIdLotCompensation(lot.getIdLotCompensation());
        dto.setReferenceLot(lot.getReferenceLot());
        dto.setTypeLot(lot.getTypeLot());
        dto.setDateTraitement(lot.getDateTraitement());
        dto.setStatut(lot.getStatut());
        dto.setCommentaire(lot.getCommentaire());
        return dto;
    }

    private OrdrePaiementExterneResponseDTO toDto(OrdrePaiementExterne ordre) {
        OrdrePaiementExterneResponseDTO dto = new OrdrePaiementExterneResponseDTO();
        dto.setIdOrdrePaiementExterne(ordre.getIdOrdrePaiementExterne());
        dto.setReferenceOrdre(ordre.getReferenceOrdre());
        dto.setTypeFlux(ordre.getTypeFlux());
        dto.setSens(ordre.getSens());
        dto.setMontant(ordre.getMontant());
        dto.setFrais(ordre.getFrais());
        dto.setCompte(ordre.getCompte().getNumCompte());
        dto.setLotCompensation(ordre.getLotCompensation() == null ? null : ordre.getLotCompensation().getReferenceLot());
        dto.setReferenceExterne(ordre.getReferenceExterne());
        dto.setReferenceTransactionInterne(ordre.getReferenceTransactionInterne());
        dto.setDestinationDetail(ordre.getDestinationDetail());
        dto.setDateInitiation(ordre.getDateInitiation());
        dto.setDateReglement(ordre.getDateReglement());
        dto.setDateRapprochement(ordre.getDateRapprochement());
        dto.setStatut(ordre.getStatut());
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
