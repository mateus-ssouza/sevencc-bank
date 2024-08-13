package br.acc.bank.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.acc.bank.dto.conta.ContaRequestDTO;
import br.acc.bank.model.enums.TipoConta;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
public class ContaRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidContaRequestDTO() {
        ContaRequestDTO contaRequest = createValidContaRequestDTO();

        Set<ConstraintViolation<ContaRequestDTO>> violations = validator.validate(contaRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número da agência for nulo")
    void shouldInvalidateWhenNumeroDaAgenciaIsNull() {
        ContaRequestDTO contaRequest = createValidContaRequestDTO();
        contaRequest.setNumeroDaAgencia(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validator.validate(contaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ContaRequestDTO> violation = violations.iterator().next();
        assertEquals("O número da agência é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número da agência for negativo")
    void shouldInvalidateWhenNumeroDaAgenciaIsNegative() {
        ContaRequestDTO contaRequest = createValidContaRequestDTO();
        contaRequest.setNumeroDaAgencia(-123L); // Número da agência negativo

        Set<ConstraintViolation<ContaRequestDTO>> violations = validator.validate(contaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ContaRequestDTO> violation = violations.iterator().next();
        assertEquals("O número da agência deve ser positivo", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o tipo de conta for nulo")
    void shouldInvalidateWhenTipoIsNull() {
        ContaRequestDTO contaRequest = createValidContaRequestDTO();
        contaRequest.setTipo(null);

        Set<ConstraintViolation<ContaRequestDTO>> violations = validator.validate(contaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ContaRequestDTO> violation = violations.iterator().next();
        assertEquals("O tipo de transação é obrigatório", violation.getMessage());
    }

    private ContaRequestDTO createValidContaRequestDTO() {
        ContaRequestDTO contaRequest = new ContaRequestDTO();
        contaRequest.setNumeroDaAgencia(123L);
        contaRequest.setTipo(TipoConta.CORRENTE);

        return contaRequest;
    }
}
