package com.soutra.microfinance.api.controller.communication;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.communication.NotificationPreferencesRequestDTO;
import com.soutra.microfinance.dto.response.communication.NotificationPreferencesResponseDTO;
import com.soutra.microfinance.dto.response.communication.NotificationResponseDTO;
import com.soutra.microfinance.entity.Notification;
import com.soutra.microfinance.entity.NotificationPreference;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Historique des SMS/Emails envoyes aux clients")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Notifications d'un client", description = "Retourne la liste paginee des SMS/Emails envoyes a un client specifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des notifications retournee avec succes")
    })
    @GetMapping("/client/{idClient}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CLIENT')")
    public ResponseEntity<Page<NotificationResponseDTO>> notificationsClient(
            @PathVariable Long idClient,
            @ParameterObject Pageable pageable) {

        Page<Notification> notifications = notificationService.listerNotificationsClient(idClient, pageable);
        Page<NotificationResponseDTO> dtoPage = notifications.map(this::toResponseDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(summary = "Marquer une notification comme lue",
            description = "Marque la notification comme lue. Verifie que la notification appartient bien au client (multi-tenant). Idempotent : re-marquer une notif deja lue est sans effet.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification marquee comme lue"),
            @ApiResponse(responseCode = "404", description = "Notification introuvable ou n'appartient pas au client")
    })
    @PutMapping("/{id}/lu")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CLIENT')")
    @AuditLog(action = "NOTIFICATION_MARK_READ", resource = "NOTIFICATION")
    public ResponseEntity<Void> marquerCommeLue(
            @PathVariable Long id,
            @RequestParam Long idClient) {
        notificationService.marquerCommeLue(id, idClient);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mettre a jour les preferences de notification",
            description = "Active ou desactive les canaux push, SMS et email pour le client. Cree les preferences si elles n'existent pas (upsert).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferences mises a jour"),
            @ApiResponse(responseCode = "400", description = "Donnees de preferences invalides")
    })
    @PutMapping("/preferences")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERVISEUR','CLIENT')")
    @AuditLog(action = "NOTIFICATION_UPDATE_PREFERENCES", resource = "NOTIFICATION")
    public ResponseEntity<NotificationPreferencesResponseDTO> mettreAJourPreferences(
            @RequestParam Long idClient,
            @Valid @RequestBody NotificationPreferencesRequestDTO request) {
        NotificationPreference updated = notificationService.updatePreferences(
                idClient,
                request.getPushActif(),
                request.getSmsActif(),
                request.getEmailActif());
        return ResponseEntity.ok(toPreferencesResponseDTO(updated));
    }

    private NotificationResponseDTO toResponseDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getIdNotif());
        dto.setMessage(notification.getMessage());
        dto.setTypeNotification(notification.getTypeCanal() != null
                ? notification.getTypeCanal().getLibelle() : null);
        dto.setDateEnvoi(notification.getDateEnvoi());
        dto.setStatutEnvoi(notification.getStatutEnvoi() != null
                ? notification.getStatutEnvoi().getLibelle() : null);
        dto.setErreurEnvoi(notification.getErreurEnvoi());
        dto.setClientId(notification.getClient() != null
                ? notification.getClient().getIdClient() : null);
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setLu(notification.getLu());
        dto.setLueLe(notification.getLueLe());
        return dto;
    }

    private NotificationPreferencesResponseDTO toPreferencesResponseDTO(NotificationPreference pref) {
        NotificationPreferencesResponseDTO dto = new NotificationPreferencesResponseDTO();
        dto.setIdClient(pref.getIdClient());
        dto.setPushActif(pref.getPushActif());
        dto.setSmsActif(pref.getSmsActif());
        dto.setEmailActif(pref.getEmailActif());
        dto.setUpdatedAt(pref.getUpdatedAt());
        return dto;
    }
}
