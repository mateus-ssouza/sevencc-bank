package br.acc.bank.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

// Validate criado para verificar se a data passada não é uma data futura
public class PastDateValidator implements ConstraintValidator<PastDate, LocalDate> {

    @Override
    public void initialize(PastDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || value.isBefore(LocalDate.now());
    }
}
