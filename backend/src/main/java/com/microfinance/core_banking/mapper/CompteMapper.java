package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.response.compte.CarteVisaResponseDTO;
import com.microfinance.core_banking.dto.response.compte.CompteResponseDTO;
import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.entity.Compte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompteMapper {

    // On extrait le texte du TypeCompte
    @Mapping(source = "typeCompte.libelle", target = "typeCompte")
    // Le statut actuel est un peu complexe à récupérer via MapStruct car c'est un historique (StatutCompte).
    // On l'ignore ici, le Controller s'en chargera si besoin !
    @Mapping(target = "statut", ignore = true)
    CompteResponseDTO toCompteResponseDTO(Compte compte);

    // On dit à MapStruct d'utiliser notre méthode personnalisée (ci-dessous) pour le numéro
    @Mapping(target = "numeroCarteMasque", expression = "java(masquerNumero(carte.getNumeroCarte()))")
    @Mapping(target = "statut", expression = "java(carte.getStatut() ? \"ACTIF\" : \"INACTIF\")")
    CarteVisaResponseDTO toCarteVisaResponseDTO(CarteVisa carte);

    // Méthode personnalisée intégrée au Mapper
    default String masquerNumero(String numero) {
        if (numero == null || numero.length() < 16) return numero;
        // Ne garde que les 4 premiers et 4 derniers chiffres (Ex: 4123 **** **** 7890)
        return numero.substring(0, 4) + " **** **** " + numero.substring(12, 16);
    }
}