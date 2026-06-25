package com.soutra.microfinance.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientBlacklistHistoryResponseDTO {
    private Long idHistory;
    private Long idClient;
    private String action;
    private String clientNom;
    private String clientPrenom;
    private String numeroClient;
    private String motif;
    private String details;
    private LocalDateTime dateAction;
    private String operateur;
}
