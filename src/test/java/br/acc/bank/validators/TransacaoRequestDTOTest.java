package br.acc.bank.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.acc.bank.dto.transacao.TransacaoRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
public class TransacaoRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidTransacaoRequestDTO() {
        TransacaoRequestDTO transacaoRequest = createValidTransacaoRequestDTO();

        Set<ConstraintViolation<TransacaoRequestDTO>> violations = validator.validate(transacaoRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o valor da transação for nulo")
    void shouldInvalidateWhenValorIsNull() {
        TransacaoRequestDTO transacaoRequest = createValidTransacaoRequestDTO();
        transacaoRequest.setValor(null);

        Set<ConstraintViolation<TransacaoRequestDTO>> violations = validator.validate(transacaoRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<TransacaoRequestDTO> violation = violations.iterator().next();
        assertEquals("O valor da transação é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o valor da transação for menor que 0.01")
    void shouldInvalidateWhenValorIsLessThanMinimum() {
        TransacaoRequestDTO transacaoRequest = createValidTransacaoRequestDTO();
        transacaoRequest.setValor(new BigDecimal("0.00")); // Valor menor que o mínimo permitido

        Set<ConstraintViolation<TransacaoRequestDTO>> violations = validator.validate(transacaoRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<TransacaoRequestDTO> violation = violations.iterator().next();
        assertEquals("O valor da transação deve ser positivo e maior que zero", violation.getMessage());
    }

    @Test
    @DisplayName("Deve validar corretamente se o valor da transação for exatamente 0.01")
    void shouldValidateWhenValorIsExactlyMinimum() {
        TransacaoRequestDTO transacaoRequest = createValidTransacaoRequestDTO();
        transacaoRequest.setValor(new BigDecimal("0.01")); // Valor no mínimo permitido

        Set<ConstraintViolation<TransacaoRequestDTO>> violations = validator.validate(transacaoRequest);

        assertTrue(violations.isEmpty());
    }

    private TransacaoRequestDTO createValidTransacaoRequestDTO() {
        TransacaoRequestDTO transacaoRequest = new TransacaoRequestDTO();
        transacaoRequest.setValor(new BigDecimal("10.00"));

        return transacaoRequest;
    }
}
