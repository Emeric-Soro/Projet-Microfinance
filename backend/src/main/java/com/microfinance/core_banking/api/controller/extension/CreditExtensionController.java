package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerDemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerProduitCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DebloquerCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DeciderDemandeCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EnregistrerGarantieRequestDTO;
import com.microfinance.core_banking.dto.request.extension.PassagePerteCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ReportEcheanceCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RembourserCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RestructurationCreditRequestDTO;
import com.microfinance.core_banking.dto.response.extension.ActionEnAttenteResponseDTO;
import com.microfinance.core_banking.dto.response.extension.CreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.DemandeCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.EcheanceCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.GarantieCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ImpayeCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ProduitCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.ProvisionCreditResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RemboursementCreditResponseDTO;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.EcheanceCredit;
import com.microfinance.core_banking.entity.GarantieCredit;
import com.microfinance.core_banking.entity.ImpayeCredit;
import com.microfinance.core_banking.entity.ProvisionCredit;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.RemboursementCredit;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.service.extension.PendingActionSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/credits")
public class CreditExtensionController {

    private final CreditExtensionService creditExtensionService;
    private final PendingActionSubmissionService pendingActionSubmissionService;

    public CreditExtensionController(CreditExtensionService creditExtensionService, PendingActionSubmissionService pendingActionSubmissionService) {
        this.creditExtensionService = creditExtensionService;
        this.pendingActionSubmissionService = pendingActionSubmissionService;
    }

    @Operation(summary = "Creer un produit de credit", description = "Cree un nouveau produit de credit (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation produit credit soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_PRODUCT_CREATE", resource = "PRODUIT_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> creerProduit(@Valid @RequestBody CreerProduitCreditRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_PRODUIT_CREDIT", "PRODUIT_CREDIT", dto.getCodeProduit(), dto, "Creation produit credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les produits de credit", description = "Retourne la liste de tous les produits de credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<ProduitCreditResponseDTO>> listerProduits() {
        return ResponseEntity.ok(creditExtensionService.listerProduits().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Creer une demande de credit", description = "Cree une nouvelle demande de credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Demande de credit creee avec succes"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PostMapping("/demandes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_REQUEST_CREATE", resource = "DEMANDE_CREDIT")
    public ResponseEntity<DemandeCreditResponseDTO> creerDemande(@Valid @RequestBody CreerDemandeCreditRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(creditExtensionService.creerDemande(dto)));
    }

    @Operation(summary = "Lister les demandes de credit", description = "Retourne la liste de toutes les demandes de credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des demandes retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping("/demandes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<DemandeCreditResponseDTO>> listerDemandes() {
        return ResponseEntity.ok(creditExtensionService.listerDemandes().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Decider une demande de credit", description = "Approuve ou rejette une demande de credit (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Decision soumise en attente de validation"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Demande introuvable")
    })
    @PutMapping("/demandes/{idDemande}/decision")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_REQUEST_DECISION", resource = "DEMANDE_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> deciderDemande(@PathVariable Long idDemande, @Valid @RequestBody DeciderDemandeCreditRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DECISION_DEMANDE_CREDIT", "DEMANDE_CREDIT", String.valueOf(idDemande), dto, "Decision demande credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Debloquer un credit", description = "Debloque les fonds d'un credit approuve (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Deblocage soumis en attente de validation"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Demande introuvable")
    })
    @PostMapping("/demandes/{idDemande}/deblocage")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_DISBURSE", resource = "CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> debloquerCredit(@PathVariable Long idDemande, @Valid @RequestBody DebloquerCreditRequestDTO dto) {
        dto.setIdDemande(idDemande);
        ActionEnAttente action = pendingActionSubmissionService.submit("DEBLOCAGE_CREDIT", "CREDIT", String.valueOf(idDemande), dto, "Deblocage credit soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Restructurer un credit", description = "Soumet une restructuration de credit pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Restructuration soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @PutMapping("/{idCredit}/restructuration")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_RESTRUCTURE_SUBMIT", resource = "CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> restructurerCredit(@PathVariable Long idCredit, @Valid @RequestBody RestructurationCreditRequestDTO dto) {
        dto.setIdCredit(idCredit);
        ActionEnAttente action = pendingActionSubmissionService.submit("RESTRUCTURATION_CREDIT", "CREDIT", String.valueOf(idCredit), dto, "Restructuration credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Reporter une echeance de credit", description = "Soumet un report d'echeance de credit pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Report d'echeance soumis en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit ou echeance introuvable")
    })
    @PutMapping("/{idCredit}/echeances/{idEcheanceCredit}/report")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_INSTALLMENT_DEFER_SUBMIT", resource = "ECHEANCE_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> reporterEcheance(@PathVariable Long idCredit, @PathVariable Long idEcheanceCredit, @Valid @RequestBody ReportEcheanceCreditRequestDTO dto) {
        dto.setIdCredit(idCredit);
        dto.setIdEcheanceCredit(idEcheanceCredit);
        ActionEnAttente action = pendingActionSubmissionService.submit("REPORT_ECHEANCE_CREDIT", "ECHEANCE_CREDIT", String.valueOf(idEcheanceCredit), dto, "Report d'echeance credit soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Passer un credit en perte", description = "Soumet un passage en perte de credit pour approbation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Passage en perte soumis en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @PutMapping("/{idCredit}/passage-perte")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_WRITE_OFF_SUBMIT", resource = "CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> passerEnPerte(@PathVariable Long idCredit, @Valid @RequestBody PassagePerteCreditRequestDTO dto) {
        dto.setIdCredit(idCredit);
        ActionEnAttente action = pendingActionSubmissionService.submit("PASSAGE_PERTE_CREDIT", "CREDIT", String.valueOf(idCredit), dto, "Passage en perte soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les credits", description = "Retourne la liste de tous les credits")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des credits retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<CreditResponseDTO>> listerCredits() {
        return ResponseEntity.ok(creditExtensionService.listerCredits().stream().map(this::toDto).toList());
    }

    @Operation(summary = "Lister les echeances d'un credit", description = "Retourne le calendrier des echeances pour un credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des echeances retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}/echeances")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<EcheanceCreditResponseDTO>> listerEcheances(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerEcheances(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Enregistrer une garantie", description = "Enregistre une garantie pour un credit (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Creation garantie soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @PostMapping("/{idCredit}/garanties")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_COLLATERAL_CREATE", resource = "GARANTIE_CREDIT")
    public ResponseEntity<ActionEnAttenteResponseDTO> enregistrerGarantie(@PathVariable Long idCredit, @Valid @RequestBody EnregistrerGarantieRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CREATE_GARANTIE_CREDIT", "GARANTIE_CREDIT", String.valueOf(idCredit), dto, "Creation garantie credit soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(toActionDto(action));
    }

    @Operation(summary = "Lister les garanties d'un credit", description = "Retourne la liste des garanties associees a un credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des garanties retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}/garanties")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<GarantieCreditResponseDTO>> listerGaranties(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerGaranties(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Rembourser un credit", description = "Effectue un remboursement sur un credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Remboursement effectue avec succes"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @PostMapping("/{idCredit}/remboursements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_REPAYMENT_CREATE", resource = "REMBOURSEMENT_CREDIT")
    public ResponseEntity<RemboursementCreditResponseDTO> rembourserCredit(@PathVariable Long idCredit, @Valid @RequestBody RembourserCreditRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(creditExtensionService.rembourserCredit(idCredit, dto)));
    }

    @Operation(summary = "Lister les remboursements d'un credit", description = "Retourne l'historique des remboursements d'un credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des remboursements retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}/remboursements")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<RemboursementCreditResponseDTO>> listerRemboursements(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerRemboursements(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Detecter les impayes", description = "Lance la detection des impayes sur les credits (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Detection impayes soumise en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/impayes/detection")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_OVERDUE_DETECT", resource = "IMPAYE_CREDIT")
    public ResponseEntity<List<ActionEnAttenteResponseDTO>> detecterImpayes(@Valid @RequestBody DetecterImpayesRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("DETECTION_IMPAYE_CREDIT", "IMPAYE_CREDIT", null, dto, "Detection impayes soumise");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(List.of(toActionDto(action)));
    }

    @Operation(summary = "Lister les impayes d'un credit", description = "Retourne la liste des impayes pour un credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des impayes retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}/impayes")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<ImpayeCreditResponseDTO>> listerImpayes(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerImpayes(idCredit).stream().map(this::toDto).toList());
    }

    @Operation(summary = "Calculer les provisions", description = "Lance le calcul des provisions sur les credits (soumis pour approbation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Calcul provisions soumis en attente"),
        @ApiResponse(responseCode = "400", description = "Donnees invalides"),
        @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    @PostMapping("/provisions/calcul")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_MANAGE)")
    @AuditLog(action = "CREDIT_PROVISION_CALCULATE", resource = "PROVISION_CREDIT")
    public ResponseEntity<List<ActionEnAttenteResponseDTO>> calculerProvisions(@Valid @RequestBody CalculerProvisionsRequestDTO dto) {
        ActionEnAttente action = pendingActionSubmissionService.submit("CALCUL_PROVISION_CREDIT", "PROVISION_CREDIT", null, dto, "Calcul provisions soumis");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(List.of(toActionDto(action)));
    }

    @Operation(summary = "Lister les provisions d'un credit", description = "Retourne l'historique des provisions pour un credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des provisions retournee avec succes"),
        @ApiResponse(responseCode = "403", description = "Acces refuse"),
        @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}/provisions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_GUICHETIER,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_CREDIT_VIEW)")
    public ResponseEntity<List<ProvisionCreditResponseDTO>> listerProvisions(@PathVariable Long idCredit) {
        return ResponseEntity.ok(creditExtensionService.listerProvisions(idCredit).stream().map(this::toDto).toList());
    }

    private ProduitCreditResponseDTO toDto(ProduitCredit produit) {
        ProduitCreditResponseDTO dto = new ProduitCreditResponseDTO();
        dto.setIdProduitCredit(produit.getIdProduitCredit());
        dto.setCodeProduit(produit.getCodeProduit());
        dto.setLibelle(produit.getLibelle());
        dto.setCategorie(produit.getCategorie());
        dto.setTauxAnnuel(produit.getTauxAnnuel());
        dto.setMontantMin(produit.getMontantMin());
        dto.setMontantMax(produit.getMontantMax());
        dto.setStatut(produit.getStatut());
        return dto;
    }

    private DemandeCreditResponseDTO toDto(DemandeCredit demande) {
        DemandeCreditResponseDTO dto = new DemandeCreditResponseDTO();
        dto.setIdDemandeCredit(demande.getIdDemandeCredit());
        dto.setReferenceDossier(demande.getReferenceDossier());
        dto.setIdClient(demande.getClient().getIdClient());
        dto.setProduit(demande.getProduitCredit().getLibelle());
        dto.setMontantDemande(demande.getMontantDemande());
        dto.setDureeMois(demande.getDureeMois());
        dto.setStatut(demande.getStatut());
        dto.setScoreCredit(demande.getScoreCredit());
        dto.setAvisComite(demande.getAvisComite());
        dto.setDecisionFinale(demande.getDecisionFinale());
        dto.setDateDecision(demande.getDateDecision());
        return dto;
    }

    private CreditResponseDTO toDto(Credit credit) {
        CreditResponseDTO dto = new CreditResponseDTO();
        dto.setIdCredit(credit.getIdCredit());
        dto.setReferenceCredit(credit.getReferenceCredit());
        dto.setIdClient(credit.getClient().getIdClient());
        dto.setMontantAccorde(credit.getMontantAccorde());
        dto.setTauxAnnuel(credit.getTauxAnnuel());
        dto.setMensualite(credit.getMensualite());
        dto.setCapitalRestantDu(credit.getCapitalRestantDu());
        dto.setFraisPreleves(credit.getFraisPreleves());
        dto.setReferenceTransactionDeblocage(credit.getReferenceTransactionDeblocage());
        dto.setStatut(credit.getStatut());
        return dto;
    }

    private EcheanceCreditResponseDTO toDto(EcheanceCredit echeance) {
        EcheanceCreditResponseDTO dto = new EcheanceCreditResponseDTO();
        dto.setIdEcheanceCredit(echeance.getIdEcheanceCredit());
        dto.setNumeroEcheance(echeance.getNumeroEcheance());
        dto.setDateEcheance(echeance.getDateEcheance());
        dto.setCapitalPrevu(echeance.getCapitalPrevu());
        dto.setInteretPrevu(echeance.getInteretPrevu());
        dto.setAssurancePrevue(echeance.getAssurancePrevue());
        dto.setTotalPrevu(echeance.getTotalPrevu());
        dto.setCapitalPaye(echeance.getCapitalPaye());
        dto.setInteretPaye(echeance.getInteretPaye());
        dto.setAssurancePayee(echeance.getAssurancePayee());
        dto.setStatut(echeance.getStatut());
        return dto;
    }

    private GarantieCreditResponseDTO toDto(GarantieCredit garantie) {
        GarantieCreditResponseDTO dto = new GarantieCreditResponseDTO();
        dto.setIdGarantieCredit(garantie.getIdGarantieCredit());
        dto.setTypeGarantie(garantie.getTypeGarantie());
        dto.setDescription(garantie.getDescription());
        dto.setValeur(garantie.getValeur());
        dto.setValeurNantie(garantie.getValeurNantie());
        dto.setStatut(garantie.getStatut());
        return dto;
    }

    private RemboursementCreditResponseDTO toDto(RemboursementCredit remboursement) {
        RemboursementCreditResponseDTO dto = new RemboursementCreditResponseDTO();
        dto.setIdRemboursementCredit(remboursement.getIdRemboursementCredit());
        dto.setReferenceRemboursement(remboursement.getReferenceRemboursement());
        dto.setMontant(remboursement.getMontant());
        dto.setCapitalPaye(remboursement.getCapitalPaye());
        dto.setInteretPaye(remboursement.getInteretPaye());
        dto.setAssurancePayee(remboursement.getAssurancePayee());
        dto.setReferenceTransaction(remboursement.getReferenceTransaction());
        dto.setDatePaiement(remboursement.getDatePaiement());
        dto.setStatut(remboursement.getStatut());
        return dto;
    }

    private ImpayeCreditResponseDTO toDto(ImpayeCredit impaye) {
        ImpayeCreditResponseDTO dto = new ImpayeCreditResponseDTO();
        dto.setIdImpayeCredit(impaye.getIdImpayeCredit());
        dto.setIdCredit(impaye.getCredit().getIdCredit());
        dto.setIdEcheanceCredit(impaye.getEcheanceCredit().getIdEcheanceCredit());
        dto.setMontant(impaye.getMontant());
        dto.setJoursRetard(impaye.getJoursRetard());
        dto.setClasseRisque(impaye.getClasseRisque());
        dto.setStatut(impaye.getStatut());
        return dto;
    }

    private ProvisionCreditResponseDTO toDto(ProvisionCredit provision) {
        ProvisionCreditResponseDTO dto = new ProvisionCreditResponseDTO();
        dto.setIdProvisionCredit(provision.getIdProvisionCredit());
        dto.setIdCredit(provision.getCredit().getIdCredit());
        dto.setDateCalcul(provision.getDateCalcul());
        dto.setTauxProvision(provision.getTauxProvision());
        dto.setMontantProvision(provision.getMontantProvision());
        dto.setReferencePieceComptable(provision.getReferencePieceComptable());
        dto.setStatut(provision.getStatut());
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
