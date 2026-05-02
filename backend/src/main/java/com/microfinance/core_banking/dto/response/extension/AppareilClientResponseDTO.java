package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppareilClientResponseDTO {
    private Long idAppareilClient;
    private Long idClient;
    private String empreinteAppareil;
    private String plateforme;
    private String nomAppareil;
    private Boolean autorise;
    private LocalDateTime derniereConnexion;
}
