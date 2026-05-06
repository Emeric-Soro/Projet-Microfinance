package com.microfinance.core_banking.api.controller.communication;

import com.microfinance.core_banking.entity.Notification;
import com.microfinance.core_banking.repository.communication.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Historique des SMS/Emails envoyes aux clients")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Notifications d'un client", description = "Retourne la liste paginee des SMS/Emails envoyes a un client specifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des notifications retournee avec succes")
    })
    @GetMapping("/client/{idClient}")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Page<Notification>> notificationsClient(
            @PathVariable Long idClient,
            Pageable pageable) {

        return ResponseEntity.ok(notificationRepository.findByClient_IdClient(idClient, pageable));
    }
}
