package br.acc.bank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import br.acc.bank.dto.conta.ContaRequestDTO;
import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.model.Agencia;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Conta;
import br.acc.bank.model.ContaCorrente;
import br.acc.bank.model.Transacao;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.ContaRepository;
import br.acc.bank.repository.TransacaoRepository;

class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta conta;
    private Cliente cliente;
    private Agencia agencia;
    private Transacao transacao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Dados de teste
        conta = new Conta();
        conta.setId(1L);
        conta.setSaldo(BigDecimal.valueOf(1000));

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setLogin("clienteLogin");

        agencia = new Agencia();
        agencia.setNumero(1234L);

        transacao = new Transacao();
        transacao.setId(1L);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setContaOrigem(conta);
    }

    @Test
    @DisplayName("Deve listar todas as contas quando o tipo não for fornecido")
    void testGetAllWithoutTipo() {
        List<Conta> contas = Arrays.asList(conta);
        when(contaRepository.findAll()).thenReturn(contas);

        List<Conta> result = contaService.getAll(null);

        assertEquals(contas, result);
        verify(contaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar contas por tipo")
    void testGetAllByTipo() {
        List<Conta> contas = Arrays.asList(conta);
        when(contaRepository.findAllByTipo(TipoConta.POUPANCA)).thenReturn(contas);

        List<Conta> result = contaService.getAll(TipoConta.POUPANCA);

        assertEquals(contas, result);
        verify(contaRepository, times(1)).findAllByTipo(TipoConta.POUPANCA);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando conta não for encontrada pelo id")
    void testGetById_NotFound() {
        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.getById(1L);
        });

        verify(contaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar conta quando encontrada pelo id")
    void testGetById_Success() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        Optional<Conta> result = contaService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(contaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar uma conta com sucesso")
    @Transactional
    void testCreate() {
        ContaRequestDTO contaDTO = new ContaRequestDTO();
        contaDTO.setNumeroDaAgencia(1234L);
        contaDTO.setTipo(TipoConta.CORRENTE);

        when(agenciaRepository.findByNumero(1234L)).thenReturn(Optional.of(agencia));
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.of(cliente));
        when(contaRepository.existsByClienteId(cliente.getId())).thenReturn(false);

        Conta novaConta = new ContaCorrente();
        when(contaRepository.save(any(Conta.class))).thenReturn(novaConta);

        Conta result = contaService.create(contaDTO, "clienteLogin");

        assertNotNull(result);
        verify(agenciaRepository, times(1)).findByNumero(1234L);
        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
        verify(contaRepository, times(1)).existsByClienteId(cliente.getId());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao criar conta se a agência não existir")
    void testCreate_AgenciaNotFound() {
        ContaRequestDTO contaDTO = new ContaRequestDTO();
        contaDTO.setNumeroDaAgencia(1234L);
        contaDTO.setTipo(TipoConta.CORRENTE);

        when(agenciaRepository.findByNumero(1234L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.create(contaDTO, "clienteLogin");
        });

        verify(agenciaRepository, times(1)).findByNumero(1234L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao criar conta se o cliente não existir")
    void testCreate_ClienteNotFound() {
        ContaRequestDTO contaDTO = new ContaRequestDTO();
        contaDTO.setNumeroDaAgencia(1234L);
        contaDTO.setTipo(TipoConta.CORRENTE);

        when(agenciaRepository.findByNumero(1234L)).thenReturn(Optional.of(agencia));
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.create(contaDTO, "clienteLogin");
        });

        verify(agenciaRepository, times(1)).findByNumero(1234L);
        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao criar conta se o cliente já tiver uma conta")
    void testCreate_ConflictException() {
        ContaRequestDTO contaDTO = new ContaRequestDTO();
        contaDTO.setNumeroDaAgencia(1234L);
        contaDTO.setTipo(TipoConta.CORRENTE);

        when(agenciaRepository.findByNumero(1234L)).thenReturn(Optional.of(agencia));
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.of(cliente));
        when(contaRepository.existsByClienteId(cliente.getId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            contaService.create(contaDTO, "clienteLogin");
        });

        verify(agenciaRepository, times(1)).findByNumero(1234L);
        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
        verify(contaRepository, times(1)).existsByClienteId(cliente.getId());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao remover conta que não existe")
    void testDelete_NotFound() {
        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.delete(1L);
        });

        verify(contaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao remover conta com saldo não zerado")
    void testDelete_ConflictException() {
        conta.setSaldo(BigDecimal.valueOf(100));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThrows(ConflictException.class, () -> {
            contaService.delete(1L);
        });

        verify(contaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve remover a conta com sucesso")
    void testDelete_Success() {
        conta.setSaldo(BigDecimal.ZERO);
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        contaService.delete(1L);

        verify(contaRepository, times(1)).findById(1L);
        verify(contaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve gerar extrato com sucesso")
    @Transactional
    void testGetExtrato() {
        List<Transacao> transacoes = Arrays.asList(transacao);
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(conta));
        when(transacaoRepository.findByContaId(conta.getId())).thenReturn(transacoes);

        List<Transacao> result = contaService.getExtrato("clienteLogin");

        assertEquals(transacoes, result);
        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(transacaoRepository, times(1)).findByContaId(conta.getId());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao gerar extrato se cliente não for encontrado")
    void testGetExtrato_ClienteNotFound() {
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.getExtrato("clienteLogin");
        });

        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao gerar extrato se conta não for encontrada")
    void testGetExtrato_ContaNotFound() {
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.getExtrato("clienteLogin");
        });

        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
    }

    @Test
    @DisplayName("Deve retornar detalhes da conta com sucesso")
    void testDetailsConta() {
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(conta));

        Optional<Conta> result = contaService.detailsConta("clienteLogin");

        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao visualizar detalhes da conta se cliente não for encontrado")
    void testDetailsConta_ClienteNotFound() {
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.detailsConta("clienteLogin");
        });

        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao visualizar detalhes da conta se conta não for encontrada")
    void testDetailsConta_ContaNotFound() {
        when(clienteRepository.findByLogin("clienteLogin")).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            contaService.detailsConta("clienteLogin");
        });

        verify(clienteRepository, times(1)).findByLogin("clienteLogin");
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
    }
}
