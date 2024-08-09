package br.acc.bank.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.acc.bank.model.Cliente;
import br.acc.bank.model.Endereco;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class ClienteRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ClienteRepository clienteRepository;

    @Test
    @DisplayName("Deve obter um cliente buscado por email com sucesso")
    void testFindByEmailSuccess() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345678");
        endereco.setPais("Brasil");

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("12345678900");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEmail("joao.silva@example.com");
        cliente.setTelefone("11987654321");
        cliente.setEndereco(endereco);

        this.createCliente(cliente);

        Optional<Cliente> result = this.clienteRepository.findByEmail("joao.silva@example.com");

        assertTrue(result.isPresent());
        assertEquals(cliente.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("Não deve obter um cliente buscado por email com sucesso")
    void testFindByEmailNotFound() {
        Optional<Cliente> result = this.clienteRepository.findByEmail("email.inexistente@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve obter um cliente buscado por CPF com sucesso")
    void testFindByCpfSuccess() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345678");
        endereco.setPais("Brasil");

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("12345678900");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEmail("joao.silva@example.com");
        cliente.setTelefone("11987654321");
        cliente.setEndereco(endereco);

        this.createCliente(cliente);

        Optional<Cliente> result = this.clienteRepository.findByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals(cliente.getCpf(), result.get().getCpf());
    }

    @Test
    @DisplayName("Não deve obter um cliente buscado por CPF com sucesso")
    void testFindByCpfNotFound() {
        Optional<Cliente> result = this.clienteRepository.findByCpf("00000000000");

        assertFalse(result.isPresent());
    }

    private Cliente createCliente(Cliente cliente) {
        this.entityManager.persist(cliente);
        return cliente;
    }
}
