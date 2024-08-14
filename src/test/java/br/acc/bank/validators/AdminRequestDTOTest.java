package br.acc.bank.validators;



import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.acc.bank.dto.admin.AdminRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AdminRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidAdminRequestDTO() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome for nulo")
    void shouldInvalidateWhenNomeIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setNome(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome tiver menos de 3 caracteres")
    void shouldInvalidateWhenNomeIsTooShort() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setNome("AB"); // Nome muito curto

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome tiver mais de 60 caracteres")
    void shouldInvalidateWhenNomeIsTooLong() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setNome("A".repeat(61)); // Nome muito longo

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CPF for nulo")
    void shouldInvalidateWhenCpfIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setCpf(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O CPF é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CPF não tiver 11 dígitos")
    void shouldInvalidateWhenCpfIsInvalid() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setCpf("123456789"); // CPF inválido (menos de 11 dígitos)

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O CPF deve ter 11 dígitos", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone for nulo")
    void shouldInvalidateWhenTelefoneIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setTelefone(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone tiver menos de 10 caracteres")
    void shouldInvalidateWhenTelefoneIsTooShort() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setTelefone("123456789"); // Telefone muito curto

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone tiver mais de 13 caracteres")
    void shouldInvalidateWhenTelefoneIsTooLong() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setTelefone("12345678901234"); // Telefone muito longo

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se a data de nascimento for nula")
    void shouldInvalidateWhenDataNascimentoIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setDataNascimento(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("A data de nascimento é obrigatória", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se a data de nascimento for uma data futura")
    void shouldInvalidateWhenDataNascimentoIsInTheFuture() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setDataNascimento(LocalDate.now().plusDays(1)); // Data no futuro

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("A data de nascimento deve ser uma data no passado", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar duas violações se o e-mail for nulo")
    void shouldInvalidateWhenEmailIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setEmail(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(2, violations.size());

        boolean notNullViolationFound = false;
        boolean notBlankViolationFound = false;

        for (ConstraintViolation<AdminRequestDTO> violation : violations) {
            if (violation.getMessage().equals("O e-mail é obrigatório")) {
                notNullViolationFound = true;
            } else if (violation.getMessage().equals("O e-mail não deve estar em branco")) {
                notBlankViolationFound = true;
            }
        }

        assertTrue(notNullViolationFound, "A violação @NotNull deve estar presente");
        assertTrue(notBlankViolationFound, "A violação @NotBlank deve estar presente");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o e-mail estiver em branco")
    void shouldInvalidateWhenEmailIsBlank() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setEmail("");

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O e-mail não deve estar em branco", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o e-mail for inválido")
    void shouldInvalidateWhenEmailIsInvalid() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setEmail("emailinvalido"); // Email inválido

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O e-mail deve ser válido", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login for nulo")
    void shouldInvalidateWhenLoginIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setLogin(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O login é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login tiver menos de 3 caracteres")
    void shouldInvalidateWhenLoginIsTooShort() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setLogin("AB"); // Login muito curto

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O login deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login tiver mais de 60 caracteres")
    void shouldInvalidateWhenLoginIsTooLong() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setLogin("A".repeat(61)); // Login muito longo

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O login deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password for nulo")
    void shouldInvalidateWhenPasswordIsNull() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setPassword(null);

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O password é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password tiver menos de 6 caracteres")
    void shouldInvalidateWhenPasswordIsTooShort() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setPassword("12345"); // Senha muito curta

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O password deve ter entre 6 e 25 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password tiver mais de 25 caracteres")
    void shouldInvalidateWhenPasswordIsTooLong() {
        AdminRequestDTO adminRequest = createValidAdminRequestDTO();
        adminRequest.setPassword("A".repeat(26)); // Senha muito longa

        Set<ConstraintViolation<AdminRequestDTO>> violations = validator.validate(adminRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AdminRequestDTO> violation = violations.iterator().next();
        assertEquals("O password deve ter entre 6 e 25 caracteres", violation.getMessage());
    }

    private AdminRequestDTO createValidAdminRequestDTO() {
        return new AdminRequestDTO(
                "Admin Nome",
                "12345678901",
                "11987654321",
                LocalDate.of(1990, 1, 1),
                "admin@example.com",
                "adminlogin",
                "admin123");
    }
}
