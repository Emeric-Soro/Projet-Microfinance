package com.soutra.microfinance.service.client;

import com.soutra.microfinance.dto.response.client.SessionActiveResponseDTO;

import java.util.List;

public interface SessionService {

    List<SessionActiveResponseDTO> listerSessionsUtilisateur(Long idUser);

    void revoquerSession(Long idUser, String sessionId);

    void enregistrerSession(Long idUser, String sessionId, String token, String adresseIp, String userAgent);

    boolean estSessionValide(String sessionId);

    int countSessionsActives();

    List<SessionActiveResponseDTO> listerToutesSessions();
}
