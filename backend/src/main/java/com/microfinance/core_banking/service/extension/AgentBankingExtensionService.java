package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerAgentRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerPortefeuilleAgentRequestDTO;
import com.microfinance.core_banking.dto.request.extension.EffectuerTransactionAgentRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ReglerCommissionAgentRequestDTO;
import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Agent;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.CommissionAgent;
import com.microfinance.core_banking.entity.PortefeuilleAgent;
import com.microfinance.core_banking.entity.TransactionAgent;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.AgentRepository;
import com.microfinance.core_banking.repository.extension.CommissionAgentRepository;
import com.microfinance.core_banking.repository.extension.PortefeuilleAgentRepository;
import com.microfinance.core_banking.repository.extension.TransactionAgentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AgentBankingExtensionService {

    private final AgentRepository agentRepository;
    private final PortefeuilleAgentRepository portefeuilleAgentRepository;
    private final TransactionAgentRepository transactionAgentRepository;
    private final CommissionAgentRepository commissionAgentRepository;
    private final AgenceRepository agenceRepository;
    private final ClientRepository clientRepository;

    public AgentBankingExtensionService(
            AgentRepository agentRepository,
            PortefeuilleAgentRepository portefeuilleAgentRepository,
            TransactionAgentRepository transactionAgentRepository,
            CommissionAgentRepository commissionAgentRepository,
            AgenceRepository agenceRepository,
            ClientRepository clientRepository
    ) {
        this.agentRepository = agentRepository;
        this.portefeuilleAgentRepository = portefeuilleAgentRepository;
        this.transactionAgentRepository = transactionAgentRepository;
        this.commissionAgentRepository = commissionAgentRepository;
        this.agenceRepository = agenceRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Agent creerAgent(CreerAgentRequestDTO dto) {
        Agence agence = agenceRepository.findById(dto.getIdAgenceRattachement())
                .orElseThrow(() -> new EntityNotFoundException("Agence de rattachement introuvable"));
        Agent agent = new Agent();
        agent.setCodeAgent(dto.getCodeAgent());
        agent.setNomAgent(dto.getNomAgent());
        agent.setTelephone(dto.getTelephone());
        agent.setAdresse(dto.getAdresse());
        agent.setTypeAgent(dto.getTypeAgent());
        agent.setAgenceRattachement(agence);
        agent.setStatut("ACTIF");
        agent.setDateAgrement(LocalDate.now());
        return agentRepository.save(agent);
    }

    @Transactional
    public PortefeuilleAgent creerPortefeuille(CreerPortefeuilleAgentRequestDTO dto) {
        Agent agent = agentRepository.findById(dto.getIdAgent())
                .orElseThrow(() -> new EntityNotFoundException("Agent introuvable"));
        PortefeuilleAgent portefeuille = new PortefeuilleAgent();
        portefeuille.setAgent(agent);
        portefeuille.setPlafondMaximum(dto.getPlafondMaximum());
        portefeuille.setPlafondMinimum(dto.getPlafondMinimum() != null ? dto.getPlafondMinimum() : BigDecimal.ZERO);
        portefeuille.setSolde(BigDecimal.ZERO);
        portefeuille.setStatut("ACTIF");
        return portefeuilleAgentRepository.save(portefeuille);
    }

    @Transactional
    public TransactionAgent effectuerTransaction(EffectuerTransactionAgentRequestDTO dto) {
        Agent agent = agentRepository.findById(dto.getIdAgent())
                .orElseThrow(() -> new EntityNotFoundException("Agent introuvable"));
        Client client = clientRepository.findById(dto.getIdClient())
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable"));

        TransactionAgent transaction = new TransactionAgent();
        transaction.setAgent(agent);
        transaction.setClient(client);
        transaction.setTypeOperation(dto.getTypeOperation());
        transaction.setMontant(dto.getMontant());
        transaction.setFrais(dto.getFrais() != null ? dto.getFrais() : BigDecimal.ZERO);
        transaction.setReferenceTransaction("AGTX-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        transaction.setDateTransaction(LocalDateTime.now());
        transaction.setStatut("EFFECTUEE");
        return transactionAgentRepository.save(transaction);
    }

    @Transactional
    public CommissionAgent calculerCommissionAgent(Long idTransactionAgent) {
        TransactionAgent transaction = transactionAgentRepository.findById(idTransactionAgent)
                .orElseThrow(() -> new EntityNotFoundException("Transaction agent introuvable"));
        BigDecimal montantCommission = transaction.getMontant().multiply(BigDecimal.valueOf(0.01));
        CommissionAgent commission = new CommissionAgent();
        commission.setAgent(transaction.getAgent());
        commission.setTransactionAgent(transaction);
        commission.setTypeCommission("TRANSACTION");
        commission.setMontantCommission(montantCommission);
        commission.setDateCalcul(LocalDateTime.now());
        commission.setStatut("CALCULEE");
        return commissionAgentRepository.save(commission);
    }

    @Transactional
    public CommissionAgent reglerCommission(Long idCommissionAgent) {
        CommissionAgent commission = commissionAgentRepository.findById(idCommissionAgent)
                .orElseThrow(() -> new EntityNotFoundException("Commission agent introuvable"));
        commission.setStatut("PAYEE");
        commission.setDatePaiement(LocalDateTime.now());
        return commissionAgentRepository.save(commission);
    }

    @Transactional(readOnly = true)
    public List<Agent> listerAgentsByAgence(Long idAgence) {
        return agentRepository.findByAgenceRattachement_IdAgence(idAgence);
    }

    @Transactional(readOnly = true)
    public PortefeuilleAgent consulterPortefeuille(Long idAgent) {
        return portefeuilleAgentRepository.findByAgent_IdAgent(idAgent)
                .orElseThrow(() -> new EntityNotFoundException("Portefeuille agent introuvable"));
    }

    @Transactional
    public List<TransactionAgent> synchroniserTransactionsOffline(List<EffectuerTransactionAgentRequestDTO> transactionsDTO) {
        return transactionsDTO.stream().map(this::effectuerTransaction).toList();
    }
}
