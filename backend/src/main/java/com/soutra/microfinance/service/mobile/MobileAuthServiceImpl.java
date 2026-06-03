package com.soutra.microfinance.service.mobile;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MobileAuthServiceImpl implements MobileAuthService {

    private final Map<Long, String> pinStorage = new ConcurrentHashMap<>();
    private final Map<Long, String> biometrieStorage = new ConcurrentHashMap<>();

    @Override
    public void creerOuModifierPin(Long idUser, String codePin) {
        pinStorage.put(idUser, codePin);
    }

    @Override
    public boolean verifierPin(Long idUser, String codePin) {
        String storedPin = pinStorage.get(idUser);
        return storedPin != null && storedPin.equals(codePin);
    }

    @Override
    public void activerBiometrie(Long idUser, String biometrieToken) {
        biometrieStorage.put(idUser, biometrieToken);
    }

    @Override
    public void desactiverBiometrie(Long idUser) {
        biometrieStorage.remove(idUser);
    }

    @Override
    public boolean estBiometrieActive(Long idUser) {
        return biometrieStorage.containsKey(idUser);
    }
}
