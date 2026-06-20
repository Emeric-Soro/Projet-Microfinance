package com.soutra.microfinance.api.helper;

import com.soutra.microfinance.entity.Utilisateur;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SoutraSecurityHelper {

    private SoutraSecurityHelper() {
        throw new UnsupportedOperationException("Classe utilitaire");
    }

    public static Utilisateur extraireUtilisateurAuthentifie() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Utilisateur)) {
            throw new AccessDeniedException("Utilisateur non authentifie");
        }
        return (Utilisateur) auth.getPrincipal();
    }

    public static void verifierCorrespondanceUtilisateur(Long idRequete, Long idAuthentifie, String roleMetier) {
        if (idRequete == null || idAuthentifie == null || !idRequete.equals(idAuthentifie)) {
            throw new IllegalArgumentException("L'identifiant " + roleMetier + " doit correspondre a l'utilisateur authentifie");
        }
    }
}
