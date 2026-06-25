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
public class ClientBlacklistResponseDTO {
    private Long id;
    private Long idClient;
    private String numeroClient;
    private String nom;
    private String prenom;
    private String motif;
    private String details;
    private LocalDateTime dateBlacklist;
    private String blacklistePar;
}
