package br.acc.bank.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
public class ClienteRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar corretamente um DTO válido")
    void shouldValidateValidClienteRequestDTO() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome for nulo")
    void shouldInvalidateWhenNomeIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome(null);
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome tiver menos de 3 caracteres")
    void shouldInvalidateWhenNomeIsTooShort() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("Jo"); // Nome muito curto
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome tiver mais de 60 caracteres")
    void shouldInvalidateWhenNomeIsTooLong() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("A".repeat(61)); // Nome muito longo
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O nome deve ter entre 3 e 60 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CPF for nulo")
    void shouldInvalidateWhenCpfIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf(null);
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O CPF é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CPF não tiver 11 dígitos")
    void shouldInvalidateWhenCpfIsInvalid() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("1234567890"); // CPF inválido
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O CPF deve ter 11 dígitos", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone for nulo")
    void shouldInvalidateWhenTelefoneIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone(null);
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone é obrigatório", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone tiver menos de 10 caracteres")
    void shouldInvalidateWhenTelefoneIsTooShort() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("123456789"); // Telefone muito curto
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone tiver mais de 13 caracteres")
    void shouldInvalidateWhenTelefoneIsTooLong() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("12345678901234"); // Telefone muito longo
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o e-mail for nulo")
    void shouldInvalidateWhenEmailIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setEmail(null);
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(2, violations.size());

        // Verifica a mensagem de violação para o campo 'email'
        boolean hasRequiredViolation = false;
        boolean hasNotBlankViolation = false;
        for (ConstraintViolation<ClienteRequestDTO> violation : violations) {
            if (violation.getPropertyPath().toString().equals("email")) {
                if (violation.getMessage().equals("O e-mail é obrigatório")) {
                    hasRequiredViolation = true;
                } else if (violation.getMessage().equals("O e-mail não deve estar em branco")) {
                    hasNotBlankViolation = true;
                }
            }
        }

        assertTrue(hasRequiredViolation);
        assertTrue(hasNotBlankViolation);
    }

    @Test
    @DisplayName("Deve gerar uma violação se o e-mail não for válido")
    void shouldInvalidateWhenEmailIsInvalid() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setEmail("joao.silva"); // E-mail inválido
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("O e-mail deve ser válido", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se a data de nascimento for nula")
    void shouldInvalidateWhenDataNascimentoIsNull() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setDataNascimento(null);
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("A data de nascimento é obrigatória", violation.getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se a data de nascimento estiver no futuro")
    void shouldInvalidateWhenDataNascimentoIsInFuture() {
        EnderecoRequestDTO endereco = createValidEnderecoRequestDTO();

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setDataNascimento(LocalDate.now().plusDays(1)); // Data futura
        clienteRequest.setEndereco(endereco);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);

        assertEquals(1, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertEquals("A data de nascimento deve ser uma data no passado", violation.getMessage());
    }

    private EnderecoRequestDTO createValidEnderecoRequestDTO() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setCep("12345678");
        endereco.setRua("Rua Exemplo");
        endereco.setNumero("123");
        endereco.setBairro("Bairro Exemplo");
        endereco.setCidade("Cidade Exemplo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");
        return endereco;
    }
}
