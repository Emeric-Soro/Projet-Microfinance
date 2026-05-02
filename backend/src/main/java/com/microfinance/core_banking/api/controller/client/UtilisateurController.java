package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.config.JwtService;
import com.microfinance.core_banking.config.JwtTokenBlacklistService;
import com.microfinance.core_banking.dto.request.client.ActivationUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.request.client.ChangementMotDePasseRequestDTO;
import com.microfinance.core_banking.dto.request.client.CreationUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.request.client.LoginRequestDTO;
import com.microfinance.core_banking.dto.request.client.VerificationOtpRequestDTO;
import com.microfinance.core_banking.dto.response.client.AuthenticationResponseDTO;
import com.microfinance.core_banking.dto.response.client.AuthenticationStepStatus;
import com.microfinance.core_banking.dto.response.client.UtilisateurResponseDTO;
import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.mapper.UtilisateurMapper;
import com.microfinance.core_banking.service.client.AuthenticationWorkflowResult;
import com.microfinance.core_banking.service.client.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "API de gestion des acces numeriques")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;
    private final JwtService jwtService;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;

    public UtilisateurController(
            UtilisateurService utilisateurService,
            UtilisateurMapper utilisateurMapper,
            JwtService jwtService,
            JwtTokenBlacklistService jwtTokenBlacklistService
    ) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
        this.jwtService = jwtService;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
    }

    @Operation(
            summary = "Creer un compte web",
            description = "Cree un acces web/mobile pour un client existant apres verification du code client, de l'email et de la date de naissance"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte web cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Compte web deja existant pour ce client", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @AuditLog(action = "USER_CREATE", resource = "UTILISATEUR")
    public ResponseEntity<UtilisateurResponseDTO> creerCompteWeb(
            @Valid @RequestBody CreationUtilisateurRequestDTO requestDTO
    ) {
        Utilisateur utilisateurCree = utilisateurService.creerCompteWeb(
                requestDTO.getCodeClient(),
                requestDTO.getEmail(),
                requestDTO.getDateNaissance(),
                requestDTO.getMotDePasseBrut()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurMapper.toResponseDTO(utilisateurCree));
    }

    @Operation(
            summary = "Authentifier un utilisateur",
            description = "Valide le mot de passe, applique le lockout et initie un OTP si le second facteur est actif"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification reussie ou OTP emis"),
            @ApiResponse(responseCode = "400", description = "Donnees de connexion invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides ou compte verrouille", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    @AuditLog(action = "USER_LOGIN", resource = "AUTH")
    public ResponseEntity<AuthenticationResponseDTO> authentifier(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ) {
        AuthenticationWorkflowResult resultat = utilisateurService.authentifier(
                requestDTO.getLogin(),
                requestDTO.getMotDePasse()
        );

        if (resultat.otpRequired()) {
            return ResponseEntity.ok(construireReponseOtp(resultat));
        }

        return ResponseEntity.ok(construireReponseAuthentifiee(resultat.utilisateur()));
    }

    @Operation(
            summary = "Verifier le second facteur",
            description = "Valide le challenge OTP puis emet le JWT final"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP valide et session ouverte"),
            @ApiResponse(responseCode = "400", description = "Donnees OTP invalides", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "OTP invalide ou expire", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login/otp")
    @AuditLog(action = "USER_LOGIN_OTP", resource = "AUTH")
    public ResponseEntity<AuthenticationResponseDTO> verifierOtp(
            @Valid @RequestBody VerificationOtpRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.verifierSecondFacteur(
                requestDTO.getLogin(),
                requestDTO.getChallengeId(),
                requestDTO.getCodeOtp()
        );
        return ResponseEntity.ok(construireReponseAuthentifiee(utilisateur));
    }

    @Operation(
            summary = "Assigner un role",
            description = "Attribue un role metier a un utilisateur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role assigne avec succes"),
            @ApiResponse(responseCode = "400", description = "Code role invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @PutMapping("/{idUser}/roles")
    @AuditLog(action = "USER_ASSIGN_ROLE", resource = "UTILISATEUR")
    public ResponseEntity<UtilisateurResponseDTO> assignerRole(
            @PathVariable Long idUser,
            @RequestParam String codeRole
    ) {
        Utilisateur utilisateur = utilisateurService.assignerRole(idUser, codeRole);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(
            summary = "Revoquer un role",
            description = "Retire un role metier a un utilisateur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role revoque avec succes"),
            @ApiResponse(responseCode = "400", description = "Code role invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @DeleteMapping("/{idUser}/roles")
    @AuditLog(action = "USER_REVOKE_ROLE", resource = "UTILISATEUR")
    public ResponseEntity<UtilisateurResponseDTO> revoquerRole(
            @PathVariable Long idUser,
            @RequestParam String codeRole
    ) {
        Utilisateur utilisateur = utilisateurService.revoquerRole(idUser, codeRole);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(
            summary = "Activer ou desactiver un utilisateur",
            description = "Active ou desactive explicitement l'acces numerique d'un utilisateur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut d'activation mis a jour"),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN)")
    @PutMapping("/{idUser}/activation")
    @AuditLog(action = "USER_ACTIVATION_UPDATE", resource = "UTILISATEUR")
    public ResponseEntity<UtilisateurResponseDTO> changerActivation(
            @PathVariable Long idUser,
            @Valid @RequestBody ActivationUtilisateurRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.changerActivation(idUser, requestDTO.getActif());
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(
            summary = "Deconnecter un utilisateur",
            description = "Revoque le JWT courant en le mettant en liste noire jusqu'a expiration"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deconnexion prise en compte"),
            @ApiResponse(responseCode = "400", description = "Token manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentification requise - token JWT manquant ou invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    @AuditLog(action = "USER_LOGOUT", resource = "AUTH")
    public ResponseEntity<Void> deconnecter(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("L'en-tete Authorization Bearer est obligatoire");
        }
        String token = authHeader.substring(7);
        jwtTokenBlacklistService.blacklist(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Changer le mot de passe d'un utilisateur",
            description = "Applique la politique de mot de passe, interdit la reutilisation recente et reinitialise les etats de verrouillage et OTP"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mot de passe mis a jour"),
            @ApiResponse(responseCode = "400", description = "Mot de passe invalide ou reutilise", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Mot de passe actuel invalide", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - permissions insuffisantes pour cette ressource", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{idUser}/mot-de-passe")
    @AuditLog(action = "USER_PASSWORD_CHANGE", resource = "UTILISATEUR")
    public ResponseEntity<UtilisateurResponseDTO> changerMotDePasse(
            @PathVariable Long idUser,
            @Valid @RequestBody ChangementMotDePasseRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.changerMotDePasse(
                idUser,
                requestDTO.getMotDePasseActuel(),
                requestDTO.getNouveauMotDePasse(),
                requestDTO.getMotif()
        );
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }

    private AuthenticationResponseDTO construireReponseOtp(AuthenticationWorkflowResult resultat) {
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
        responseDTO.setStatutAuthentification(AuthenticationStepStatus.OTP_REQUIS);
        responseDTO.setOtpRequis(Boolean.TRUE);
        responseDTO.setChallengeId(resultat.challengeId());
        responseDTO.setMessage(resultat.message());
        return responseDTO;
    }

    private AuthenticationResponseDTO construireReponseAuthentifiee(Utilisateur utilisateur) {
        UserDetails userDetails = utilisateur;
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO();
        responseDTO.setToken(jwtService.generateToken(userDetails));
        responseDTO.setUtilisateur(utilisateurMapper.toResponseDTO(utilisateur));
        responseDTO.setStatutAuthentification(AuthenticationStepStatus.AUTHENTIFIE);
        responseDTO.setOtpRequis(Boolean.FALSE);
        responseDTO.setMessage("Authentification reussie");
        return responseDTO;
    }
}
