package br.acc.bank.exception;

public class InvalidNumericValueException extends RuntimeException {
    public InvalidNumericValueException(String message) {
        super(message);
    }
}
