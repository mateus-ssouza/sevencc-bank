package br.acc.bank.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.acc.bank.dto.agencia.AgenciaRequestDTO;
import br.acc.bank.dto.endereco.EnderecoRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
public class AgenciaRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidAgenciaRequestDTO() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome for nulo")
    void shouldInvalidateWhenNomeIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome(null);
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome tiver menos de 3 caracteres")
    void shouldInvalidateWhenNomeIsTooShort() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("AB"); // Nome muito curto
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome deve ter entre 3 e 45 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome tiver mais de 45 caracteres")
    void shouldInvalidateWhenNomeIsTooLong() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("A".repeat(46)); // Nome muito longo
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome deve ter entre 3 e 45 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número for nulo")
    void shouldInvalidateWhenNumeroIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(null);
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O número é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o número for negativo")
    void shouldInvalidateWhenNumeroIsNegative() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(-12345L); // Número negativo
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O número deve ser positivo", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone for nulo")
    void shouldInvalidateWhenTelefoneIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone(null);
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone tiver menos de 10 caracteres")
    void shouldInvalidateWhenTelefoneIsTooShort() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("123456789"); // Telefone muito curto
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone tiver mais de 13 caracteres")
    void shouldInvalidateWhenTelefoneIsTooLong() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("12345678901234"); // Telefone muito longo
        agenciaRequest.setEndereco(endereco);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o endereço for nulo")
    void shouldInvalidateWhenEnderecoIsNull() {
        AgenciaRequestDTO agenciaRequest = new AgenciaRequestDTO();
        agenciaRequest.setNome("Agência Central");
        agenciaRequest.setNumero(12345L);
        agenciaRequest.setTelefone("11987654321");
        agenciaRequest.setEndereco(null);

        Set<ConstraintViolation<AgenciaRequestDTO>> violations = validator.validate(agenciaRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<AgenciaRequestDTO> violation = violations.iterator().next();
        assertEquals("O endereço é obrigatório", violation.getMessage());
    }

    private EnderecoRequestDTO createValidEnderecoRequestDTO() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345678");
        endereco.setPais("Brasil");
        return endereco;
    }
}