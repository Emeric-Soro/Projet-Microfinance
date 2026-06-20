package com.soutra.microfinance.mapper;

import com.soutra.microfinance.dto.response.credit.CreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.DemandeCreditResponseDTO;
import com.soutra.microfinance.dto.response.credit.EcheanceResponseDTO;
import com.soutra.microfinance.dto.response.credit.GarantieResponseDTO;
import com.soutra.microfinance.dto.response.credit.TableauAmortissementResponseDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.DemandeCredit;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.Garantie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditMapper {

	// --- Demande de Credit ---

	@Mapping(target = "nomClient", expression = "java(demande.getClient().getNom() + \" \" + demande.getClient().getPrenom())")
	@Mapping(target = "codeProduit", source = "produitCredit.codeProduit")
	@Mapping(target = "libelleProduit", source = "produitCredit.libelle")
	@Mapping(target = "statutDemande", expression = "java(demande.getStatutDemande().name())")
	@Mapping(target = "nomAgentCredit", expression = "java(demande.getAgentCredit() != null ? demande.getAgentCredit().getUsername() : null)")
	DemandeCreditResponseDTO toDemandeCreditResponseDTO(DemandeCredit demande);

	// --- Credit ---

	@Mapping(target = "nomClient", expression = "java(credit.getClient().getNom() + \" \" + credit.getClient().getPrenom())")
	@Mapping(target = "codeProduit", source = "produitCredit.codeProduit")
	@Mapping(target = "libelleProduit", source = "produitCredit.libelle")
	@Mapping(target = "methodeCalcul", expression = "java(credit.getMethodeCalcul().name())")
	@Mapping(target = "statutCredit", source = "statutCredit.libelle")
	@Mapping(target = "numCompteDecaissement", expression = "java(credit.getCompteDecaissement() != null ? credit.getCompteDecaissement().getNumCompte() : null)")
	@Mapping(target = "referenceDemande", expression = "java(credit.getDemandeCredit() != null ? credit.getDemandeCredit().getReferenceDemande() : null)")
	CreditResponseDTO toCreditResponseDTO(Credit credit);

	// --- Echeance ---

	EcheanceResponseDTO toEcheanceResponseDTO(Echeance echeance);

	List<EcheanceResponseDTO> toEcheanceResponseDTOList(List<Echeance> echeances);

	@Mapping(target = "typeGarantie", expression = "java(garantie.getTypeGarantie().name())")
	GarantieResponseDTO toGarantieResponseDTO(Garantie garantie);

	List<GarantieResponseDTO> toGarantieResponseDTOList(List<Garantie> garanties);

	// --- Methode par defaut pour construire le tableau d'amortissement complet ---

	default TableauAmortissementResponseDTO toTableauAmortissementResponseDTO(Credit credit, List<Echeance> echeances) {
		List<EcheanceResponseDTO> echeanceDTOs = toEcheanceResponseDTOList(echeances);

		BigDecimal totalInterets = echeances.stream()
				.map(Echeance::getMontantInteret)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal coutTotal = credit.getMontantAccorde().add(totalInterets);

		return new TableauAmortissementResponseDTO(
				credit.getReferenceCredit(),
				credit.getMontantAccorde(),
				credit.getTauxInteretAnnuel(),
				credit.getDureeMois(),
				credit.getMethodeCalcul().name(),
				totalInterets,
				coutTotal,
				echeanceDTOs
		);
	}
}
