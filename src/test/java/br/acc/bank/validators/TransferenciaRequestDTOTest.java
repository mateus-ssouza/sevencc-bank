package br.acc.bank.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.acc.bank.dto.transacao.TransferenciaRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
public class TransferenciaRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidTransferenciaRequestDTO() {
        TransferenciaRequestDTO transferenciaRequest = createValidTransferenciaRequestDTO();

        Set<ConstraintViolation<TransferenciaRequestDTO>> violations = validator.validate(transferenciaRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o valor da transação for nulo")
    void shouldInvalidateWhenValorIsNull() {
        TransferenciaRequestDTO transferenciaRequest = createValidTransferenciaRequestDTO();
        transferenciaRequest.setValor(null);

        Set<ConstraintViolation<TransferenciaRequestDTO>> violations = validator.validate(transferenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<TransferenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O valor da transação é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o valor da transação for menor que 0.01")
    void shouldInvalidateWhenValorIsLessThanMinimum() {
        TransferenciaRequestDTO transferenciaRequest = createValidTransferenciaRequestDTO();
        transferenciaRequest.setValor(new BigDecimal("0.00")); // Valor menor que o mínimo permitido

        Set<ConstraintViolation<TransferenciaRequestDTO>> violations = validator.validate(transferenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<TransferenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O valor da transação deve ser positivo e maior que zero", violation.getMessage());
    }

    @Test
    @DisplayName("Deve validar corretamente se o valor da transação for exatamente 0.01")
    void shouldValidateWhenValorIsExactlyMinimum() {
        TransferenciaRequestDTO transferenciaRequest = createValidTransferenciaRequestDTO();
        transferenciaRequest.setValor(new BigDecimal("0.01")); // Valor no mínimo permitido

        Set<ConstraintViolation<TransferenciaRequestDTO>> violations = validator.validate(transferenciaRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número da conta de destino for nulo")
    void shouldInvalidateWhenNumeroContaDestinoIsNull() {
        TransferenciaRequestDTO transferenciaRequest = createValidTransferenciaRequestDTO();
        transferenciaRequest.setNumeroContaDestino(null);

        Set<ConstraintViolation<TransferenciaRequestDTO>> violations = validator.validate(transferenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<TransferenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O número da conta de destino é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número da conta de destino for negativo")
    void shouldInvalidateWhenNumeroContaDestinoIsNegative() {
        TransferenciaRequestDTO transferenciaRequest = createValidTransferenciaRequestDTO();
        transferenciaRequest.setNumeroContaDestino(-12345L); // Número negativo

        Set<ConstraintViolation<TransferenciaRequestDTO>> violations = validator.validate(transferenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<TransferenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O número da conta de destino deve ser positivo", violation.getMessage());
    }

    private TransferenciaRequestDTO createValidTransferenciaRequestDTO() {
        TransferenciaRequestDTO transferenciaRequest = new TransferenciaRequestDTO();
        transferenciaRequest.setValor(new BigDecimal("100.00"));
        transferenciaRequest.setNumeroContaDestino(123456L);

        return transferenciaRequest;
    }
}
