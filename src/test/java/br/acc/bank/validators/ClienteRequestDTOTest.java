package br.acc.bank.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.acc.bank.dto.cliente.ClienteRequestDTO;
import br.acc.bank.dto.endereco.EnderecoRequestDTO;
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
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o nome for nulo")
    void shouldInvalidateWhenNomeIsNull() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setNome(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O nome é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o CPF for inválido")
    void shouldInvalidateWhenCpfIsInvalid() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setCpf("123"); // CPF inválido

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O CPF deve ter 11 dígitos", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o telefone for muito curto")
    void shouldInvalidateWhenTelefoneIsTooShort() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setTelefone("123456789"); // Telefone muito curto

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O telefone deve ter entre 10 e 13 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se a data de nascimento for no futuro")
    void shouldInvalidateWhenDataNascimentoIsInFuture() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setDataNascimento(LocalDate.now().plusDays(1)); // Data no futuro

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("A data de nascimento deve ser uma data no passado", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o e-mail for nulo")
    void shouldInvalidateWhenEmailIsNull() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setEmail(null);

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(2, violations.size());
        ConstraintViolation<ClienteRequestDTO> violation = violations.iterator().next();
        assertTrue(violation.getMessage().equals("O e-mail é obrigatório")
                || violation.getMessage().equals("O e-mail não deve estar em branco"));
    }

    @Test
    @DisplayName("Deve gerar uma violação se o e-mail for inválido")
    void shouldInvalidateWhenEmailIsInvalid() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setEmail("email-invalido"); // E-mail inválido

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O e-mail deve ser válido", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o login for muito curto")
    void shouldInvalidateWhenLoginIsTooShort() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setLogin("jo"); // Login muito curto

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O login deve ter entre 3 e 60 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o password for muito curto")
    void shouldInvalidateWhenPasswordIsTooShort() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setPassword("123"); // Senha muito curta

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O password deve ter entre 6 e 25 caracteres", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Deve gerar uma violação se o endereço for nulo")
    void shouldInvalidateWhenEnderecoIsNull() {
        ClienteRequestDTO clienteRequest = createValidClienteRequestDTO();
        clienteRequest.setEndereco(null); // Endereço nulo

        Set<ConstraintViolation<ClienteRequestDTO>> violations = validator.validate(clienteRequest);
        assertEquals(1, violations.size());
        assertEquals("O endereço é obrigatório", violations.iterator().next().getMessage());
    }

    private ClienteRequestDTO createValidClienteRequestDTO() {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345678");
        endereco.setPais("Brasil");

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("João da Silva");
        clienteRequest.setCpf("12345678901");
        clienteRequest.setTelefone("11987654321");
        clienteRequest.setDataNascimento(LocalDate.of(1990, 1, 1));
        clienteRequest.setEmail("joao.silva@example.com");
        clienteRequest.setLogin("joaosilva");
        clienteRequest.setPassword("senha123");
        clienteRequest.setEndereco(endereco);

        return clienteRequest;
    }
}
