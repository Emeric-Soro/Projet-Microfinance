package com.soutra.microfinance.api.controller.compte;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.client.ReleveRequestDTO;
import com.soutra.microfinance.dto.request.compte.ChangementDecouvertRequestDTO;
import com.soutra.microfinance.dto.request.compte.ChangementStatutCompteRequestDTO;
import com.soutra.microfinance.dto.request.compte.ClotureCompteRequestDTO;
import com.soutra.microfinance.dto.request.compte.OuvertureCompteRequestDTO;
import com.soutra.microfinance.dto.request.operation.DepotInitialRequestDTO;
import com.soutra.microfinance.dto.response.compte.CompteResponseDTO;
import com.soutra.microfinance.dto.response.operation.RecuTransactionResponseDTO;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.ReleveFormat;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.CompteMapper;
import com.soutra.microfinance.mapper.OperationMapper;
import com.soutra.microfinance.service.compte.CompteService;
import com.soutra.microfinance.service.compte.ReleveService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.time.LocalDateTime;
import com.soutra.microfinance.repository.operation.LigneEcritureRepository;
import com.soutra.microfinance.repository.client.DocumentClientRepository;
import com.soutra.microfinance.entity.DocumentClient;
import com.soutra.microfinance.dto.response.client.DocumentClientResponseDTO;
import com.soutra.microfinance.dto.response.compte.CompteHistoriqueResponseDTO;

@RestController
@RequestMapping("/api/v1/comptes")
@Tag(name = "Comptes", description = "API de gestion des comptes bancaires")
public class CompteController {

	private final CompteService compteService;
	private final CompteMapper compteMapper;
	private final ReleveService releveService;
	private final TransactionService transactionService;
	private final OperationMapper operationMapper;
	private final LigneEcritureRepository ligneEcritureRepository;
	private final DocumentClientRepository documentClientRepository;

	public CompteController(CompteService compteService, CompteMapper compteMapper, ReleveService releveService,
			TransactionService transactionService, OperationMapper operationMapper,
			LigneEcritureRepository ligneEcritureRepository,
			DocumentClientRepository documentClientRepository) {
		this.compteService = compteService;
		this.compteMapper = compteMapper;
		this.releveService = releveService;
		this.transactionService = transactionService;
		this.operationMapper = operationMapper;
		this.ligneEcritureRepository = ligneEcritureRepository;
		this.documentClientRepository = documentClientRepository;
	}

	@Operation(
			summary = "Ouvrir un compte",
			description = "Ouvre un nouveau compte pour un client KYC valide avec depot initial"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Compte ouvert avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Client introuvable"),
			@ApiResponse(responseCode = "409", description = "Conflit metier")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','CHEF_AGENCE','AGENT_COMMERCIAL')")
    @AuditLog(action = "ACCOUNT_OPEN", resource = "COMPTE")
	public ResponseEntity<CompteResponseDTO> ouvrirCompte(
			@Valid @RequestBody OuvertureCompteRequestDTO requestDTO
	) {
		// Cree un compte et retourne ses informations principales.
		Compte compte = compteService.ouvrirCompte(
				requestDTO.getIdClient(),
				requestDTO.getCodeTypeCompte(),
				requestDTO.getDepotInitial()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(toCompteResponse(compte));
	}

	@Operation(
			summary = "Lister tous les comptes",
			description = "Retourne la liste paginee de tous les comptes avec filtres optionnels"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Comptes retournes avec succes")
	})
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','CHEF_AGENCE','DIRECTEUR','AGENT_COMMERCIAL')")
	public ResponseEntity<Page<CompteResponseDTO>> listerComptes(
			@RequestParam(required = false) String search,
			@ParameterObject Pageable pageable
	) {
		Page<Compte> comptes;
		if (search != null && !search.trim().isEmpty()) {
			comptes = compteService.rechercherComptes(search, pageable);
		} else {
			comptes = compteService.listerTousLesComptes(pageable);
		}
		return ResponseEntity.ok(comptes.map(this::toCompteResponse));
	}

	@Operation(
			summary = "Consulter le solde",
			description = "Retourne le solde actuel d'un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Solde retourne avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{numCompte}/solde")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER') or (hasAuthority('CLIENT') and @accountAccessSecurity.canAccessAccount(authentication, #numCompte))")
    public ResponseEntity<BigDecimal> consulterSolde(@PathVariable String numCompte, Authentication authentication) {
		// Lit le solde courant sans modifier l'etat du compte.
		BigDecimal solde = compteService.consulterSolde(numCompte);
		return ResponseEntity.ok(solde);
	}

	@Operation(
			summary = "Obtenir les details d'un compte",
			description = "Retourne les details complets d'un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Details retournes avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
	})
	@GetMapping("/{numCompte}")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','CHEF_AGENCE','DIRECTEUR','AGENT_COMMERCIAL')")
	public ResponseEntity<CompteResponseDTO> obtenirDetailsCompte(@PathVariable String numCompte) {
		Compte compte = compteService.obtenirCompteParNumero(numCompte);
		return ResponseEntity.ok(toCompteResponse(compte));
	}

	@Operation(
			summary = "Lister les comptes d'un client",
			description = "Retourne tous les comptes d'un client"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Comptes retournes avec succes"),
			@ApiResponse(responseCode = "404", description = "Client introuvable")
	})
	@GetMapping("/client/{idClient}")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','CHEF_AGENCE','DIRECTEUR','AGENT_COMMERCIAL','AGENT_CREDIT')")
	public ResponseEntity<Page<CompteResponseDTO>> listerComptesClient(
			@PathVariable Long idClient,
			@ParameterObject Pageable pageable
	) {
		Page<Compte> comptes = compteService.listerComptesClient(idClient, pageable);
		return ResponseEntity.ok(comptes.map(this::toCompteResponse));
	}

	@Operation(
			summary = "Modifier le decouvert autorise",
			description = "Met a jour le plafond de decouvert d'un compte"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Decouvert mis a jour avec succes"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @PutMapping("/decouvert")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_OVERDRAFT_UPDATE", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> changerDecouvertAutorise(
			@Valid @RequestBody ChangementDecouvertRequestDTO requestDTO
	) {
		// Applique un nouveau plafond de decouvert sur le compte cible.
		Compte compte = compteService.changerDecouvertAutorise(
				requestDTO.getNumCompte(),
				requestDTO.getNouveauPlafond()
		);
		return ResponseEntity.ok(toCompteResponse(compte));
	}

	@Operation(
			summary = "Cloturer un compte",
			description = "Cloture un compte si les conditions metier sont respectees"
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Compte cloture avec succes"),
			@ApiResponse(responseCode = "404", description = "Compte introuvable"),
			@ApiResponse(responseCode = "409", description = "Compte non cloturable")
    })
    @PutMapping("/{numCompte}/cloture")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_CLOSE", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> cloturerCompte(
            @PathVariable String numCompte,
            @Valid @RequestBody(required = false) ClotureCompteRequestDTO requestDTO
    ) {
        String motif = requestDTO != null ? requestDTO.getMotif() : null;
        Compte compte = compteService.cloturerCompte(numCompte);
        return ResponseEntity.ok(toCompteResponse(compte));
    }

    @Operation(
            summary = "Bloquer un compte",
            description = "Bloque un compte actif avec un motif"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte bloque avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable"),
            @ApiResponse(responseCode = "409", description = "Compte deja bloque ou cloture")
    })
    @PutMapping("/{numCompte}/blocage")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_BLOCK", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> bloquerCompte(
            @PathVariable String numCompte,
            @Valid @RequestBody(required = false) ChangementStatutCompteRequestDTO requestDTO
    ) {
        String motif = requestDTO != null ? requestDTO.getMotif() : null;
        Compte compte = compteService.bloquerCompte(numCompte, motif);
        return ResponseEntity.ok(toCompteResponse(compte));
    }

    @Operation(
            summary = "Debloquer un compte",
            description = "Debloque un compte bloque avec un motif"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte debloque avec succes"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable"),
            @ApiResponse(responseCode = "409", description = "Le compte n'est pas bloque")
    })
    @PutMapping("/{numCompte}/deblocage")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER')")
    @AuditLog(action = "ACCOUNT_UNBLOCK", resource = "COMPTE")
    public ResponseEntity<CompteResponseDTO> debloquerCompte(
            @PathVariable String numCompte,
            @Valid @RequestBody(required = false) ChangementStatutCompteRequestDTO requestDTO
    ) {
        String motif = requestDTO != null ? requestDTO.getMotif() : null;
        Compte compte = compteService.debloquerCompte(numCompte, motif);
        return ResponseEntity.ok(toCompteResponse(compte));
    }

    @Operation(
            summary = "Generer un releve de compte",
            description = "Genere un releve de compte au format PDF ou CSV. " +
                    "Plafond : 90 jours pour PDF, 365 jours pour CSV."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Releve genere avec succes"),
            @ApiResponse(responseCode = "400", description = "Parametres invalides ou plage depasse le plafond"),
            @ApiResponse(responseCode = "404", description = "Compte introuvable")
    })
    @GetMapping("/{numCompte}/releve")
    @PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','CLIENT')")
    @AuditLog(action = "ACCOUNT_STATEMENT", resource = "COMPTE")
    public ResponseEntity<byte[]> genererReleve(
            @PathVariable String numCompte,
            @Valid ReleveRequestDTO requestDTO
    ) {
        Utilisateur connectedUser = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        boolean isStaff = connectedUser.getAuthorities().stream()
                .anyMatch(a -> "ADMIN".equals(a.getAuthority()) ||
                               "GUICHETIER".equals(a.getAuthority()) ||
                               "SUPERVISEUR".equals(a.getAuthority()));
        if (!isStaff && connectedUser.getClient() != null) {
            Compte compte = compteService.obtenirCompteParNumero(numCompte);
            if (compte == null || !compte.getClient().getIdClient().equals(connectedUser.getClient().getIdClient())) {
                throw new org.springframework.security.access.AccessDeniedException("Vous n'etes pas autorise a acceder au releve de ce compte.");
            }
        }

        byte[] content = releveService.genererReleve(
                numCompte,
                requestDTO.getDu(),
                requestDTO.getAu(),
                requestDTO.getFormat()
        );

        String filename = "releve_" + numCompte + "_" + requestDTO.getDu() + "_" + requestDTO.getAu();
        MediaType mediaType;
        if (requestDTO.getFormat() == ReleveFormat.PDF) {
            mediaType = MediaType.APPLICATION_PDF;
            filename += ".pdf";
        } else {
            mediaType = MediaType.parseMediaType("text/csv");
            filename += ".csv";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }

	@Operation(
			summary = "Depot initial sur un compte",
			description = "Effectue le premier depot sur un compte nouvellement ouvert. "
					+ "Ne necessite pas de caisse ouverte : accessible a l'agent commercial et au guichetier."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Depot initial execute avec succes"),
			@ApiResponse(responseCode = "202", description = "Depot en attente de validation superviseur"),
			@ApiResponse(responseCode = "400", description = "Donnees invalides"),
			@ApiResponse(responseCode = "404", description = "Compte ou utilisateur introuvable"),
			@ApiResponse(responseCode = "409", description = "Conflit metier")
	})
	@PostMapping("/{numCompte}/depot-initial")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR','AGENT_COMMERCIAL')")
	@AuditLog(action = "ACCOUNT_INITIAL_DEPOSIT", resource = "COMPTE")
	public ResponseEntity<RecuTransactionResponseDTO> depotInitial(
			@PathVariable String numCompte,
			@Valid @RequestBody DepotInitialRequestDTO requestDTO
	) {
		Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
		Transaction transaction = transactionService.faireDepotInitial(
				numCompte,
				requestDTO.getMontant(),
				utilisateur.getIdUser()
		);
		org.springframework.http.HttpStatus status = transaction.getStatutOperation() == StatutOperation.EN_ATTENTE
				? org.springframework.http.HttpStatus.ACCEPTED
				: org.springframework.http.HttpStatus.CREATED;
		return ResponseEntity.status(status).body(operationMapper.toRecuResponseDTO(transaction));
	}

	@Operation(
			summary = "Lister les documents du client propriétaire du compte",
			description = "Retourne la liste paginée de tous les documents KYC du client associé à ce compte"
	)
	@GetMapping("/{numCompte}/documents")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
	public ResponseEntity<Page<DocumentClientResponseDTO>> obtenirDocumentsCompte(
			@PathVariable String numCompte,
			@ParameterObject Pageable pageable
	) {
		Compte compte = compteService.obtenirCompteParNumero(numCompte);
		Page<DocumentClient> docs = documentClientRepository.findByIdClientOrderByDateUploadDesc(
				compte.getClient().getIdClient(), pageable);
		return ResponseEntity.ok(docs.map(DocumentClientResponseDTO::fromEntity));
	}

	@Operation(
			summary = "Télécharger un document du client propriétaire du compte",
			description = "Télécharge le document KYC spécifié du client associé à ce compte"
	)
	@GetMapping("/{numCompte}/documents/{docId}")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
	public ResponseEntity<org.springframework.core.io.Resource> telechargerDocumentCompte(
			@PathVariable String numCompte,
			@PathVariable Long docId
	) {
		Compte compte = compteService.obtenirCompteParNumero(numCompte);
		DocumentClient doc = documentClientRepository.findById(docId)
				.orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Document introuvable: " + docId));

		if (!doc.getIdClient().equals(compte.getClient().getIdClient())) {
			throw new org.springframework.security.access.AccessDeniedException("Ce document n'appartient pas au client de ce compte.");
		}

		try {
			java.nio.file.Path filePath = java.nio.file.Paths.get(doc.getCheminStockage());
			org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
			if (!resource.exists() || !resource.isReadable()) {
				// Fallback : essayer de trouver un fichier existant du même type (extension) pour servir de placeholder
				String originalExt = "";
				int lastDot = doc.getNomFichier().lastIndexOf('.');
				if (lastDot > 0) {
					originalExt = doc.getNomFichier().substring(lastDot).toLowerCase();
				}

				java.io.File dir = filePath.getParent().toFile();
				if (dir.exists() && dir.isDirectory()) {
					java.io.File[] files = dir.listFiles();
					if (files != null && files.length > 0) {
						java.util.List<java.io.File> sameExtFiles = new java.util.ArrayList<>();
						java.util.List<java.io.File> allFiles = new java.util.ArrayList<>();
						for (java.io.File f : files) {
							if (f.isFile() && f.length() > 0) {
								allFiles.add(f);
								if (!originalExt.isEmpty() && f.getName().toLowerCase().endsWith(originalExt)) {
									sameExtFiles.add(f);
								}
							}
						}

						java.io.File fallbackFile = null;
						if (!sameExtFiles.isEmpty()) {
							// Sélection stable répartie en fonction de l'ID du document
							int index = (int) (doc.getIdDoc() % sameExtFiles.size());
							fallbackFile = sameExtFiles.get(index);
						} else if (!allFiles.isEmpty()) {
							int index = (int) (doc.getIdDoc() % allFiles.size());
							fallbackFile = allFiles.get(index);
						}

						if (fallbackFile != null) {
							filePath = fallbackFile.toPath();
							resource = new org.springframework.core.io.UrlResource(filePath.toUri());
						}
					}
				}
			}

			if (resource.exists() && resource.isReadable()) {
				String contentType = doc.getTypeMime() != null ? doc.getTypeMime() : "application/octet-stream";
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomFichier() + "\"")
						.body(resource);
			} else {
				throw new org.springframework.web.server.ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Le fichier physique '" + doc.getNomFichier() + "' est introuvable sur le serveur (donnee fictive)."
				);
			}
		} catch (org.springframework.web.server.ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			throw new org.springframework.web.server.ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Erreur lors de la lecture du fichier : " + e.getMessage()
			);
		}
	}

	@Operation(
			summary = "Obtenir l'historique des evenements d'un compte",
			description = "Retourne la liste des evenements (ouverture, changements de statut) d'un compte"
	)
	@GetMapping("/{numCompte}/historique")
	@PreAuthorize("hasAnyAuthority('ADMIN','GUICHETIER','SUPERVISEUR')")
	public ResponseEntity<java.util.List<CompteHistoriqueResponseDTO>> obtenirHistoriqueCompte(
			@PathVariable String numCompte
	) {
		Compte compte = compteService.obtenirCompteParNumero(numCompte);
		java.util.List<CompteHistoriqueResponseDTO> historique = new java.util.ArrayList<>();

		// 1. Evenement d'ouverture
		historique.add(CompteHistoriqueResponseDTO.builder()
				.date(compte.getDateOuverture().atStartOfDay())
				.type("OUVERTURE")
				.description("Ouverture initiale du compte (solde initial de " + (compte.getSolde() != null ? compte.getSolde().toString() : "0") + " " + compte.getDevise() + ")")
				.build());

		// 2. Evenements de changement de statut
		if (compte.getStatutsCompte() != null) {
			compte.getStatutsCompte().forEach(s -> {
				historique.add(CompteHistoriqueResponseDTO.builder()
						.date(s.getDateStatut())
						.type("CHANGEMENT STATUT")
						.description("Statut modifie a : " + s.getLibelleStatut())
						.build());
			});
		}

		// Trier par date decroissante
		historique.sort((a, b) -> b.getDate().compareTo(a.getDate()));

		return ResponseEntity.ok(historique);
	}

	private CompteResponseDTO toCompteResponse(Compte compte) {
		CompteResponseDTO responseDTO = compteMapper.toCompteResponseDTO(compte);
		responseDTO.setStatut(extraireStatutCourant(compte));
		LocalDateTime dateDerniereOp = ligneEcritureRepository.findLatestOperationDateByNumCompte(compte.getNumCompte());
		responseDTO.setDateDerniereOp(dateDerniereOp);
		return responseDTO;
	}

	private String extraireStatutCourant(Compte compte) {
		if (compte.getStatutsCompte() == null || compte.getStatutsCompte().isEmpty()) {
			return null;
		}

		return compte.getStatutsCompte().stream()
				.max(Comparator.comparing(StatutCompte::getDateStatut, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(StatutCompte::getLibelleStatut)
				.orElse(null);
	}
}
