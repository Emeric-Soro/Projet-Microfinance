package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.config.JwtService;
import com.microfinance.core_banking.dto.request.client.CreationUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.request.client.LoginRequestDTO;
import com.microfinance.core_banking.dto.response.client.AuthenticationResponseDTO;
import com.microfinance.core_banking.dto.response.client.UtilisateurResponseDTO;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.mapper.UtilisateurMapper;
import com.microfinance.core_banking.service.client.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "API de gestion des acces numeriques")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UtilisateurController(
            UtilisateurService utilisateurService,
            UtilisateurMapper utilisateurMapper,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Creer un compte web",
            description = "Cree un acces web/mobile pour un client existant"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte web cree avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @PostMapping
    public ResponseEntity<UtilisateurResponseDTO> creerCompteWeb(
            @Valid @RequestBody CreationUtilisateurRequestDTO requestDTO
    ) {
        // Cree un utilisateur applicatif lie a un client existant.
        Utilisateur utilisateurCree = utilisateurService.creerCompteWeb(
                requestDTO.getIdClient(),
                requestDTO.getMotDePasseBrut()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurMapper.toResponseDTO(utilisateurCree));
    }

    @Operation(
            summary = "Authentifier un utilisateur",
            description = "Valide les identifiants via AuthenticationManager puis retourne un JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification reussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "400", description = "Donnees de connexion invalides")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> authentifier(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ) {
        // Delegue la verification login/mot de passe a Spring Security.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getLogin(), requestDTO.getMotDePasse())
        );

        // Le principal authentifie est ensuite utilise pour emettre le JWT.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        // Retourne le token et le profil utilisateur associe dans la meme reponse.
        Utilisateur utilisateur = (Utilisateur) userDetails;
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(
                token,
                utilisateurMapper.toResponseDTO(utilisateur)
        );
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Assigner un role",
            description = "Attribue un role metier a un utilisateur"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role assigne avec succes"),
            @ApiResponse(responseCode = "400", description = "Code role invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @PutMapping("/{idUser}/roles")
    public ResponseEntity<UtilisateurResponseDTO> assignerRole(
            @PathVariable Long idUser,
            @RequestParam String codeRole
    ) {
        // Met a jour les droits de l'utilisateur.
        Utilisateur utilisateur = utilisateurService.assignerRole(idUser, codeRole);
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }
}
