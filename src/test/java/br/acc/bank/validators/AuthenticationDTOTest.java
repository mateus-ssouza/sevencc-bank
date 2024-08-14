package br.acc.bank.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.acc.bank.dto.auth.AuthenticationDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AuthenticationDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidAuthenticationDTO() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login for nulo")
    void shouldInvalidateWhenLoginIsNull() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();
        authDTO.setLogin(null);

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertEquals(1, violations.size());
        ConstraintViolation<AuthenticationDTO> violation = violations.iterator().next();
        assertEquals("O login é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login tiver menos de 3 caracteres")
    void shouldInvalidateWhenLoginIsTooShort() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();
        authDTO.setLogin("AB"); // Login muito curto

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertEquals(1, violations.size());
        ConstraintViolation<AuthenticationDTO> violation = violations.iterator().next();
        assertEquals("O login deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login tiver mais de 60 caracteres")
    void shouldInvalidateWhenLoginIsTooLong() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();
        authDTO.setLogin("A".repeat(61)); // Login muito longo

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertEquals(1, violations.size());
        ConstraintViolation<AuthenticationDTO> violation = violations.iterator().next();
        assertEquals("O login deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password for nulo")
    void shouldInvalidateWhenPasswordIsNull() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();
        authDTO.setPassword(null);

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertEquals(1, violations.size());
        ConstraintViolation<AuthenticationDTO> violation = violations.iterator().next();
        assertEquals("O password é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password tiver menos de 6 caracteres")
    void shouldInvalidateWhenPasswordIsTooShort() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();
        authDTO.setPassword("12345"); // Password muito curto

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertEquals(1, violations.size());
        ConstraintViolation<AuthenticationDTO> violation = violations.iterator().next();
        assertEquals("O password deve ter entre 6 e 25 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password tiver mais de 25 caracteres")
    void shouldInvalidateWhenPasswordIsTooLong() {
        AuthenticationDTO authDTO = createValidAuthenticationDTO();
        authDTO.setPassword("A".repeat(26)); // Password muito longo

        Set<ConstraintViolation<AuthenticationDTO>> violations = validator.validate(authDTO);

        assertEquals(1, violations.size());
        ConstraintViolation<AuthenticationDTO> violation = violations.iterator().next();
        assertEquals("O password deve ter entre 6 e 25 caracteres", violation.getMessage());
    }

    private AuthenticationDTO createValidAuthenticationDTO() {
        AuthenticationDTO authDTO = new AuthenticationDTO();
        authDTO.setLogin("validLogin");
        authDTO.setPassword("validPassword123");

        return authDTO;
    }
}
