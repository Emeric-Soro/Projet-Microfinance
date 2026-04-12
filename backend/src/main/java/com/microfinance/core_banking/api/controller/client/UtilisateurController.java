package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.dto.request.client.CreationUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.request.client.LoginRequestDTO;
import com.microfinance.core_banking.dto.response.client.UtilisateurResponseDTO;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.service.client.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microfinance.core_banking.mapper.UtilisateurMapper;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@Tag(name = "Utilisateurs", description = "API de gestion des acces numeriques")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurController(UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
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
            description = "Verifie les identifiants login/mot de passe"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification reussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "400", description = "Donnees de connexion invalides")
    })
    @PostMapping("/login")
    public ResponseEntity<UtilisateurResponseDTO> authentifier(
            @Valid @RequestBody LoginRequestDTO requestDTO
    ) {
        // Retourne 401 si le login ou le mot de passe ne correspondent pas.
        return utilisateurService.authentifier(requestDTO.getLogin(), requestDTO.getMotDePasse())
                .map(utilisateur -> ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
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
