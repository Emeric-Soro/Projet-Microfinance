package com.microfinance.core_banking.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponseDTO {

    private Long idUser;
    private String login;
    private Long idClient;
    private List<String> roles;
}

