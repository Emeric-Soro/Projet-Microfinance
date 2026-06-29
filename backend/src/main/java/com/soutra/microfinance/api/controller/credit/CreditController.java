package com.soutra.microfinance.api.controller.credit;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.credit.DecaissementRequestDTO;
import com.soutra.microfinance.dto.request.credit.GarantieRequestDTO;
import com.soutra.microfinance.dto.request.credit.RemboursementRequestDTO;
import com.soutra.microfinance.dto.request.credit.SimulationRequestDTO;
import com.soutra.microfinance.dto.response.credit.CreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.EcheanceResponseDTO;
import com.soutra.microfinance.dto.response.credit.GarantieResponseDTO;
import com.soutra.microfinance.dto.response.credit.TableauAmortissementResponseDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.Garantie;
import com.soutra.microfinance.entity.MethodeCalculInteret;
import com.soutra.microfinance.mapper.CreditMapper;
import com.soutra.microfinance.service.credit.AmortissementService;
import com.soutra.microfinance.service.credit.CreditService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/credits")
@Tag(name = "Credits", description = "API de gestion des credits et demandes")
public class CreditController {

	private final CreditService creditService;
	private final CreditMapper creditMapper;
	private final AmortissementService amortissementService;

	public CreditController(CreditService creditService, CreditMapper creditMapper, AmortissementService amortissementService) {
		this.creditService = creditService;
		this.creditMapper = creditMapper;
		this.amortissementService = amortissementService;
	}

	@Operation(summary = "Consulter un credit", description = "Retourne les details d'un credit par son identifiant")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Credit retourne avec succes"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@GetMapping("/{idCredit}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<CreditResponseDTO> consulterCredit(@PathVariable Long idCredit) {
		Credit credit = creditService.consulterCredit(idCredit);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@Operation(summary = "Decaisser un credit", description = "Effectue le decaissement d'un credit approuve vers le compte cible")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Decaissement effectue avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/decaissement")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_DISBURSEMENT", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> decaisser(
			@PathVariable Long idCredit,
			@Valid @RequestBody DecaissementRequestDTO request) {

		Credit credit = creditService.decaisserCredit(idCredit, request.numCompteCible());
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@Operation(summary = "Enregistrer un remboursement", description = "Enregistre un remboursement partiel ou total sur un credit")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Remboursement enregistre avec succes"),
			@ApiResponse(responseCode = "400", description = "Montant invalide"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/remboursement")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
	@AuditLog(action = "CREDIT_REPAYMENT", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> rembourser(
			@PathVariable Long idCredit,
			@Valid @RequestBody RemboursementRequestDTO request) {

		Credit credit = creditService.enregistrerRemboursement(idCredit, request.montant(), request.numCompteSource());
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@Operation(summary = "Consulter l'echeancier", description = "Retourne le tableau d'amortissement d'un credit")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Echeancier retourne avec succes"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@GetMapping("/{idCredit}/echeancier")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<TableauAmortissementResponseDTO> consulterEcheancier(
			@PathVariable Long idCredit) {

		Credit credit = creditService.consulterCredit(idCredit);
		List<Echeance> echeances = creditService.consulterTableauAmortissement(idCredit);
		return ResponseEntity.ok(creditMapper.toTableauAmortissementResponseDTO(credit, echeances));
	}

	@Operation(summary = "Credits d'un client", description = "Liste paginee des credits d'un client")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des credits retournee avec succes"),
			@ApiResponse(responseCode = "404", description = "Client introuvable")
	})
	@GetMapping("/client/{idClient}")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE','GUICHETIER')")
	public ResponseEntity<Page<CreditResponseDTO>> creditsClient(
			@PathVariable Long idClient, @ParameterObject Pageable pageable) {

		Page<Credit> credits = creditService.consulterCreditsClient(idClient, pageable);
		return ResponseEntity.ok(credits.map(creditMapper::toCreditResponseDTO));
	}

	@Operation(summary = "Simuler un credit", description = "Simule les echeances d'un credit selon le montant, le taux, la duree et la methode de calcul")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Simulation realisee avec succes"),
			@ApiResponse(responseCode = "400", description = "Parametres de simulation invalides")
	})
	@PostMapping("/simulation")
	@PreAuthorize("permitAll()")
	public ResponseEntity<List<EcheanceResponseDTO>> simulerCredit(
			@Valid @RequestBody SimulationRequestDTO request) {

		MethodeCalculInteret methode = MethodeCalculInteret.valueOf(request.methode().toUpperCase());
		List<Echeance> echeances = amortissementService.genererTableau(
				request.montant(),
				request.taux(),
				request.duree(),
				methode,
				LocalDate.now()
		);
		return ResponseEntity.ok(creditMapper.toEcheanceResponseDTOList(echeances));
	}

	@Operation(summary = "Lister tous les credits", description = "Liste paginee des credits avec filtres")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des credits retournee avec succes")
	})
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	public ResponseEntity<Page<CreditResponseDTO>> listerCredits(
			@RequestParam(required = false) String statut,
			@RequestParam(required = false) Long idClient,
			@ParameterObject Pageable pageable) {
		Page<Credit> credits;
		if (statut != null && !statut.isBlank()) {
			credits = creditService.consulterCreditsParStatut(statut, pageable);
		} else if (idClient != null) {
			credits = creditService.consulterCreditsClient(idClient, pageable);
		} else {
			credits = creditService.consulterTousLesCredits(pageable);
		}
		return ResponseEntity.ok(credits.map(creditMapper::toCreditResponseDTO));
	}

	@Operation(summary = "Mettre un credit en instruction", description = "Passe le statut du credit en INSTRUCTION")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Credit en instruction"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/instruire")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_INSTRUCT", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> instruireCredit(@PathVariable Long idCredit) {
		Credit credit = creditService.instruireCredit(idCredit);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@Operation(summary = "Approuver un credit", description = "Approuve un credit instruit")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Credit approuve avec succes"),
			@ApiResponse(responseCode = "400", description = "Credit non eligible a l'approbation"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/approuver")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_APPROVE", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> approuverCredit(@PathVariable Long idCredit) {
		Credit credit = creditService.approuverCredit(idCredit);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@Operation(summary = "Ajouter des garanties", description = "Ajoute des garanties a un credit")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Garanties ajoutees avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees de garantie invalides"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/garanties")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	@AuditLog(action = "CREDIT_ADD_GUARANTEE", resource = "CREDIT")
	public ResponseEntity<List<GarantieResponseDTO>> ajouterGaranties(
			@PathVariable Long idCredit,
			@Valid @RequestBody List<GarantieRequestDTO> garanties) {
		List<Garantie> saved = creditService.ajouterGaranties(idCredit, garanties);
		return ResponseEntity.ok(creditMapper.toGarantieResponseDTOList(saved));
	}

	@Operation(summary = "Restructurer un credit", description = "Restructure les conditions d'un credit (duree, taux, montant)")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Credit restructure avec succes"),
			@ApiResponse(responseCode = "400", description = "Parametres de restructuration invalides"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/restructurer")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
	@AuditLog(action = "CREDIT_RESTRUCTURE", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> restructurerCredit(
			@PathVariable Long idCredit,
			@RequestParam Integer nouvelleDureeMois,
			@RequestParam(required = false) BigDecimal nouveauTaux) {
		Credit credit = creditService.restructurerCredit(idCredit, nouvelleDureeMois, nouveauTaux);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}

	@Operation(summary = "Lister les echeances en retard", description = "Retourne la liste des echeances en retard de paiement")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Liste des echeances en retard retournee avec succes")
	})
	@GetMapping("/echeances-retard")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CHEF_AGENCE')")
	public ResponseEntity<List<EcheanceResponseDTO>> listerEcheancesRetard() {
		List<Echeance> echeances = creditService.consulterEcheancesRetard();
		return ResponseEntity.ok(echeances.stream().map(creditMapper::toEcheanceResponseDTO).toList());
	}

	@Operation(summary = "Passation en souffrance", description = "Passe un credit en souffrance (write-off)")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Credit passe en souffrance avec succes"),
			@ApiResponse(responseCode = "404", description = "Credit introuvable")
	})
	@PostMapping("/{idCredit}/passation")
	@PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR')")
	@AuditLog(action = "CREDIT_WRITEOFF", resource = "CREDIT")
	public ResponseEntity<CreditResponseDTO> passationSouffrance(@PathVariable Long idCredit) {
		Credit credit = creditService.passerEnSouffrance(idCredit);
		return ResponseEntity.ok(creditMapper.toCreditResponseDTO(credit));
	}
}
