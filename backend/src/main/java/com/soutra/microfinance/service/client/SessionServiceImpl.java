package com.soutra.microfinance.service.client;

import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.dto.response.client.SessionActiveResponseDTO;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SessionServiceImpl implements SessionService {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final JwtTokenBlacklistService jwtTokenBlacklistService;
    private final UtilisateurRepository utilisateurRepository;

    public SessionServiceImpl(
            JwtTokenBlacklistService jwtTokenBlacklistService,
            UtilisateurRepository utilisateurRepository
    ) {
        this.jwtTokenBlacklistService = jwtTokenBlacklistService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public List<SessionActiveResponseDTO> listerSessionsUtilisateur(Long idUser) {
        return sessions.values().stream()
                .filter(s -> s.idUser().equals(idUser))
                .map(s -> new SessionActiveResponseDTO(
                        s.sessionId(),
                        s.idUser(),
                        s.login(),
                        s.nomPrenom(),
                        s.adresseIp(),
                        s.userAgent(),
                        s.dateConnexion(),
                        s.derniereActivite(),
                        false
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void revoquerSession(Long idUser, String sessionId) {
        SessionInfo session = sessions.remove(sessionId);
        if (session != null) {
            jwtTokenBlacklistService.blacklist(session.token());
        }
    }

    @Override
    public void enregistrerSession(Long idUser, String sessionId, String token, String adresseIp, String userAgent) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        String nomPrenom = (utilisateur.getClient() != null)
                ? utilisateur.getClient().getPrenom() + " " + utilisateur.getClient().getNom()
                : utilisateur.getLogin();

        sessions.put(sessionId, new SessionInfo(
                sessionId,
                idUser,
                utilisateur.getLogin(),
                nomPrenom,
                token,
                adresseIp,
                userAgent,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
    }

    @Override
    public boolean estSessionValide(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    @Override
    public int countSessionsActives() {
        return sessions.size();
    }

    public void revoquerToutesSessionsUtilisateur(Long idUser) {
        List<String> sessionsARevoquer = sessions.values().stream()
                .filter(s -> s.idUser().equals(idUser))
                .map(SessionInfo::sessionId)
                .toList();

        sessionsARevoquer.forEach(sessionId -> {
            SessionInfo session = sessions.remove(sessionId);
            if (session != null) {
                jwtTokenBlacklistService.blacklist(session.token());
            }
        });
    }

    public List<SessionActiveResponseDTO> listerToutesSessions() {
        return sessions.values().stream()
                .map(s -> new SessionActiveResponseDTO(
                        s.sessionId(),
                        s.idUser(),
                        s.login(),
                        s.nomPrenom(),
                        s.adresseIp(),
                        s.userAgent(),
                        s.dateConnexion(),
                        s.derniereActivite(),
                        false
                ))
                .collect(Collectors.toList());
    }

    private record SessionInfo(
            String sessionId,
            Long idUser,
            String login,
            String nomPrenom,
            String token,
            String adresseIp,
            String userAgent,
            LocalDateTime dateConnexion,
            LocalDateTime derniereActivite
    ) {}
}
