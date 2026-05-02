package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.ChangerStatutOrdreRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerLotCompensationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOperateurRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerOrdreCompensationRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerTransactionMobileMoneyRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerWalletRequestDTO;
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
    public ResponseEntity<List<OperateurMobileMoneyResponseDTO>> listerOperateurs() {
        return ResponseEntity.ok(paiementExterneService.listerOperateurs().stream().map(this::toDto).toList());
    }

    @PostMapping("/wallets")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_WALLET_CREATE", resource = "WALLET_CLIENT")
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
    public ResponseEntity<List<WalletClientResponseDTO>> listerWallets() {
        return ResponseEntity.ok(paiementExterneService.listerWallets().stream().map(this::toDto).toList());
    }

    @PostMapping("/mobile-money/transactions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_TRANSACTION_CREATE", resource = "TRANSACTION_MOBILE_MONEY")
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
    public ResponseEntity<List<TransactionMobileMoneyResponseDTO>> listerTransactionsMobileMoney() {
        return ResponseEntity.ok(paiementExterneService.listerTransactionsMobileMoney().stream().map(this::toDto).toList());
    }

    @PutMapping("/mobile-money/transactions/{idTransactionMobileMoney}/statut")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "MM_TRANSACTION_STATUS_UPDATE", resource = "TRANSACTION_MOBILE_MONEY")
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
    public ResponseEntity<List<LotCompensationResponseDTO>> listerLots() {
        return ResponseEntity.ok(paiementExterneService.listerLotsCompensation().stream().map(this::toDto).toList());
    }

    @PostMapping("/ordres-paiement")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_PAYMENTS_MANAGE)")
    @AuditLog(action = "EXTERNAL_PAYMENT_ORDER_CREATE", resource = "ORDRE_PAIEMENT_EXTERNE")
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
