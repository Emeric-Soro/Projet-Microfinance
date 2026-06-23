package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.api.exception.UnauthorizedException;
import com.soutra.microfinance.config.JwtService;
import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.dto.request.mobile.MobileLoginOtpRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileLoginRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileMotDePasseOublieRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobilePinRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileRefreshTokenRequestDTO;
import com.soutra.microfinance.dto.request.mobile.MobileReinitialiserMotDePasseRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileAuthResponseDTO;
import com.soutra.microfinance.dto.response.mobile.MobileUtilisateurDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.client.AuthenticationWorkflowResult;
import com.soutra.microfinance.service.client.SessionService;
import com.soutra.microfinance.service.client.UtilisateurService;
import com.soutra.microfinance.service.mobile.MobileAuthService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mobile/auth")
@Tag(name = "Mobile Authentification", description = "API d'authentification mobile (login OTP, PIN, biometrie)")
public class MobileAuthController {

    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;
    private final JwtTokenBlacklistService jwtTokenBlacklistService;
    private final SessionService sessionService;
    private final MobileAuthService mobileAuthService;

    public MobileAuthController(
            UtilisateurService utilisateurService,
            JwtService jwtService,
            JwtTokenBlacklistService jwtTokenBlacklistService,
            SessionService sessionService,
            MobileAuthService mobileAuthService
    ) {
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
        this.sessionService = sessionService;
        this.mobileAuthService = mobileAuthService;
    }

    @Operation(summary = "Connexion mobile", description = "Authentifie un utilisateur mobile. Retourne un JWT ou OTP_REQUIS si le 2FA est actif.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification reussie ou OTP requis"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "423", description = "Compte temporairement bloque"),
            @ApiResponse(responseCode = "403", description = "Compte desactive")
    })
    @PostMapping("/login")
    @AuditLog(action = "MOBILE_AUTH_LOGIN", resource = "AUTH")
    public ResponseEntity<MobileAuthResponseDTO> login(@Valid @RequestBody MobileLoginRequestDTO requestDTO) {
        AuthenticationWorkflowResult resultat = utilisateurService.authentifier(
                requestDTO.getLogin(),
                requestDTO.getMotDePasse()
        );

        if (resultat.otpRequired()) {
            MobileAuthResponseDTO response = new MobileAuthResponseDTO(
                    null, null, "OTP_REQUIS", resultat.challengeId(), null
            );
            return ResponseEntity.ok(response);
        }

        Utilisateur utilisateur = resultat.utilisateur();
        return ResponseEntity.ok(construireReponseAuthentifiee(utilisateur));
    }

    @Operation(summary = "Verification OTP mobile", description = "Verifie un code OTP et finalise l'authentification mobile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP valide, authentification complete"),
            @ApiResponse(responseCode = "401", description = "Code OTP invalide ou expire")
    })
    @PostMapping("/login/otp")
    @AuditLog(action = "MOBILE_AUTH_LOGIN_OTP", resource = "AUTH")
    public ResponseEntity<MobileAuthResponseDTO> verifierOtp(@Valid @RequestBody MobileLoginOtpRequestDTO requestDTO) {
        Utilisateur utilisateur = utilisateurService.verifierSecondFacteur(
                requestDTO.getLogin(), requestDTO.getChallengeId(), requestDTO.getCodeOtp()
        );
        return ResponseEntity.ok(construireReponseAuthentifiee(utilisateur));
    }

    @Operation(summary = "Deconnexion mobile", description = "Deconnecte l'utilisateur mobile et blackliste le JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deconnexion prise en compte"),
            @ApiResponse(responseCode = "400", description = "Token manquant ou invalide")
    })
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_AUTH_LOGOUT", resource = "AUTH")
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

    @Operation(summary = "Rafraichir le token mobile", description = "Genere un nouveau JWT a partir d'un refresh token valide.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token rafraichi avec succes"),
            @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expire")
    })
    @PostMapping("/refresh-token")
    @AuditLog(action = "MOBILE_AUTH_REFRESH", resource = "AUTH")
    public ResponseEntity<MobileAuthResponseDTO> refreshToken(@Valid @RequestBody MobileRefreshTokenRequestDTO requestDTO) {
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
        MobileUtilisateurDTO utilisateurDTO = toUtilisateurDTO(utilisateur);

        MobileAuthResponseDTO response = new MobileAuthResponseDTO(
                nouveauToken, nouveauRefreshToken, "AUTHENTIFIE", null, utilisateurDTO
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mot de passe oublie", description = "Envoie un email de reinitialisation de mot de passe.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email de reinitialisation envoye"),
            @ApiResponse(responseCode = "404", description = "Email introuvable")
    })
    @PostMapping("/mot-de-passe/oublie")
    @AuditLog(action = "MOBILE_AUTH_PASSWORD_FORGOT", resource = "AUTH")
    public ResponseEntity<Void> motDePasseOublie(@Valid @RequestBody MobileMotDePasseOublieRequestDTO requestDTO) {
        utilisateurService.chargerUtilisateurParLogin(requestDTO.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reinitialiser le mot de passe", description = "Reinitialise le mot de passe avec un token de reinitialisation.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mot de passe reinitialise"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expire")
    })
    @PostMapping("/mot-de-passe/reinitialiser")
    @AuditLog(action = "MOBILE_AUTH_PASSWORD_RESET", resource = "AUTH")
    public ResponseEntity<Void> reinitialiserMotDePasse(@Valid @RequestBody MobileReinitialiserMotDePasseRequestDTO requestDTO) {
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Creer le code PIN", description = "Cree ou modifie le code PIN de l'utilisateur mobile.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "PIN cree ou modifie avec succes")
    })
    @PostMapping("/pin/creer")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_AUTH_PIN_CREATE", resource = "AUTH")
    public ResponseEntity<Void> creerPin(
            @Valid @RequestBody MobilePinRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        mobileAuthService.creerOuModifierPin(utilisateur.getIdUser(), requestDTO.getCodePin());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verifier le code PIN", description = "Verifie le code PIN de l'utilisateur mobile.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "PIN valide"),
            @ApiResponse(responseCode = "401", description = "PIN invalide")
    })
    @PostMapping("/pin/verifier")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_AUTH_PIN_VERIFY", resource = "AUTH")
    public ResponseEntity<Void> verifierPin(
            @Valid @RequestBody MobilePinRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        boolean valide = mobileAuthService.verifierPin(utilisateur.getIdUser(), requestDTO.getCodePin());
        if (!valide) {
            throw new UnauthorizedException("Code PIN invalide");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activer la biometrie", description = "Active l'authentification biometrique pour l'utilisateur mobile.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Biometrie activee avec succes")
    })
    @PostMapping("/biometrie/activer")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_AUTH_BIOMETRY_ENABLE", resource = "AUTH")
    public ResponseEntity<Void> activerBiometrie(
            @Valid @RequestBody MobilePinRequestDTO requestDTO,
            Authentication authentication
    ) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        mobileAuthService.activerBiometrie(utilisateur.getIdUser(), requestDTO.getCodePin());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactiver la biometrie", description = "Desactive l'authentification biometrique pour l'utilisateur mobile.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Biometrie desactivee avec succes")
    })
    @PostMapping("/biometrie/desactiver")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_AUTH_BIOMETRY_DISABLE", resource = "AUTH")
    public ResponseEntity<Void> desactiverBiometrie(Authentication authentication) {
        Utilisateur utilisateur = extraireUtilisateur(authentication);
        mobileAuthService.desactiverBiometrie(utilisateur.getIdUser());
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

    private MobileAuthResponseDTO construireReponseAuthentifiee(Utilisateur utilisateur) {
        UserDetails userDetails = utilisateur;
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        MobileUtilisateurDTO utilisateurDTO = toUtilisateurDTO(utilisateur);
        return new MobileAuthResponseDTO(token, refreshToken, "AUTHENTIFIE", null, utilisateurDTO);
    }

    private MobileUtilisateurDTO toUtilisateurDTO(Utilisateur utilisateur) {
        return new MobileUtilisateurDTO(
                utilisateur.getIdUser(),
                utilisateur.getLogin(),
                utilisateur.getClient() != null ? utilisateur.getClient().getNom() : null,
                utilisateur.getClient() != null ? utilisateur.getClient().getPrenom() : null,
                utilisateur.getClient() != null ? utilisateur.getClient().getTelephone() : null,
                utilisateur.getClient() != null ? utilisateur.getClient().getEmail() : null,
                utilisateur.getClient() != null ? utilisateur.getClient().getPhotoIdentiteUrl() : null
        );
    }
}
