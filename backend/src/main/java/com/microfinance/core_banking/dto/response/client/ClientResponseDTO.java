package com.microfinance.core_banking.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    private Long idClient;
    private String codeClient;
    private String nomComplet;
    private String email;
    private String telephone;
    private String statut;
}
