package com.soutra.microfinance.service.exception;

import com.soutra.microfinance.dto.request.parametrage.*;
import com.soutra.microfinance.dto.response.common.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExceptionService {

    DerogationResponseDTO creerDerogation(DerogationRequestDTO requestDTO, String creePar);

    List<DerogationResponseDTO> listerDerogations();

    Page<DerogationResponseDTO> listerDerogations(Pageable pageable);

    DerogationResponseDTO traiterDerogation(Long id, TraiterDerogationRequestDTO requestDTO, String traitePar);

    EscaladeResponseDTO creerEscalade(EscaladeRequestDTO requestDTO, String creePar);

    List<EscaladeResponseDTO> listerEscalades();

    Page<EscaladeResponseDTO> listerEscalades(Pageable pageable);

    EscaladeResponseDTO getEscaladeById(Long id);

    EscaladeResponseDTO traiterEscalade(Long id, TraiterEscaladeRequestDTO requestDTO, String traitePar);

    List<RegleDerogationEscaladeResponseDTO> listerRegles();
}
