package com.microfinance.core_banking.api.exception;

import com.microfinance.core_banking.dto.response.common.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        String details = extractEntityName(ex);
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, "RESSOURCE_INTROUVABLE", details);
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        String code = (ex instanceof ConstraintViolationException) ? "CONTRAINTE_VIOLATION" : "ARGUMENT_INVALIDE";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, code, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessConflict(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, "CONFLIT_METIER", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Requete invalide";
        }

        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> "\"" + fe.getField() + "\": \"" +
                        (fe.getDefaultMessage() != null ? fe.getDefaultMessage().replace("\"", "'") : "valeur invalide") + "\"")
                .collect(Collectors.joining(", ", "{", "}"));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, "VALIDATION_ECHOUEE", details);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Identifiants invalides", request, "AUTHENTIFICATION_ECHOUEE", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acces refuse", request, "ACCES_REFUSE", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        LOGGER.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        String safeMessage = "Une erreur interne est survenue. Veuillez reessayer plus tard.";
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                safeMessage,
                request,
                "ERREUR_INTERNE",
                null
        );
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            String code,
            String details
    ) {
        String correlationId = request.getHeader("X-Correlation-Id");
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                code,
                details,
                correlationId
        );
        return ResponseEntity.status(status).body(errorResponseDTO);
    }

    private String formatFieldError(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage() == null
                ? "valeur invalide"
                : fieldError.getDefaultMessage();
        return fieldError.getField() + " : " + defaultMessage;
    }

    private String extractEntityName(EntityNotFoundException ex) {
        String msg = ex.getMessage();
        if (msg != null && !msg.isBlank()) {
            String lower = msg.toLowerCase();
            if (lower.contains("introuvable") || lower.contains("not found")) {
                String[] parts = msg.split("\\s+");
                if (parts.length > 0) {
                    return parts[0];
                }
            }
            return msg;
        }
        return null;
    }
}
