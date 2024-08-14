package br.acc.bank.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.acc.bank.model.Transacao;
import br.acc.bank.model.Conta;
import br.acc.bank.model.Endereco;
import br.acc.bank.model.Agencia;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.model.enums.TipoTransacao;
import br.acc.bank.model.enums.UsuarioRole;

@DataJpaTest
public class TransacaoRepositoryTest {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AgenciaRepository agenciaRepository;

    @Test
    @DisplayName("Deve encontrar transações por ID de conta com sucesso")
    void testFindByContaIdSuccess() {
        Conta contaOrigem = createContaValid();
        Conta contaDestino = createContaValid(TipoConta.POUPANCA);

        this.setContaOrigemEContaDestino(contaOrigem, contaDestino);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Transacao transacao = this.createTransacao(contaOrigem, contaDestino);

        transacaoRepository.save(transacao);

        List<Transacao> result = transacaoRepository.findByContaId(contaOrigem.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(t -> t.getContaOrigem().getId().equals(contaOrigem.getId())
                || t.getContaDestino().getId().equals(contaOrigem.getId())));
    }

    @Test
    @DisplayName("Não deve encontrar transações por ID de conta se não houver transações associadas")
    void testFindByContaIdNotFound() {
        Conta conta = createContaValid();

        Agencia agencia = this.agenciaRepository.save(conta.getAgencia());
        Cliente cliente = this.clienteRepository.save(conta.getCliente());

        conta.getAgencia().setId(agencia.getId());
        conta.getCliente().setId(cliente.getId());

        contaRepository.save(conta);

        List<Transacao> result = transacaoRepository.findByContaId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve encontrar transações por ID de conta de origem")
    void testFindByContaIdSuccessContaOrigem() {
        Conta contaOrigem = createContaValid();
        Conta contaDestino = createContaValid(TipoConta.POUPANCA);

        this.setContaOrigemEContaDestino(contaOrigem, contaDestino);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Transacao transacao = this.createTransacao(contaOrigem, contaDestino);

        transacaoRepository.save(transacao);

        List<Transacao> result = transacaoRepository.findByContaId(contaOrigem.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(t -> t.getContaOrigem().getId().equals(contaOrigem.getId())));
    }

    @Test
    @DisplayName("Deve encontrar transações por ID de conta de destino")
    void testFindByContaIdSuccessContaDestino() {
        Conta contaOrigem = createContaValid();
        Conta contaDestino = createContaValid(TipoConta.POUPANCA);

        this.setContaOrigemEContaDestino(contaOrigem, contaDestino);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Transacao transacao = this.createTransacao(contaOrigem, contaDestino);

        transacaoRepository.save(transacao);

        List<Transacao> result = transacaoRepository.findByContaId(contaDestino.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(t -> t.getContaDestino().getId().equals(contaDestino.getId())));
    }

    private Transacao createTransacao(Conta contaOrigem, Conta contaDestino) {
        Transacao transacao = new Transacao();
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        transacao.setValor(new BigDecimal("250.00"));
        transacao.setTipo(TipoTransacao.TRANSFERENCIA);
        transacao.setDataTransacao(LocalDateTime.now());

        return transacao;
    }

    private void setContaOrigemEContaDestino(Conta contaOrigem, Conta contaDestino) {
        Agencia agencia = this.agenciaRepository.save(contaOrigem.getAgencia());
        Cliente cliente1 = this.clienteRepository.save(contaOrigem.getCliente());
        Cliente cliente2 = this.clienteRepository.save(contaDestino.getCliente());

        contaOrigem.getAgencia().setId(agencia.getId());
        contaOrigem.getCliente().setId(cliente1.getId());

        contaDestino.getAgencia().setId(agencia.getId());
        contaDestino.getCliente().setId(cliente2.getId());

        contaOrigem.setNumero(12345L);
        contaDestino.setNumero(12346L);
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