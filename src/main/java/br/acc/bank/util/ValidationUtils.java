package br.acc.bank.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import br.acc.bank.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {

    public static void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            throw new ValidationException("Validação do(s) campo(s) falhou.", errors);
        }
    }
}