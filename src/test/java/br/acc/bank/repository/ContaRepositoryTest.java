package br.acc.bank.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.acc.bank.model.Conta;
import br.acc.bank.model.Endereco;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.model.enums.UsuarioRole;
import br.acc.bank.model.Agencia;
import br.acc.bank.model.Cliente;

@DataJpaTest
public class ContaRepositoryTest {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AgenciaRepository agenciaRepository;

    @Test
    @DisplayName("Deve obter uma conta buscada por número com sucesso")
    void testFindByNumeroSuccess() {
        Conta conta = createContaValid();

        Agencia agencia = this.agenciaRepository.save(conta.getAgencia());
        Cliente cliente = this.clienteRepository.save(conta.getCliente());

        conta.getAgencia().setId(agencia.getId());
        conta.getCliente().setId(cliente.getId());

        this.contaRepository.save(conta);

        Optional<Conta> result = this.contaRepository.findByNumero(123456L);

        assertTrue(result.isPresent());
        assertEquals(conta.getNumero(), result.get().getNumero());
    }

    @Test
    @DisplayName("Não deve obter uma conta buscada por número se não existir")
    void testFindByNumeroNotFound() {
        Optional<Conta> result = this.contaRepository.findByNumero(999999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve verificar a existência de uma conta pelo número com sucesso")
    void testExistsByNumeroSuccess() {
        Conta conta = createContaValid();

        Agencia agencia = this.agenciaRepository.save(conta.getAgencia());
        Cliente cliente = this.clienteRepository.save(conta.getCliente());

        conta.getAgencia().setId(agencia.getId());
        conta.getCliente().setId(cliente.getId());

        this.contaRepository.save(conta);

        boolean exists = this.contaRepository.existsByNumero(123456L);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Não deve verificar a existência de uma conta pelo número se não existir")
    void testExistsByNumeroNotFound() {
        boolean exists = this.contaRepository.existsByNumero(999999L);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve obter todas as contas de um tipo específico com sucesso")
    void testFindAllByTipoSuccess() {
        Conta conta1 = createContaValid(TipoConta.CORRENTE);
        Conta conta2 = createContaValid(TipoConta.POUPANCA);

        Agencia agencia = this.agenciaRepository.save(conta1.getAgencia());
        Cliente cliente1 = this.clienteRepository.save(conta1.getCliente());
        Cliente cliente2 = this.clienteRepository.save(conta2.getCliente());

        conta1.getAgencia().setId(agencia.getId());
        conta1.getCliente().setId(cliente1.getId());

        conta2.getAgencia().setId(agencia.getId());
        conta2.getCliente().setId(cliente2.getId());

        conta1.setNumero(12345L);
        conta2.setNumero(12346L);

        this.contaRepository.save(conta1);
        this.contaRepository.save(conta2);

        List<Conta> contas = this.contaRepository.findAllByTipo(TipoConta.CORRENTE);

        assertEquals(1, contas.size());
        assertEquals(conta1.getTipo(), contas.get(0).getTipo());
    }

    @Test
    @DisplayName("Deve verificar a existência de uma conta por clienteId com sucesso")
    void testExistsByClienteIdSuccess() {
        Conta conta = createContaValid();

        Agencia agencia = this.agenciaRepository.save(conta.getAgencia());
        Cliente cliente = this.clienteRepository.save(conta.getCliente());

        conta.getAgencia().setId(agencia.getId());
        conta.getCliente().setId(cliente.getId());

        this.contaRepository.save(conta);

        boolean exists = this.contaRepository.existsByClienteId(cliente.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Não deve verificar a existência de uma conta por clienteId se não existir")
    void testExistsByClienteIdNotFound() {
        boolean exists = this.contaRepository.existsByClienteId(999L);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve obter uma conta buscada por clienteId com sucesso")
    void testFindByClienteIdSuccess() {
        Conta conta = createContaValid();

        Agencia agencia = this.agenciaRepository.save(conta.getAgencia());
        Cliente cliente = this.clienteRepository.save(conta.getCliente());

        conta.getAgencia().setId(agencia.getId());
        conta.getCliente().setId(cliente.getId());

        this.contaRepository.save(conta);

        Optional<Conta> result = this.contaRepository.findByClienteId(cliente.getId());

        assertTrue(result.isPresent());
        assertEquals(conta.getCliente().getId(), result.get().getCliente().getId());
    }

    @Test
    @DisplayName("Não deve obter uma conta buscada por clienteId se não existir")
    void testFindByClienteIdNotFound() {
        Optional<Conta> result = this.contaRepository.findByClienteId(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve verificar a existência de uma conta por agenciaId com sucesso")
    void testExistsByAgenciaIdSuccess() {
        Conta conta = createContaValid();

        Agencia agencia = this.agenciaRepository.save(conta.getAgencia());
        Cliente cliente = this.clienteRepository.save(conta.getCliente());

        conta.getAgencia().setId(agencia.getId());
        conta.getCliente().setId(cliente.getId());

        this.contaRepository.save(conta);

        boolean exists = this.contaRepository.existsByAgenciaId(1L);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Não deve verificar a existência de uma conta por agenciaId se não existir")
    void testExistsByAgenciaIdNotFound() {
        boolean exists = this.contaRepository.existsByAgenciaId(999L);

        assertFalse(exists);
    }

    private Conta createContaValid() {
        return createContaValid(TipoConta.CORRENTE);
    }

    private Conta createContaValid(TipoConta tipo) {
        Agencia agencia = createAgencia();

        Cliente cliente;
        if (tipo == TipoConta.CORRENTE) {
            cliente = createClienteCorrente();
        } else {
            cliente = createClientePoupanca();
        }

        return new Conta(
                null,
                123456L,
                new BigDecimal("1000.00"),
                tipo,
                agencia,
                cliente);
    }

    private Cliente createClienteCorrente() {
        Endereco endereco = createEndereco();

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("12345678900");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEmail("joao.silva@example.com");
        cliente.setTelefone("11987654321");
        cliente.setLogin("joao");
        cliente.setPassword("joao123");
        cliente.setRole(UsuarioRole.USUARIO);
        cliente.setEndereco(endereco);

        return cliente;
    }

    private Cliente createClientePoupanca() {
        Endereco endereco = createEndereco();

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setEmail("joao@example.com");
        cliente.setTelefone("11987654321");
        cliente.setLogin("joao2");
        cliente.setPassword("joao124");
        cliente.setRole(UsuarioRole.USUARIO);
        cliente.setEndereco(endereco);

        return cliente;
    }

    private Agencia createAgencia() {
        Endereco endereco = createEndereco();

        Agencia agencia = new Agencia();
        agencia.setNome("Agência Central");
        agencia.setNumero(12345L);
        agencia.setTelefone("11987654321");
        agencia.setEndereco(endereco);

        return agencia;
    }

    private Endereco createEndereco() {
        Endereco endereco = new Endereco();
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
