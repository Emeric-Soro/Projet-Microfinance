package com.microfinance.core_banking.api.controller.operation;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.operation.ActionTransactionRequestDTO;
import com.microfinance.core_banking.dto.request.operation.TransactionSimpleRequestDTO;
import com.microfinance.core_banking.dto.request.operation.ValidationTransactionRequestDTO;
import com.microfinance.core_banking.dto.request.operation.VirementRequestDTO;
import com.microfinance.core_banking.dto.response.operation.LigneReleveResponseDTO;
import com.microfinance.core_banking.dto.response.operation.RecuTransactionResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.mapper.OperationMapper;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import com.microfinance.core_banking.service.operation.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "API des operations bancaires")
public class TransactionController {

    private final TransactionService transactionService;
    private final OperationMapper operationMapper;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public TransactionController(TransactionService transactionService, OperationMapper operationMapper, PendingActionSubmissionService pendingActionSubmissionService) {
        this.transactionService = transactionService;
        this.operationMapper = operationMapper;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(
            summary = "Effectuer un depot",
            description = "Initie un depot; les montants sensibles passent par une validation superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Depot execute avec succes"),
            @ApiResponse(responseCode = "202", description = "Depot en attente de validation"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflit metier", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/depot")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "TRANSACTION_DEPOSIT", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> faireDepot(
            @Valid @RequestBody TransactionSimpleRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = extraireUtilisateurAuthentifie(authentication);
        verifierCorrespondanceUtilisateur(requestDTO.getIdGuichetier(), utilisateurAuthentifie.getIdUser(), "guichetier");
        Transaction transaction = transactionService.faireDepot(
                requestDTO.getNumCompte(),
                requestDTO.getMontant(),
                utilisateurAuthentifie.getIdUser(),
                requestDTO.getIdSessionCaisse()
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
            @ApiResponse(responseCode = "400", description = "Donnees invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Fonds insuffisants ou conflit metier", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/retrait")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "TRANSACTION_WITHDRAWAL", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> faireRetrait(
            @Valid @RequestBody TransactionSimpleRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = extraireUtilisateurAuthentifie(authentication);
        verifierCorrespondanceUtilisateur(requestDTO.getIdGuichetier(), utilisateurAuthentifie.getIdUser(), "guichetier");
        Transaction transaction = transactionService.faireRetrait(
                requestDTO.getNumCompte(),
                requestDTO.getMontant(),
                utilisateurAuthentifie.getIdUser(),
                requestDTO.getIdSessionCaisse()
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
            @ApiResponse(responseCode = "400", description = "Donnees invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Fonds insuffisants ou conflit metier", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/virement")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER)")
    @AuditLog(action = "TRANSACTION_TRANSFER", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> faireVirement(
            @Valid @RequestBody VirementRequestDTO requestDTO,
            @RequestParam Long idGuichetier,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = extraireUtilisateurAuthentifie(authentication);
        verifierCorrespondanceUtilisateur(idGuichetier, utilisateurAuthentifie.getIdUser(), "guichetier");
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
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction ou superviseur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Workflow de validation incompatible", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{referenceUnique}/approbation")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @AuditLog(action = "TRANSACTION_APPROVAL", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> approuverTransaction(
            @PathVariable String referenceUnique,
            @Valid @RequestBody ValidationTransactionRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = extraireUtilisateurAuthentifie(authentication);
        verifierCorrespondanceUtilisateur(requestDTO.getIdSuperviseur(), utilisateurAuthentifie.getIdUser(), "superviseur");
        Transaction transaction = transactionService.approuverTransaction(referenceUnique, utilisateurAuthentifie.getIdUser());
        return ResponseEntity.ok(operationMapper.toRecuResponseDTO(transaction));
    }

    @Operation(
            summary = "Rejeter une transaction en attente",
            description = "Rejette une transaction sensible via le superviseur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction rejetee"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction ou superviseur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Workflow de validation incompatible", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{referenceUnique}/rejet")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @AuditLog(action = "TRANSACTION_REJECTION", resource = "TRANSACTION")
    public ResponseEntity<RecuTransactionResponseDTO> rejeterTransaction(
            @PathVariable String referenceUnique,
            @Valid @RequestBody ValidationTransactionRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateurAuthentifie = extraireUtilisateurAuthentifie(authentication);
        verifierCorrespondanceUtilisateur(requestDTO.getIdSuperviseur(), utilisateurAuthentifie.getIdUser(), "superviseur");
        Transaction transaction = transactionService.rejeterTransaction(
                referenceUnique,
                utilisateurAuthentifie.getIdUser(),
                requestDTO.getMotif()
        );
        return ResponseEntity.ok(operationMapper.toRecuResponseDTO(transaction));
    }

    @Operation(
            summary = "Soumettre une annulation d'operation",
            description = "Soumet une demande d'annulation d'operation pour approbation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Annulation soumise en attente de validation"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Transaction non annulable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{referenceUnique}/annulation/soumettre")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @AuditLog(action = "TRANSACTION_CANCEL_SUBMIT", resource = "TRANSACTION")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreAnnulation(
            @PathVariable String referenceUnique,
            @Valid @RequestBody ActionTransactionRequestDTO requestDTO
    ) {
        ActionEnAttente action = pendingActionSubmissionService.submit("ANNULATION_OPERATION", "TRANSACTION", referenceUnique, requestDTO, "Annulation d'operation soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(
            summary = "Soumettre un extourne d'operation",
            description = "Soumet une demande d'extourne d'operation pour approbation"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Extourne soumis en attente de validation"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Transaction non extournable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{referenceUnique}/extourne/soumettre")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR)")
    @AuditLog(action = "TRANSACTION_REVERSAL_SUBMIT", resource = "TRANSACTION")
    public ResponseEntity<ActionEnAttenteResponseDTO> soumettreExtourne(
            @PathVariable String referenceUnique,
            @Valid @RequestBody ActionTransactionRequestDTO requestDTO
    ) {
        ActionEnAttente action = pendingActionSubmissionService.submit("EXTOURNE_OPERATION", "TRANSACTION", referenceUnique, requestDTO, "Extourne d'operation soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(
            summary = "Consulter l'historique d'un compte",
            description = "Retourne les lignes d'ecriture paginees associees a un compte"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique retourne avec succes"),
            @ApiResponse(responseCode = "400", description = "Parametres invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Compte introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/comptes/{numCompte}/historique")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN, T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER) or (hasAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_CLIENT) and @accountAccessSecurity.canAccessAccount(authentication, #numCompte))")
    public ResponseEntity<Page<LigneReleveResponseDTO>> consulterHistorique(
            @PathVariable String numCompte,
            @ParameterObject Pageable pageable
    ) {
        Page<LigneEcriture> pageLignes = transactionService.historiqueOperations(numCompte, pageable);
        Page<LigneReleveResponseDTO> pageReleve = pageLignes.map(operationMapper::toLigneReleveResponseDTO);
        return ResponseEntity.ok(pageReleve);
    }

    private ResponseEntity<RecuTransactionResponseDTO> construireReponseTransaction(Transaction transaction) {
        HttpStatus status = transaction.getStatutOperation() == StatutOperation.EN_ATTENTE
                ? HttpStatus.ACCEPTED
                : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(operationMapper.toRecuResponseDTO(transaction));
    }

    private Utilisateur extraireUtilisateurAuthentifie(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Utilisateur utilisateur)) {
            throw new IllegalStateException("Utilisateur authentifie introuvable");
        }
        return utilisateur;
    }

    private void verifierCorrespondanceUtilisateur(Long idRequete, Long idAuthentifie, String roleMetier) {
        if (idRequete == null || idAuthentifie == null || !idRequete.equals(idAuthentifie)) {
            throw new IllegalArgumentException("L'identifiant " + roleMetier + " doit correspondre a l'utilisateur authentifie");
        }
    }

    private ActionEnAttenteResponseDTO toActionDto(ActionEnAttente action) {
        ActionEnAttenteResponseDTO dto = new ActionEnAttenteResponseDTO();
        dto.setIdActionEnAttente(action.getIdActionEnAttente());
        dto.setTypeAction(action.getTypeAction());
        dto.setRessource(action.getRessource());
        dto.setReferenceRessource(action.getReferenceRessource());
        dto.setStatut(action.getStatut());
        return dto;
    }
}
