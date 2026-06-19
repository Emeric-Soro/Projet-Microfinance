package com.soutra.microfinance.mapper;

import com.soutra.microfinance.dto.response.client.ClientResponseDTO;
import com.soutra.microfinance.entity.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    @Test
    void shouldMapClientToResponseDTO() {
        Client client = buildClient();

        ClientResponseDTO dto = mapper.toResponseDTO(client);

        assertThat(dto).isNotNull();
        assertThat(dto.getIdClient()).isEqualTo(1L);
        assertThat(dto.getCodeClient()).isEqualTo("CLT-001");
        assertThat(dto.getNomComplet()).isEqualTo("Diallo Amadou");
        assertThat(dto.getStatut()).isEqualTo("ACTIF");
        assertThat(dto.getKycComplet()).isTrue();
    }

    @Test
    void shouldMaskNumeroPieceIdentite() {
        Client client = buildClient();
        client.setNumeroPieceIdentite("1234567890123456");

        ClientResponseDTO dto = mapper.toResponseDTO(client);

        assertThat(dto.getNumeroPieceIdentiteMasque()).isNotBlank();
    }

    @Test
    void shouldHandleNullFieldsGracefully() {
        Client client = new Client();
        client.setIdClient(2L);
        client.setCodeClient("CLT-002");
        client.setNom("Doe");
        client.setPrenom("John");
        client.setStatutClient(buildStatut("NOUVEAU"));
        client.setStatutKyc(StatutKycClient.BROUILLON);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setDateInscription(LocalDate.now());

        ClientResponseDTO dto = mapper.toResponseDTO(client);

        assertThat(dto).isNotNull();
        assertThat(dto.getNomComplet()).isEqualTo("Doe John");
    }

    @Test
    void shouldComputeKycCompletFalseWhenPiecesMissing() {
        Client client = buildClient();
        client.setNumeroPieceIdentite(null);

        ClientResponseDTO dto = mapper.toResponseDTO(client);

        assertThat(dto.getKycComplet()).isFalse();
    }

    private Client buildClient() {
        Client client = new Client();
        client.setIdClient(1L);
        client.setCodeClient("CLT-001");
        client.setNom("Diallo");
        client.setPrenom("Amadou");
        client.setDateNaissance(LocalDate.of(1990, 5, 15));
        client.setAdresse("Dakar, Senegal");
        client.setTelephone("+221770000000");
        client.setEmail("amadou.diallo@test.com");
        client.setTypePieceIdentite(TypePieceIdentite.CNI);
        client.setNumeroPieceIdentite("123456789");
        client.setDateExpirationPieceIdentite(LocalDate.now().plusYears(2));
        client.setPhotoIdentiteUrl("upload/photo.jpg");
        client.setJustificatifDomicileUrl("upload/domicile.pdf");
        client.setJustificatifRevenusUrl("upload/revenus.pdf");
        client.setProfession("Commercant");
        client.setPaysNationalite("Senegal");
        client.setPaysResidence("Senegal");
        client.setDateInscription(LocalDate.now());
        client.setStatutKyc(StatutKycClient.VALIDE);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setStatutClient(buildStatut("ACTIF"));
        client.setVersion(0);
        return client;
    }

    private StatutClient buildStatut(String libelle) {
        StatutClient statut = new StatutClient();
        statut.setIdStatutClient(1L);
        statut.setLibelleStatut(libelle);
        statut.setDateStatut(LocalDateTime.now());
        return statut;
    }
}
