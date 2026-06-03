package com.soutra.microfinance.service.mobile;

public interface MobileAuthService {

    void creerOuModifierPin(Long idUser, String codePin);

    boolean verifierPin(Long idUser, String codePin);

    void activerBiometrie(Long idUser, String biometrieToken);

    void desactiverBiometrie(Long idUser);

    boolean estBiometrieActive(Long idUser);
}
