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
public class SessionActiveResponseDTO {

    private String sessionId;
    private Long idUser;
    private String login;
    private String nomPrenom;
    private String adresseIp;
    private String userAgent;
    private LocalDateTime dateConnexion;
    private LocalDateTime derniereActivite;
    private Boolean estSessionCourante;
}
