package com.soutra.microfinance.service.conformite;

import com.soutra.microfinance.dto.request.conformite.ConsentementRgpdRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateAlerteLcbFtRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateRapportSarRequestDTO;
import com.soutra.microfinance.dto.request.conformite.CreateReclamationRequestDTO;
import com.soutra.microfinance.dto.request.conformite.TraiterAlerteLcbFtRequestDTO;
import com.soutra.microfinance.dto.request.conformite.TraiterReclamationRequestDTO;
import com.soutra.microfinance.dto.request.conformite.UpdateSarStatusRequestDTO;
import com.soutra.microfinance.dto.request.conformite.VerifierPepRequestDTO;
import com.soutra.microfinance.dto.response.conformite.AlerteLcbFtResponseDTO;
import com.soutra.microfinance.dto.response.conformite.ConsentementRgpdResponseDTO;
import com.soutra.microfinance.dto.response.conformite.KycExpireResponseDTO;
import com.soutra.microfinance.dto.response.conformite.PepResponseDTO;
import com.soutra.microfinance.dto.response.conformite.RapportSarResponseDTO;
import com.soutra.microfinance.dto.response.conformite.ReclamationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConformiteService {

    RapportSarResponseDTO creerRapportSar(CreateRapportSarRequestDTO dto);

    Page<RapportSarResponseDTO> listerRapportsSar(Pageable pageable);

    RapportSarResponseDTO getRapportSar(Long id);

    RapportSarResponseDTO mettreAJourStatutSar(Long id, UpdateSarStatusRequestDTO dto);

    ReclamationResponseDTO creerReclamation(CreateReclamationRequestDTO dto);

    Page<ReclamationResponseDTO> listerReclamations(Pageable pageable);

    ReclamationResponseDTO getReclamation(Long id);

    ReclamationResponseDTO traiterReclamation(Long id, TraiterReclamationRequestDTO dto);

    void enregistrerConsentement(ConsentementRgpdRequestDTO dto);

    List<ConsentementRgpdResponseDTO> exporterDonneesPersonnelles(Long idClient);

    void effacerDonnees(Long idClient);

    List<KycExpireResponseDTO> listerKycExpires();

    Page<KycExpireResponseDTO> listerKycExpires(Pageable pageable);

    PepResponseDTO verifierPep(VerifierPepRequestDTO dto);

    List<PepResponseDTO> listerPep();

    Page<PepResponseDTO> listerPep(Pageable pageable);

    AlerteLcbFtResponseDTO creerAlerte(CreateAlerteLcbFtRequestDTO dto);

    List<AlerteLcbFtResponseDTO> listerAlertesLcbFt();

    Page<AlerteLcbFtResponseDTO> listerAlertesLcbFt(Pageable pageable);

    AlerteLcbFtResponseDTO traiterAlerte(Long id, TraiterAlerteLcbFtRequestDTO dto);
}
