package br.acc.bank.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
public class EnderecoRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidEnderecoRequestDTO() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertTrue(violations.isEmpty(), "Não deveria haver violações de validação.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CEP for nulo")
    void shouldInvalidateWhenCepIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep(null);
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O CEP é obrigatório", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CEP tiver menos de 8 caracteres")
    void shouldInvalidateWhenCepIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("1234567"); // CEP muito curto
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O CEP deve ter exatamente 8 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CEP tiver mais de 8 caracteres")
    void shouldInvalidateWhenCepIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("123456789"); // CEP muito longo
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O CEP deve ter exatamente 8 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se a rua for nula")
    void shouldInvalidateWhenRuaIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua(null);
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("A rua é obrigatória", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se a rua tiver menos de 3 caracteres")
    void shouldInvalidateWhenRuaIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("AB"); // Rua muito curta
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("A rua deve ter entre 3 e 60 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se a rua tiver mais de 60 caracteres")
    void shouldInvalidateWhenRuaIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("A".repeat(61)); // Rua muito longa
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("A rua deve ter entre 3 e 60 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número for nulo")
    void shouldInvalidateWhenNumeroIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero(null);
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O número é obrigatório", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número tiver menos de 1 caractere")
    void shouldInvalidateWhenNumeroIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero(""); // Número muito curto
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O número deve ter entre 1 e 10 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número tiver mais de 10 caracteres")
    void shouldInvalidateWhenNumeroIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("12345678901"); // Número muito longo
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O número deve ter entre 1 e 10 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o bairro for nulo")
    void shouldInvalidateWhenBairroIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro(null);
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O bairro é obrigatório", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o bairro tiver menos de 3 caracteres")
    void shouldInvalidateWhenBairroIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("AB"); // Bairro muito curto
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O bairro deve ter entre 3 e 50 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o bairro tiver mais de 50 caracteres")
    void shouldInvalidateWhenBairroIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("A".repeat(51)); // Bairro muito longo
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O bairro deve ter entre 3 e 50 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se a cidade for nula")
    void shouldInvalidateWhenCidadeIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade(null);
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("A cidade é obrigatória", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se a cidade tiver menos de 3 caracteres")
    void shouldInvalidateWhenCidadeIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("AB"); // Cidade muito curta
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("A cidade deve ter entre 3 e 50 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se a cidade tiver mais de 50 caracteres")
    void shouldInvalidateWhenCidadeIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("A".repeat(51)); // Cidade muito longa
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("A cidade deve ter entre 3 e 50 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o estado for nulo")
    void shouldInvalidateWhenEstadoIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado(null);
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O estado é obrigatório", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o estado tiver menos de 2 caracteres")
    void shouldInvalidateWhenEstadoIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("A"); // Estado muito curto
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O estado deve ter entre 2 e 60 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o estado tiver mais de 60 caracteres")
    void shouldInvalidateWhenEstadoIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("A".repeat(61)); // Estado muito longo
        endereco.setPais("Brasil");

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O estado deve ter entre 2 e 60 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o país for nulo")
    void shouldInvalidateWhenPaisIsNull() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais(null);

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O país é obrigatório", violation.getMessage(), "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o país tiver menos de 3 caracteres")
    void shouldInvalidateWhenPaisIsTooShort() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("BR"); // País muito curto

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O país deve ter entre 3 e 60 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }

    @Test
    @DisplayName("Deve gerar uma violação se o país tiver mais de 60 caracteres")
    void shouldInvalidateWhenPaisIsTooLong() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("A".repeat(61)); // País muito longo

        Set<ConstraintViolation<EnderecoRequestDTO>> violations = validator.validate(endereco);

        assertEquals(1, violations.size(), "Deveria haver uma violação de validação.");
        ConstraintViolation<EnderecoRequestDTO> violation = violations.iterator().next();
        assertEquals("O país deve ter entre 3 e 60 caracteres", violation.getMessage(),
                "A mensagem de violação está incorreta.");
    }
}