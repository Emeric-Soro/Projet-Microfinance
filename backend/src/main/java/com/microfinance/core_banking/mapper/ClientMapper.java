package com.microfinance.core_banking.mapper;

import com.microfinance.core_banking.dto.request.client.CreationClientRequestDTO;
import com.microfinance.core_banking.dto.response.client.ClientResponseDTO;
import com.microfinance.core_banking.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// componentModel = "spring" permet de pouvoir injecter ce mapper avec un constructeur dans tes futurs Controllers !
@Mapper(componentModel = "spring")
public interface ClientMapper {

    // 1. On dit à MapStruct comment fabriquer "nomComplet" en fusionnant le nom et le prenom
    @Mapping(target = "nomComplet", expression = "java(client.getNom() + \" \" + client.getPrenom())")
    // 2. On lui dit comment extraire le texte simple du statut depuis l'objet complexe StatutClient
    @Mapping(source = "statutClient.libelleStatut", target = "statut")
    @Mapping(target = "typePieceIdentite", expression = "java(client.getTypePieceIdentite() == null ? null : client.getTypePieceIdentite().name())")
    @Mapping(target = "numeroPieceIdentiteMasque", expression = "java(masquerPiece(client.getNumeroPieceIdentite()))")
    @Mapping(target = "niveauRisque", expression = "java(client.getNiveauRisque() == null ? null : client.getNiveauRisque().name())")
    @Mapping(target = "statutKyc", expression = "java(client.getStatutKyc() == null ? null : client.getStatutKyc().name())")
    @Mapping(target = "kycComplet", expression = "java(estKycComplet(client))")
    ClientResponseDTO toResponseDTO(Client client);

    // MapStruct va copier les champs du DTO vers l'Entité.
    // On ignore les champs techniques car c'est le Service ou la Base de données qui les génèrent !
    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "codeClient", ignore = true)
    @Mapping(target = "statutClient", ignore = true)
    @Mapping(target = "dateInscription", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "comptes", ignore = true)
    @Mapping(target = "niveauRisque", ignore = true)
    @Mapping(target = "statutKyc", ignore = true)
    @Mapping(target = "dateSoumissionKyc", ignore = true)
    @Mapping(target = "dateValidationKyc", ignore = true)
    @Mapping(target = "commentaireKyc", ignore = true)
    @Mapping(target = "validateurKyc", ignore = true)
    Client toEntity(CreationClientRequestDTO dto);

    default String masquerPiece(String numeroPiece) {
        if (numeroPiece == null || numeroPiece.length() <= 4) {
            return numeroPiece;
        }
        return "*".repeat(Math.max(0, numeroPiece.length() - 4)) + numeroPiece.substring(numeroPiece.length() - 4);
    }

    default boolean estKycComplet(Client client) {
        return client.getProfession() != null
                && !client.getProfession().isBlank()
                && client.getTypePieceIdentite() != null
                && client.getNumeroPieceIdentite() != null
                && !client.getNumeroPieceIdentite().isBlank()
                && client.getDateExpirationPieceIdentite() != null
                && !client.getDateExpirationPieceIdentite().isBefore(java.time.LocalDate.now())
                && client.getPhotoIdentiteUrl() != null
                && !client.getPhotoIdentiteUrl().isBlank()
                && client.getJustificatifDomicileUrl() != null
                && !client.getJustificatifDomicileUrl().isBlank()
                && client.getJustificatifRevenusUrl() != null
                && !client.getJustificatifRevenusUrl().isBlank()
                && client.getPaysNationalite() != null
                && !client.getPaysNationalite().isBlank()
                && client.getPaysResidence() != null
                && !client.getPaysResidence().isBlank();
    }
}
