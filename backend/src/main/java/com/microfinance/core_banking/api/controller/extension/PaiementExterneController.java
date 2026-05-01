package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.entity.LotCompensation;
import com.microfinance.core_banking.entity.OperateurMobileMoney;
import com.microfinance.core_banking.entity.OrdrePaiementExterne;
import com.microfinance.core_banking.entity.TransactionMobileMoney;
import com.microfinance.core_banking.entity.WalletClient;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.extension.PaiementExterneService;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','PAYMENTS_MANAGE')")
    @AuditLog(action = "MM_OPERATOR_CREATE", resource = "OPERATEUR_MOBILE_MONEY")
    public ResponseEntity<Map<String, Object>> creerOperateur(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_OPERATEUR_MOBILE_MONEY",
                "OPERATEUR_MOBILE_MONEY",
                payload.get("codeOperateur") == null ? null : payload.get("codeOperateur").toString(),
                payload,
                "Creation operateur mobile money soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/operateurs-mobile-money")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerOperateurs() {
        return ResponseEntity.ok(paiementExterneService.listerOperateurs().stream().map(this::toOperateurMap).toList());
    }

    @PostMapping("/wallets")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_MANAGE')")
    @AuditLog(action = "MM_WALLET_CREATE", resource = "WALLET_CLIENT")
    public ResponseEntity<Map<String, Object>> creerWallet(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_WALLET_CLIENT",
                "WALLET_CLIENT",
                payload.get("numeroWallet") == null ? null : payload.get("numeroWallet").toString(),
                payload,
                "Creation wallet client soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/wallets")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerWallets() {
        return ResponseEntity.ok(paiementExterneService.listerWallets().stream().map(this::toWalletMap).toList());
    }

    @PostMapping("/mobile-money/transactions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_MANAGE')")
    @AuditLog(action = "MM_TRANSACTION_CREATE", resource = "TRANSACTION_MOBILE_MONEY")
    public ResponseEntity<Map<String, Object>> creerTransactionMobileMoney(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_TRANSACTION_MOBILE_MONEY",
                "TRANSACTION_MOBILE_MONEY",
                payload.get("referenceTransaction") == null ? null : payload.get("referenceTransaction").toString(),
                payload,
                "Creation transaction mobile money soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/mobile-money/transactions")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerTransactionsMobileMoney() {
        return ResponseEntity.ok(paiementExterneService.listerTransactionsMobileMoney().stream().map(this::toTransactionMmMap).toList());
    }

    @PutMapping("/mobile-money/transactions/{idTransactionMobileMoney}/statut")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','PAYMENTS_MANAGE')")
    @AuditLog(action = "MM_TRANSACTION_STATUS_UPDATE", resource = "TRANSACTION_MOBILE_MONEY")
    public ResponseEntity<Map<String, Object>> changerStatutTransactionMobileMoney(
            @PathVariable Long idTransactionMobileMoney,
            @RequestBody Map<String, Object> payload
    ) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "UPDATE_STATUT_TRANSACTION_MOBILE_MONEY",
                "TRANSACTION_MOBILE_MONEY",
                String.valueOf(idTransactionMobileMoney),
                payload,
                "Changement de statut mobile money soumis"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PostMapping("/lots-compensation")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','PAYMENTS_MANAGE')")
    @AuditLog(action = "COMPENSATION_BATCH_CREATE", resource = "LOT_COMPENSATION")
    public ResponseEntity<Map<String, Object>> creerLot(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_LOT_COMPENSATION",
                "LOT_COMPENSATION",
                payload.get("referenceLot") == null ? null : payload.get("referenceLot").toString(),
                payload,
                "Creation lot de compensation soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/lots-compensation")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerLots() {
        return ResponseEntity.ok(paiementExterneService.listerLotsCompensation().stream().map(this::toLotMap).toList());
    }

    @PostMapping("/ordres-paiement")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_MANAGE')")
    @AuditLog(action = "EXTERNAL_PAYMENT_ORDER_CREATE", resource = "ORDRE_PAIEMENT_EXTERNE")
    public ResponseEntity<Map<String, Object>> creerOrdre(@RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "CREATE_ORDRE_PAIEMENT_EXTERNE",
                "ORDRE_PAIEMENT_EXTERNE",
                payload.get("referenceOrdre") == null ? null : payload.get("referenceOrdre").toString(),
                payload,
                "Creation ordre de paiement externe soumise"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @PutMapping("/ordres-paiement/{idOrdre}/statut")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','PAYMENTS_MANAGE')")
    @AuditLog(action = "EXTERNAL_PAYMENT_ORDER_UPDATE", resource = "ORDRE_PAIEMENT_EXTERNE")
    public ResponseEntity<Map<String, Object>> changerStatut(@PathVariable Long idOrdre, @RequestBody Map<String, Object> payload) {
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "UPDATE_STATUT_ORDRE_PAIEMENT_EXTERNE",
                "ORDRE_PAIEMENT_EXTERNE",
                String.valueOf(idOrdre),
                payload,
                "Changement de statut ordre externe soumis"
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionMap(action));
    }

    @GetMapping("/ordres-paiement")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','GUICHETIER','PAYMENTS_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> listerOrdres() {
        return ResponseEntity.ok(paiementExterneService.listerOrdresPaiement().stream().map(this::toOrdreMap).toList());
    }

    private Map<String, Object> toOperateurMap(OperateurMobileMoney operateur) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idOperateurMobileMoney", operateur.getIdOperateurMobileMoney());
        response.put("codeOperateur", operateur.getCodeOperateur());
        response.put("nomOperateur", operateur.getNomOperateur());
        response.put("statut", operateur.getStatut());
        return response;
    }

    private Map<String, Object> toWalletMap(WalletClient wallet) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idWalletClient", wallet.getIdWalletClient());
        response.put("idClient", wallet.getClient().getIdClient());
        response.put("client", wallet.getClient().getNom() + " " + wallet.getClient().getPrenom());
        response.put("operateur", wallet.getOperateurMobileMoney().getNomOperateur());
        response.put("numeroWallet", wallet.getNumeroWallet());
        response.put("compteSupport", wallet.getCompte().getNumCompte());
        response.put("statut", wallet.getStatut());
        return response;
    }

    private Map<String, Object> toTransactionMmMap(TransactionMobileMoney transactionMobileMoney) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idTransactionMobileMoney", transactionMobileMoney.getIdTransactionMobileMoney());
        response.put("referenceTransaction", transactionMobileMoney.getReferenceTransaction());
        response.put("wallet", transactionMobileMoney.getWalletClient().getNumeroWallet());
        response.put("typeTransaction", transactionMobileMoney.getTypeTransaction());
        response.put("montant", transactionMobileMoney.getMontant());
        response.put("frais", transactionMobileMoney.getFrais());
        response.put("referenceTransactionInterne", transactionMobileMoney.getReferenceTransactionInterne());
        response.put("statut", transactionMobileMoney.getStatut());
        return response;
    }

    private Map<String, Object> toLotMap(LotCompensation lot) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idLotCompensation", lot.getIdLotCompensation());
        response.put("referenceLot", lot.getReferenceLot());
        response.put("typeLot", lot.getTypeLot());
        response.put("dateTraitement", lot.getDateTraitement());
        response.put("statut", lot.getStatut());
        response.put("commentaire", lot.getCommentaire());
        return response;
    }

    private Map<String, Object> toOrdreMap(OrdrePaiementExterne ordre) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idOrdrePaiementExterne", ordre.getIdOrdrePaiementExterne());
        response.put("referenceOrdre", ordre.getReferenceOrdre());
        response.put("typeFlux", ordre.getTypeFlux());
        response.put("sens", ordre.getSens());
        response.put("montant", ordre.getMontant());
        response.put("frais", ordre.getFrais());
        response.put("compte", ordre.getCompte().getNumCompte());
        response.put("lotCompensation", ordre.getLotCompensation() == null ? null : ordre.getLotCompensation().getReferenceLot());
        response.put("referenceExterne", ordre.getReferenceExterne());
        response.put("referenceTransactionInterne", ordre.getReferenceTransactionInterne());
        response.put("destinationDetail", ordre.getDestinationDetail());
        response.put("dateInitiation", ordre.getDateInitiation());
        response.put("dateReglement", ordre.getDateReglement());
        response.put("dateRapprochement", ordre.getDateRapprochement());
        response.put("statut", ordre.getStatut());
        return response;
    }

    private Map<String, Object> toActionMap(ActionEnAttente action) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("idActionEnAttente", action.getIdActionEnAttente());
        response.put("typeAction", action.getTypeAction());
        response.put("ressource", action.getRessource());
        response.put("statut", action.getStatut());
        response.put("referenceRessource", action.getReferenceRessource());
        return response;
    }
}
