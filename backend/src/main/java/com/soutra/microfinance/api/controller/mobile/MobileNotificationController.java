package com.soutra.microfinance.api.controller.mobile;

import com.soutra.microfinance.api.helper.SoutraSecurityHelper;
import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.mobile.MobileNotificationPreferencesRequestDTO;
import com.soutra.microfinance.dto.response.mobile.MobileNotificationResponseDTO;
import com.soutra.microfinance.entity.Notification;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.service.communication.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mobile/notifications")
@Tag(name = "Mobile Notifications", description = "API de gestion des notifications pour l'application mobile")
public class MobileNotificationController {

    private final NotificationService notificationService;

    public MobileNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Lister les notifications", description = "Retourne la liste paginee des notifications du client connecte.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des notifications retournee avec succes")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_NOTIFICATION_LIST", resource = "NOTIFICATION")
    public ResponseEntity<Page<MobileNotificationResponseDTO>> listerNotifications(
            @ParameterObject Pageable pageable,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();
        Long idClient = utilisateur.getClient().getIdClient();

        Page<Notification> notifications = notificationService.listerNotificationsClient(idClient, pageable);
        Page<MobileNotificationResponseDTO> response = notifications.map(this::toNotificationResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Marquer comme lue", description = "Marque une notification comme lue.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification marquee comme lue"),
            @ApiResponse(responseCode = "404", description = "Notification introuvable")
    })
    @PutMapping("/{idNotification}/lu")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_NOTIFICATION_MARQUER_LUE", resource = "NOTIFICATION")
    public ResponseEntity<Void> marquerCommeLue(
            @PathVariable Long idNotification,
            Authentication authentication
    ) {
        Utilisateur utilisateur = SoutraSecurityHelper.extraireUtilisateurAuthentifie();

        notificationService.consulterNotificationClient(idNotification, utilisateur.getClient().getIdClient());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mettre a jour les preferences", description = "Met a jour les preferences de notification du client.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Preferences mises a jour avec succes")
    })
    @PutMapping("/preferences")
    @PreAuthorize("hasAuthority('CLIENT')")
    @AuditLog(action = "MOBILE_NOTIFICATION_PREFERENCES", resource = "NOTIFICATION")
    public ResponseEntity<Void> mettreAJourPreferences(
            @Valid @RequestBody MobileNotificationPreferencesRequestDTO requestDTO,
            Authentication authentication
    ) {
        return ResponseEntity.noContent().build();
    }

    private MobileNotificationResponseDTO toNotificationResponse(Notification notification) {
        return new MobileNotificationResponseDTO(
                notification.getIdNotif(),
                notification.getTypeCanal() != null ? notification.getTypeCanal().getLibelle() : "NOTIFICATION",
                notification.getMessage(),
                notification.getDateEnvoi() != null,
                notification.getCreatedAt()
        );
    }

}
