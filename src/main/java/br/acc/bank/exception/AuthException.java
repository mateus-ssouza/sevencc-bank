package br.acc.bank.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
