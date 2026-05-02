package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Journal des appels API externes")
public class JournalAppelExterneResponseDTO {
    @Schema(description = "Identifiant unique de l'appel", example = "1")
    private Long idJournalAppel;

    @Schema(description = "Code du partenaire", example = "PART-001")
    private String codePartenaire;

    @Schema(description = "Point d'entrée appelé", example = "/api/payments/transfer")
    private String endpoint;

    @Schema(description = "Méthode HTTP", example = "POST")
    private String methode;

    @Schema(description = "Statut de l'appel", example = "SUCCES")
    private String statut;

    @Schema(description = "Code statut HTTP", example = "200")
    private Integer codeStatutHttp;

    @Schema(description = "Adresse IP source", example = "192.168.1.1")
    private String ipSource;

    @Schema(description = "Date et heure de l'appel", example = "2026-05-02T14:30:00")
    private LocalDateTime dateAppel;

    @Schema(description = "Durée de l'appel en ms", example = "450")
    private Long dureeMs;

    @Schema(description = "Message d'erreur éventuel", example = "Timeout exceeded")
    private String erreurMessage;
}
