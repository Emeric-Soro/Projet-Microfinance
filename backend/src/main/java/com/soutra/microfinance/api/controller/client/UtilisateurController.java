package com.soutra.microfinance.api.controller.client;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.config.JwtService;
import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.dto.request.client.CreationUtilisateurRequestDTO;
import com.soutra.microfinance.dto.request.client.LoginRequestDTO;
import com.soutra.microfinance.dto.request.client.VerificationOtpRequestDTO;
import com.soutra.microfinance.dto.response.client.AuthenticationResponseDTO;
import com.soutra.microfinance.dto.response.client.AuthenticationStepStatus;
import com.soutra.microfinance.dto.response.client.UtilisateurResponseDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.UtilisateurMapper;
import com.soutra.microfinance.service.client.AuthenticationWorkflowResult;
import com.soutra.microfinance.service.client.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/utilisateurs")
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
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "409", description = "Compte web deja existant pour ce client")
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
            @ApiResponse(responseCode = "401", description = "Identifiants invalides ou compte verrouille"),
            @ApiResponse(responseCode = "400", description = "Donnees de connexion invalides")
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
            @ApiResponse(responseCode = "401", description = "OTP invalide ou expire"),
            @ApiResponse(responseCode = "400", description = "Donnees OTP invalides")
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
            summary = "Deconnecter un utilisateur",
            description = "Revoque le JWT courant en le mettant en liste noire jusqu'a expiration"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deconnexion prise en compte"),
            @ApiResponse(responseCode = "400", description = "Token manquant ou invalide")
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
        responseDTO.setRefreshToken(jwtService.generateRefreshToken(userDetails));
        responseDTO.setUtilisateur(utilisateurMapper.toResponseDTO(utilisateur));
        responseDTO.setStatutAuthentification(AuthenticationStepStatus.AUTHENTIFIE);
        responseDTO.setOtpRequis(Boolean.FALSE);
        responseDTO.setMessage("Authentification reussie");
        return responseDTO;
    }
}
