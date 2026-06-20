package com.soutra.microfinance.api.exception;

/**
 * Exception levee lorsqu'un token (refresh, OTP, PIN) est invalide ou expire.
 * Niveau HTTP : 401 Unauthorized.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
