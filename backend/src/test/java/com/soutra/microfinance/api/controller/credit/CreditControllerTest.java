package com.soutra.microfinance.api.controller.credit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutra.microfinance.config.JwtAuthenticationFilter;
import com.soutra.microfinance.config.JwtService;
import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.config.PublicApiRateLimitProperties;
import com.soutra.microfinance.config.PublicApiRateLimitingFilter;
import com.soutra.microfinance.dto.request.credit.DecaissementRequestDTO;
import com.soutra.microfinance.dto.request.credit.DecisionCreditRequestDTO;
import com.soutra.microfinance.dto.request.credit.DemandeCreditRequestDTO;
import com.soutra.microfinance.dto.request.credit.SimulationRequestDTO;
import com.soutra.microfinance.dto.response.credit.CreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.DemandeCreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.DecisionCreditResponseDTO;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.soutra.microfinance.entity.*;
import com.soutra.microfinance.mapper.CreditMapper;
import com.soutra.microfinance.service.credit.AmortissementService;
import com.soutra.microfinance.service.credit.CreditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({CreditController.class, DemandeCreditController.class})
@AutoConfigureMockMvc(addFilters = false)
@EnableConfigurationProperties(PublicApiRateLimitProperties.class)
class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreditService creditService;

    @MockitoBean
    private CreditMapper creditMapper;

    @MockitoBean
    private AmortissementService amortissementService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenBlacklistService jwtTokenBlacklistService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PublicApiRateLimitingFilter publicApiRateLimitingFilter;

    @Test
    void shouldSubmitCreditRequest() throws Exception {
        DemandeCreditRequestDTO request = new DemandeCreditRequestDTO(
                1L, "MC-COMMERCE", new BigDecimal("500000"), 12, "Achat de marchandises", null
        );
        DemandeCredit demande = buildDemandeCredit();
        DemandeCreditResponseDTO responseDTO = makeDemandeCreditResponseDTO(
                demande.getIdDemande(), demande.getReferenceDemande(), "Test Client",
                "MC-COMMERCE", "Micro Commerce", demande.getMontantDemande(),
                demande.getDureeSouhaitee(), demande.getObjetCredit(),
                demande.getDateDemande(), null, demande.getStatutDemande().name(),
                null
        );

        when(creditService.soumettreDemandeCredit(anyLong(), anyString(), any(BigDecimal.class), anyInt(), anyString(), any()))
                .thenReturn(demande);
        when(creditMapper.toDemandeCreditResponseDTO(any(DemandeCredit.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/credits/demandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceDemande").value("DEM-20260530-1234"));
    }

    @Test
    void shouldApproveCreditRequest() throws Exception {
        DecisionCreditRequestDTO request = new DecisionCreditRequestDTO(1L, "APPROUVEE", null);
        Credit credit = buildCredit();
        CreditResponseDTO creditResponseDTO = makeCreditResponseDTO(
                credit.getIdCredit(), credit.getReferenceCredit(), "Test Client",
                "MC-COMMERCE", "Micro Commerce", credit.getMontantAccorde(),
                credit.getMontantRestantDu(), credit.getTauxInteretAnnuel(),
                credit.getDureeMois(), credit.getMethodeCalcul().name(),
                credit.getFraisDossier(), credit.getDateDecaissement(),
                null, "APPROUVE", "CPT-001", credit.getReferenceCredit()
        );
        DecisionCreditResponseDTO responseDTO = new DecisionCreditResponseDTO(
                "APPROUVEE", creditResponseDTO, null
        );

        when(creditService.approuverDemande(anyLong())).thenReturn(credit);
        when(creditMapper.toCreditResponseDTO(any(Credit.class))).thenReturn(creditResponseDTO);

        mockMvc.perform(put("/api/v1/credits/demandes/1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credit.referenceCredit").value("CRD-20260530-5678"));
    }

    @Test
    void shouldRejectCreditRequest() throws Exception {
        DecisionCreditRequestDTO request = new DecisionCreditRequestDTO(1L, "REJETEE", "Risque eleve");
        DemandeCredit demande = buildDemandeCredit();
        demande.setStatutDemande(StatutDemande.REJETEE);
        demande.setMotifRejet("Risque eleve");
        DemandeCreditResponseDTO demandeResponseDTO = makeDemandeCreditResponseDTO(
                demande.getIdDemande(),
                demande.getReferenceDemande(),
                "Test Client",
                "MC-COMMERCE",
                "Micro Commerce",
                demande.getMontantDemande(),
                demande.getDureeSouhaitee(),
                demande.getObjetCredit(),
                demande.getDateDemande(),
                null,
                "REJETEE",
                "Risque eleve"
        );
        DecisionCreditResponseDTO responseDTO = new DecisionCreditResponseDTO(
                "REJETEE",
                null,
                demandeResponseDTO
        );

        when(creditService.rejeterDemande(anyLong(), anyString())).thenReturn(demande);
        when(creditMapper.toDemandeCreditResponseDTO(any(DemandeCredit.class))).thenReturn(demandeResponseDTO);

        mockMvc.perform(put("/api/v1/credits/demandes/1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("REJETEE"))
                .andExpect(jsonPath("$.demande.motifRejet").value("Risque eleve"));
    }

    @Test
    void shouldDisburseCredit() throws Exception {
        DecaissementRequestDTO request = new DecaissementRequestDTO("CPT-001");
        Credit credit = buildCredit();
        credit.setDateDecaissement(LocalDate.now());
        CreditResponseDTO creditResponseDTO = makeCreditResponseDTO(
                credit.getIdCredit(),
                credit.getReferenceCredit(),
                "Test Client",
                "MC-COMMERCE",
                "Micro Commerce",
                credit.getMontantAccorde(),
                credit.getMontantRestantDu(),
                credit.getTauxInteretAnnuel(),
                credit.getDureeMois(),
                credit.getMethodeCalcul().name(),
                credit.getFraisDossier(),
                credit.getDateDecaissement(),
                null,
                "APPROUVE",
                "CPT-001",
                null
        );

        when(creditService.decaisserCredit(anyLong(), anyString())).thenReturn(credit);
        when(creditMapper.toCreditResponseDTO(credit)).thenReturn(creditResponseDTO);

        mockMvc.perform(post("/api/v1/credits/1/decaissement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldListPendingRequests() throws Exception {
        DemandeCredit demande = buildDemandeCredit();
        Page<DemandeCredit> page = new PageImpl<>(List.of(demande));
        DemandeCreditResponseDTO responseDTO = makeDemandeCreditResponseDTO(
                demande.getIdDemande(), demande.getReferenceDemande(), "Test Client",
                "MC-COMMERCE", "Micro Commerce", demande.getMontantDemande(),
                demande.getDureeSouhaitee(), demande.getObjetCredit(),
                demande.getDateDemande(), null, "EN_ATTENTE", null
        );

        when(creditService.listerDemandesEnAttente(any())).thenReturn(page);
        when(creditMapper.toDemandeCreditResponseDTO(demande)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/credits/demandes")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldReturn404WhenCreditNotFound() throws Exception {
        when(creditService.consulterCredit(anyLong()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Credit introuvable: 999"));

        mockMvc.perform(get("/api/v1/credits/999"))
                .andExpect(status().isNotFound());
    }

    private DemandeCredit buildDemandeCredit() {
        DemandeCredit demande = new DemandeCredit();
        demande.setIdDemande(1L);
        demande.setReferenceDemande("DEM-20260530-1234");
        demande.setMontantDemande(new BigDecimal("500000"));
        demande.setDureeSouhaitee(12);
        demande.setObjetCredit("Achat de marchandises");
        demande.setDateDemande(LocalDate.now());
        demande.setStatutDemande(StatutDemande.EN_ATTENTE);
        Client client = new Client();
        client.setIdClient(1L);
        demande.setClient(client);
        ProduitCredit produit = new ProduitCredit();
        produit.setCodeProduit("MC-COMMERCE");
        demande.setProduitCredit(produit);
        return demande;
    }

    private Credit buildCredit() {
        Credit credit = new Credit();
        credit.setIdCredit(1L);
        credit.setReferenceCredit("CRD-20260530-5678");
        credit.setMontantAccorde(new BigDecimal("500000"));
        credit.setMontantRestantDu(new BigDecimal("500000"));
        credit.setTauxInteretAnnuel(new BigDecimal("0.12"));
        credit.setDureeMois(12);
        credit.setMethodeCalcul(MethodeCalculInteret.CONSTANT);
        credit.setFraisDossier(new BigDecimal("5000"));
        Client client = new Client();
        client.setIdClient(1L);
        credit.setClient(client);
        ProduitCredit produit = new ProduitCredit();
        produit.setCodeProduit("MC-COMMERCE");
        credit.setProduitCredit(produit);
        StatutCredit statut = new StatutCredit();
        statut.setCodeStatut("APPROUVE");
        statut.setLibelle("Approuve");
        credit.setStatutCredit(statut);
        return credit;
    }

    private DemandeCreditResponseDTO makeDemandeCreditResponseDTO(
            Long idDemande, String referenceDemande, String nomClient, String codeProduit, String libelleProduit,
            BigDecimal montantDemande, Integer dureeSouhaitee, String objetCredit, LocalDate dateDemande,
            LocalDateTime dateDecision, String statutDemande, String motifRejet) {
        return new DemandeCreditResponseDTO(
                idDemande, referenceDemande, nomClient, null, null, null, null, null, null,
                codeProduit, libelleProduit, montantDemande, dureeSouhaitee, objetCredit,
                dateDemande, dateDecision, statutDemande, motifRejet, null, null, null
        );
    }

    private CreditResponseDTO makeCreditResponseDTO(
            Long idCredit, String referenceCredit, String nomClient, String codeProduit, String libelleProduit,
            BigDecimal montantAccorde, BigDecimal montantRestantDu, BigDecimal tauxInteretAnnuel, Integer dureeMois,
            String methodeCalcul, BigDecimal fraisDossier, LocalDate dateDecaissement, LocalDate dateFinPrevue,
            String statutCredit, String numCompteDecaissement, String referenceDemande) {
        return new CreditResponseDTO(
                idCredit, null, referenceCredit, nomClient, codeProduit, libelleProduit,
                montantAccorde, montantRestantDu, tauxInteretAnnuel, dureeMois, methodeCalcul,
                fraisDossier, dateDecaissement, dateFinPrevue, statutCredit, numCompteDecaissement, referenceDemande
        );
    }
}
