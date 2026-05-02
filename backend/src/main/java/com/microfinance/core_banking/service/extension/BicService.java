package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.ConsultationBic;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.ConsultationBicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BicService {

    private static final BigDecimal SEUIL_ENDETTEMENT_ELEVE = new BigDecimal("5000000");

    private final ConsultationBicRepository consultationBicRepository;
    private final ClientRepository clientRepository;
    private final ConformiteExtensionService conformiteExtensionService;

    public BicService(ConsultationBicRepository consultationBicRepository,
                      ClientRepository clientRepository,
                      ConformiteExtensionService conformiteExtensionService) {
        this.consultationBicRepository = consultationBicRepository;
        this.clientRepository = clientRepository;
        this.conformiteExtensionService = conformiteExtensionService;
    }

    @Transactional
    public ConsultationBic consulterClient(Long clientId, BigDecimal encoursTotal,
                                           BigDecimal encoursSain, BigDecimal encoursImpaye,
                                           Integer nombreCreances, String referenceClient) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + clientId));

        ConsultationBic consultation = new ConsultationBic();
        consultation.setClient(client);
        consultation.setReferenceClient(referenceClient != null ? referenceClient : client.getCodeClient());
        consultation.setNumeroPiece(client.getNumeroPieceIdentite());
        consultation.setEncoursTotal(encoursTotal);
        consultation.setEncoursSain(encoursSain);
        consultation.setEncoursImpaye(encoursImpaye);
        consultation.setNombreCreances(nombreCreances);
        consultation.setDateConsultation(LocalDate.now());

        if (encoursTotal != null && encoursTotal.compareTo(SEUIL_ENDETTEMENT_ELEVE) > 0) {
            consultation.setStatutBic("BIC_ALERTE");
        } else {
            consultation.setStatutBic("BIC_OK");
        }

        ConsultationBic saved = consultationBicRepository.save(consultation);

        if ("BIC_ALERTE".equals(saved.getStatutBic())) {
            conformiteExtensionService.creerAlerteBic(client, encoursTotal);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ConsultationBic> historiqueClient(Long clientId) {
        return consultationBicRepository.findByClient_IdClientOrderByDateConsultationDesc(clientId);
    }

    @Transactional(readOnly = true)
    public List<ConsultationBic> listerConsultations(LocalDate debut, LocalDate fin) {
        return consultationBicRepository.findByDateConsultationBetween(debut, fin);
    }
}
