package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.ApiEnvelope;
import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.mobile.MobileDemandeCreditRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileSimulationCreditRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileCreditResponseDTO;
import com.soutra.microfinance.dto.response.mobile.MobileEcheanceResponseDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.DemandeCredit;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.credit.AmortissementService;
import com.soutra.microfinance.service.credit.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/credits")
@Tag(name = "Mobile Credits", description = "API de gestion des credits pour l'application mobile")
public class MobileCreditController {

    private final CreditService creditService;
    private final AmortissementService amortissementService;

    public MobileCreditController(CreditService creditService, AmortissementService amortissementService) {
        this.creditService = creditService;
        this.amortissementService = amortissementService;
    }

    @Operation(summary = "Lister les credits", description = "Retourne la liste paginee des credits du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des credits retournee avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_CREDIT_LIST", resource = "CREDIT")
    public ResponseEntity<Page<MobileCreditResponseDTO>> listerCredits(
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        Page<Credit> credits = creditService.consulterCreditsClient(idClient, pageable);
        Page<MobileCreditResponseDTO> response = credits.map(this::toCreditResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Detail d'un credit", description = "Retourne les informations detaillees d'un credit.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail du credit retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_CREDIT_DETAIL", resource = "CREDIT")
    public ResponseEntity<MobileCreditResponseDTO> detailCredit(
            @PathVariable Long idCredit,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Credit credit = creditService.consulterCredit(idCredit);

        verifierProprietaireCredit(credit, utilisateur);

        return ResponseEntity.ok(toCreditResponse(credit));
    }

    @Operation(summary = "Echeancier d'un credit", description = "Retourne le tableau d'amortissement d'un credit.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Echeancier retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Credit introuvable")
    })
    @GetMapping("/{idCredit}/echeancier")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_CREDIT_SCHEDULE", resource = "CREDIT")
    public ResponseEntity<ApiEnvelope<List<MobileEcheanceResponseDTO>>> echeancierCredit(
            @PathVariable Long idCredit,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Credit credit = creditService.consulterCredit(idCredit);

        verifierProprietaireCredit(credit, utilisateur);

        List<Echeance> echeances = creditService.consulterTableauAmortissement(idCredit);
        List<MobileEcheanceResponseDTO> response = echeances.stream()
                .map(this::toEcheanceResponse)
                .toList();

        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    @Operation(summary = "Simuler un credit", description = "Simule un credit avec les parametres fournis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulation realisee avec succes")
    })
    @PostMapping("/simulation")
    @PreAuthorize("permitAll()")
    @AuditLog(action = "MOBILE_CREDIT_SIMULATION", resource = "CREDIT")
    public ResponseEntity<ApiEnvelope<List<MobileEcheanceResponseDTO>>> simulerCredit(
            @Valid @RequestBody MobileSimulationCreditRequestDTO requestDTO,
            Authentication authentication
    ) {
        List<Echeance> echeancesSimulees = amortissementService.genererTableau(
                requestDTO.getMontant(),
                requestDTO.getTauxAnnuel(),
                requestDTO.getDureeMois(),
                requestDTO.getMethodeCalculInteret(),
                LocalDate.now()
        );
        List<MobileEcheanceResponseDTO> response = echeancesSimulees.stream()
                .map(this::toEcheanceResponse)
                .toList();

        return ResponseEntity.ok(ApiEnvelope.success(response));
    }

    @Operation(summary = "Soumettre une demande de credit", description = "Soumet une nouvelle demande de credit.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Demande de credit soumise avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides")
    })
    @PostMapping("/demandes")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_CREDIT_DEMAND_SUBMIT", resource = "CREDIT")
    public ResponseEntity<MobileCreditResponseDTO> soumettreDemande(
            @Valid @RequestBody MobileDemandeCreditRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        DemandeCredit demande = creditService.soumettreDemandeCredit(
                idClient,
                requestDTO.getCodeProduitCredit(),
                requestDTO.getMontantDemande(),
                requestDTO.getDureeSouhaitee(),
                requestDTO.getObjetCredit(),
                utilisateur.getIdUser()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new MobileCreditResponseDTO(
                        null,
                        demande.getReferenceDemande(),
                        demande.getMontantDemande(),
                        null,
                        null,
                        demande.getDureeSouhaitee(),
                        demande.getStatutDemande().name(),
                        null
                )
        );
    }

    @Operation(summary = "Lister les demandes de credit", description = "Retourne la liste paginee des demandes de credit du client.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des demandes retournee avec succes")
    })
    @GetMapping("/demandes")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_CREDIT_DEMAND_LIST", resource = "CREDIT")
    public ResponseEntity<Page<MobileCreditResponseDTO>> listerDemandes(
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        Page<DemandeCredit> demandes = creditService.consulterDemandesClient(idClient, pageable);

        Page<MobileCreditResponseDTO> response = demandes.map(d -> new MobileCreditResponseDTO(
                null,
                d.getReferenceDemande(),
                d.getMontantDemande(),
                null,
                null,
                d.getDureeSouhaitee(),
                d.getStatutDemande().name(),
                null
        ));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Detail d'une demande", description = "Retourne le detail d'une demande de credit.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detail de la demande retourne avec succes"),
            @ApiResponse(responseCode = "404", description = "Demande introuvable")
    })
    @GetMapping("/demandes/{idDemande}")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_CREDIT_DEMAND_DETAIL", resource = "CREDIT")
    public ResponseEntity<MobileCreditResponseDTO> detailDemande(
            @PathVariable Long idDemande,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        DemandeCredit demande = creditService.consulterDemande(idDemande);

        if (!demande.getClient().getIdClient().equals(utilisateur.getClient().getIdClient())) {
            throw new EntityNotFoundException("Demande introuvable");
        }

        return ResponseEntity.ok(new MobileCreditResponseDTO(
                null,
                demande.getReferenceDemande(),
                demande.getMontantDemande(),
                null,
                null,
                demande.getDureeSouhaitee(),
                demande.getStatutDemande().name(),
                null
        ));
    }

    private MobileCreditResponseDTO toCreditResponse(Credit credit) {
        return new MobileCreditResponseDTO(
                credit.getIdCredit(),
                credit.getReferenceCredit(),
                credit.getMontantAccorde(),
                credit.getMontantRestantDu(),
                credit.getTauxInteretAnnuel(),
                credit.getDureeMois(),
                credit.getStatutCredit() != null ? credit.getStatutCredit().getLibelle() : null,
                credit.getDateDecaissement()
        );
    }

    private MobileEcheanceResponseDTO toEcheanceResponse(Echeance echeance) {
        String statut = Boolean.TRUE.equals(echeance.getEstPayee()) ? "PAYEE" : "EN_ATTENTE";
        return new MobileEcheanceResponseDTO(
                echeance.getNumeroEcheance(),
                echeance.getDateEcheance(),
                echeance.getMontantTotal(),
                echeance.getMontantCapital(),
                echeance.getMontantInteret(),
                echeance.getCredit() != null ? echeance.getCredit().getMontantRestantDu().subtract(echeance.getMontantCapital()) : BigDecimal.ZERO,
                statut
        );
    }

    private void verifierProprietaireCredit(Credit credit, Utilisateur utilisateur) {
        if (!credit.getClient().getIdClient().equals(utilisateur.getClient().getIdClient())) {
            throw new IllegalArgumentException("Credit introuvable");
        }
    }

}
