package com.soutra.microfinance.api.controller.auth;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.api.exception.UnauthorizedException;
import com.soutra.microfinance.config.JwtService;
import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.dto.request.client.*;
import com.soutra.microfinance.dto.response.client.AuthenticationResponseDTO;
import com.soutra.microfinance.dto.response.client.AuthenticationStepStatus;
import com.soutra.microfinance.dto.response.client.SessionActiveResponseDTO;
import com.soutra.microfinance.dto.response.client.UtilisateurResponseDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.UtilisateurMapper;
import com.soutra.microfinance.service.client.AuthenticationWorkflowResult;
import com.soutra.microfinance.service.client.UtilisateurService;
import com.soutra.microfinance.service.client.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentification", description = "API d'authentification, 2FA et gestion de sessions")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;
    private final JwtService jwtService;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;
    private final SessionService sessionService;

    public AuthController(
            UtilisateurService utilisateurService,
            UtilisateurMapper utilisateurMapper,
            JwtService jwtService,
            JwtTokenBlacklistService jwtTokenBlacklistService,
            SessionService sessionService
    ) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
        this.jwtService = jwtService;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
        this.sessionService = sessionService;
    }

    @Operation(
            summary = "Connexion",
            description = "Authentifie un utilisateur avec email et mot de passe. Retourne un JWT ou un challenge OTP si le 2FA est actif."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification reussie ou OTP requis"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "423", description = "Compte temporairement bloque"),
            @ApiResponse(responseCode = "403", description = "Compte desactive"),
            @ApiResponse(responseCode = "409", description = "Session active existante")
    })
    @PostMapping("/login")
    @AuditLog(action = "AUTH_LOGIN", resource = "AUTH")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ) {
        AuthenticationWorkflowResult resultat = utilisateurService.authentifier(
                requestDTO.getLogin(),
                requestDTO.getMotDePasse()
        );

        if (resultat.otpRequired()) {
            AuthenticationResponseDTO response = new AuthenticationResponseDTO();
            response.setStatutAuthentification(AuthenticationStepStatus.OTP_REQUIS);
            response.setOtpRequis(Boolean.TRUE);
            response.setChallengeId(resultat.challengeId());
            response.setMessage(resultat.message());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(construireReponseAuthentifiee(resultat.utilisateur()));
    }

    @Operation(
            summary = "Deconnexion",
            description = "Deconnecte l'utilisateur, blackliste le JWT courant et reveque la session"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deconnexion prise en compte"),
            @ApiResponse(responseCode = "400", description = "Token manquant ou invalide")
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @AuditLog(action = "AUTH_LOGOUT", resource = "AUTH")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication
    ) {
        String token = extraireToken(authHeader);
        jwtTokenBlacklistService.blacklist(token);

        if (authentication != null && authentication.getPrincipal() instanceof Utilisateur utilisateur) {
            sessionService.revoquerSession(utilisateur.getIdUser(), token);
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Rafraichir le token JWT",
            description = "Genere un nouveau JWT a partir d'un refresh token valide"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token rafraichi avec succes"),
            @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expire")
    })
    @PostMapping("/refresh")
    @AuditLog(action = "AUTH_REFRESH", resource = "AUTH")
    public ResponseEntity<AuthenticationResponseDTO> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO requestDTO
    ) {
        String refreshToken = requestDTO.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        Utilisateur utilisateur = utilisateurService.chargerUtilisateurParLogin(username);

        if (jwtTokenBlacklistService.isBlacklisted(refreshToken)
                || !jwtService.isTokenValid(refreshToken, utilisateur)
                || !jwtService.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token invalide ou expire");
        }

        jwtTokenBlacklistService.blacklist(refreshToken);
        String nouveauToken = jwtService.generateToken(utilisateur);
        String nouveauRefreshToken = jwtService.generateRefreshToken(utilisateur);
        AuthenticationResponseDTO response = new AuthenticationResponseDTO();
        response.setToken(nouveauToken);
        response.setRefreshToken(nouveauRefreshToken);
        response.setUtilisateur(utilisateurMapper.toResponseDTO(utilisateur));
        response.setStatutAuthentification(AuthenticationStepStatus.AUTHENTIFIE);
        response.setMessage("Token rafraichi avec succes");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Activer/desactiver la double authentification",
            description = "Active ou desactive le second facteur OTP apres verification"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA mis a jour avec succes"),
            @ApiResponse(responseCode = "400", description = "Code OTP invalide")
    })
    @PostMapping("/2fa/activer")
    @PreAuthorize("isAuthenticated()")
    @AuditLog(action = "AUTH_2FA_TOGGLE", resource = "AUTH")
    public ResponseEntity<AuthenticationResponseDTO> activer2FA(
            @Valid @RequestBody Activer2FARequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        Utilisateur misAJour = utilisateurService.activerOuDesactiver2FA(
                utilisateur.getIdUser(),
                requestDTO.getActiver(),
                requestDTO.getCodeOtp()
        );

        AuthenticationResponseDTO response = new AuthenticationResponseDTO();
        response.setUtilisateur(utilisateurMapper.toResponseDTO(misAJour));
        response.setStatutAuthentification(AuthenticationStepStatus.AUTHENTIFIE);
        response.setMessage(misAJour.getSecondFacteurActive()
                ? "Double authentification activee"
                : "Double authentification desactivee");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Verifier un code 2FA",
            description = "Verifie un code OTP dans le cadre d'une action sensible (hors login)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Code OTP valide"),
            @ApiResponse(responseCode = "401", description = "Code OTP invalide ou expire")
    })
    @PostMapping("/2fa/verifier")
    @PreAuthorize("isAuthenticated()")
    @AuditLog(action = "AUTH_2FA_VERIFY", resource = "AUTH")
    public ResponseEntity<Void> verifier2FA(
            @Valid @RequestBody Verifier2FARequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        utilisateurService.verifierCodeOtp(utilisateur.getIdUser(), requestDTO.getCodeOtp());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Changer le mot de passe",
            description = "Modifie le mot de passe de l'utilisateur connecte"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mot de passe modifie avec succes"),
            @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect ou validation")
    })
    @PostMapping("/mot-de-passe")
    @PreAuthorize("isAuthenticated()")
    @AuditLog(action = "AUTH_CHANGE_PASSWORD", resource = "AUTH")
    public ResponseEntity<Void> changerMotDePasse(
            @Valid @RequestBody ChangerMotDePasseRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        utilisateurService.changerMotDePasse(
                utilisateur.getIdUser(),
                requestDTO.getAncienMotDePasse(),
                requestDTO.getNouveauMotDePasse()
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Mot de passe oublie",
            description = "Envoie un email contenant un lien de reinitialisation de mot de passe. " +
                    "Repond systematiquement 204, que le compte existe ou non (anti-enumeration)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Si le compte existe, un email a ete envoye")
    })
    @PostMapping("/mot-de-passe/oublie")
    @AuditLog(action = "AUTH_PASSWORD_FORGOT", resource = "AUTH")
    public ResponseEntity<Void> motDePasseOublie(
            @Valid @RequestBody MotDePasseOublieRequestDTO requestDTO
    ) {
        utilisateurService.demanderResetMotDePasse(requestDTO.getLoginOuEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reinitialiser le mot de passe",
            description = "Reinitialise le mot de passe a partir du token recu par email. " +
                    "Le token est a usage unique et expire apres 30 minutes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mot de passe reinitialise avec succes"),
            @ApiResponse(responseCode = "400", description = "Token invalide, expire, ou mot de passe invalide")
    })
    @PostMapping("/mot-de-passe/reinitialiser")
    @AuditLog(action = "AUTH_PASSWORD_RESET", resource = "AUTH")
    public ResponseEntity<Void> reinitialiserMotDePasse(
            @Valid @RequestBody ReinitialiserMotDePasseRequestDTO requestDTO
    ) {
        utilisateurService.reinitialiserMotDePasse(
                requestDTO.getToken(),
                requestDTO.getNouveauMotDePasse()
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Lister les sessions actives",
            description = "Retourne la liste des sessions actives de l'utilisateur connecte"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des sessions actives")
    })
    @GetMapping("/sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionActiveResponseDTO>> listerSessions(
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        List<SessionActiveResponseDTO> sessions = sessionService.listerSessionsUtilisateur(utilisateur.getIdUser());
        return ResponseEntity.ok(sessions);
    }

    @Operation(
            summary = "Revoquer une session",
            description = "Revoque une session active de l'utilisateur connecte"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Session revoquee"),
            @ApiResponse(responseCode = "404", description = "Session introuvable")
    })
    @PostMapping("/sessions/revoguer")
    @PreAuthorize("isAuthenticated()")
    @AuditLog(action = "AUTH_REVOKE_SESSION", resource = "AUTH")
    public ResponseEntity<Void> revoguerSession(
            @Valid @RequestBody RevoguerSessionRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        sessionService.revoquerSession(utilisateur.getIdUser(), requestDTO.getSessionId());
        return ResponseEntity.noContent().build();
    }

    private String extraireToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("L'en-tete Authorization Bearer est obligatoire");
        }
        return authHeader.substring(7);
    }

    private Utilisateur extraireUtilisateur(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Utilisateur utilisateur)) {
            throw new IllegalStateException("Utilisateur authentifie introuvable");
        }
        return utilisateur;
    }

    private AuthenticationResponseDTO construireReponseAuthentifiee(Utilisateur utilisateur) {
        UserDetails userDetails = utilisateur;
        AuthenticationResponseDTO response = new AuthenticationResponseDTO();
        response.setToken(jwtService.generateToken(userDetails));
        response.setRefreshToken(jwtService.generateRefreshToken(userDetails));
        response.setUtilisateur(utilisateurMapper.toResponseDTO(utilisateur));
        response.setStatutAuthentification(AuthenticationStepStatus.AUTHENTIFIE);
        response.setOtpRequis(Boolean.FALSE);
        response.setMessage("Authentification reussie");
        return response;
    }
}
