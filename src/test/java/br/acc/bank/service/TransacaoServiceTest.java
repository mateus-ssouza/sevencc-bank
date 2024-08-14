package br.acc.bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import br.acc.bank.dto.transacao.TransacaoRequestDTO;
import br.acc.bank.dto.transacao.TransferenciaRequestDTO;
import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.InsufficientBalanceException;
import br.acc.bank.exception.InvalidNumericValueException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Conta;
import br.acc.bank.model.ContaCorrente;
import br.acc.bank.model.Transacao;
import br.acc.bank.model.enums.TipoTransacao;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.ContaRepository;
import br.acc.bank.repository.TransacaoRepository;
import br.acc.bank.util.Strings;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService transacaoService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Autowired
    public TransacaoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve realizar um depósito com sucesso")
    void testDeposit_Success() {
        TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100));
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        Conta conta = new ContaCorrente();
        conta.setSaldo(BigDecimal.valueOf(200));

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Transacao result = transacaoService.deposit(transacaoDTO, userLoginByToken);

        assertNotNull(result);
        assertEquals(TipoTransacao.DEPOSITO, result.getTipo());
        assertEquals(BigDecimal.valueOf(100), result.getValor());
        assertEquals(BigDecimal.valueOf(300), conta.getSaldo());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(contaRepository, times(1)).save(conta);
        verify(transacaoRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Deve lançar InvalidNumericValueException ao tentar depositar valor negativo")
    void testDeposit_InvalidValue() {
        TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(-100));
        String userLoginByToken = "clienteLogin";

        InvalidNumericValueException thrown = assertThrows(InvalidNumericValueException.class, () -> {
            transacaoService.deposit(transacaoDTO, userLoginByToken);
        });

        assertEquals(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE, thrown.getMessage());
        verify(clienteRepository, never()).findByLogin(userLoginByToken);
        verify(contaRepository, never()).findByClienteId(anyLong());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar depositar para cliente inexistente")
    void testDeposit_NotFoundCliente() {
        TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100));
        String userLoginByToken = "clienteLogin";

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            transacaoService.deposit(transacaoDTO, userLoginByToken);
        });

        assertEquals(Strings.CLIENTE.NOT_FOUND, thrown.getMessage());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, never()).findByClienteId(anyLong());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve realizar um saque com sucesso")
    @Transactional
    void testWithdraw_Success() {
        TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(50));
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        Conta conta = new ContaCorrente();
        conta.setSaldo(BigDecimal.valueOf(200));

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Transacao result = transacaoService.withdraw(transacaoDTO, userLoginByToken);

        assertNotNull(result);
        assertEquals(TipoTransacao.SAQUE, result.getTipo());
        assertEquals(BigDecimal.valueOf(50), result.getValor());
        assertEquals(BigDecimal.valueOf(150), conta.getSaldo());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(contaRepository, times(1)).save(conta);
        verify(transacaoRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException ao tentar sacar mais do que o saldo")
    void testWithdraw_InsufficientBalance() {
        TransacaoRequestDTO transacaoDTO = new TransacaoRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(500));
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        Conta conta = new ContaCorrente();
        conta.setSaldo(BigDecimal.valueOf(200));

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(conta));

        InsufficientBalanceException thrown = assertThrows(InsufficientBalanceException.class, () -> {
            transacaoService.withdraw(transacaoDTO, userLoginByToken);
        });

        assertEquals(Strings.TRANSACAO.INSUFFICIENT_BALANCE, thrown.getMessage());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(contaRepository, never()).save(any());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve realizar uma transferência com sucesso")
    void testTransfer_Success() {
        TransferenciaRequestDTO transacaoDTO = new TransferenciaRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100));
        transacaoDTO.setNumeroContaDestino(2L);
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        cliente.setId(1L);  // Defina um ID para o cliente
        Conta contaOrigem = new ContaCorrente();
        contaOrigem.setId(1L);  // Defina um ID para a conta origem
        contaOrigem.setNumero(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(200));
        Conta contaDestino = new ContaCorrente();
        contaDestino.setId(2L);  // Defina um ID para a conta destino
        contaDestino.setNumero(2L);
        contaDestino.setSaldo(BigDecimal.valueOf(100));

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumero(2L)).thenReturn(Optional.of(contaDestino));
        when(transacaoRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        Transacao result = transacaoService.transfer(transacaoDTO, userLoginByToken);

        assertNotNull(result);
        assertEquals(TipoTransacao.TRANSFERENCIA, result.getTipo());
        assertEquals(BigDecimal.valueOf(100), result.getValor());
        assertEquals(BigDecimal.valueOf(100), contaOrigem.getSaldo());
        assertEquals(BigDecimal.valueOf(200), contaDestino.getSaldo());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(contaRepository, times(1)).findByNumero(2L);
        verify(contaRepository, times(2)).save(any());
        verify(transacaoRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar transferir para a mesma conta")
    void testTransfer_SameAccount() {
        TransferenciaRequestDTO transacaoDTO = new TransferenciaRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100));
        transacaoDTO.setNumeroContaDestino(1L);
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        Conta contaOrigem = new ContaCorrente();
        contaOrigem.setNumero(1L);

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumero(1L)).thenReturn(Optional.of(contaOrigem));

        ConflictException thrown = assertThrows(ConflictException.class, () -> {
            transacaoService.transfer(transacaoDTO, userLoginByToken);
        });

        assertEquals(Strings.TRANSACAO.CONFLICT, thrown.getMessage());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(contaRepository, times(1)).findByNumero(1L);
        verify(contaRepository, never()).save(any());
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException se a conta de origem não tiver saldo suficiente")
    void testTransfer_InsufficientBalance() {
        TransferenciaRequestDTO transacaoDTO = new TransferenciaRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(500));
        transacaoDTO.setNumeroContaDestino(2L);
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        Conta contaOrigem = new ContaCorrente();
        contaOrigem.setId(1L);
        contaOrigem.setNumero(1L);
        contaOrigem.setSaldo(BigDecimal.valueOf(200));
        Conta contaDestino = new ContaCorrente();
        contaDestino.setId(2L);
        contaDestino.setNumero(2L);
        contaDestino.setSaldo(BigDecimal.valueOf(100));

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumero(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(InsufficientBalanceException.class, () -> {
            transacaoService.transfer(transacaoDTO, userLoginByToken);
        });
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar transferir para conta inexistente")
    void testTransfer_NotFoundDestination() {
        TransferenciaRequestDTO transacaoDTO = new TransferenciaRequestDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100));
        transacaoDTO.setNumeroContaDestino(2L);
        String userLoginByToken = "clienteLogin";

        Cliente cliente = new Cliente();
        Conta contaOrigem = new Conta();
        contaOrigem.setSaldo(BigDecimal.valueOf(200));

        when(clienteRepository.findByLogin(userLoginByToken)).thenReturn(Optional.of(cliente));
        when(contaRepository.findByClienteId(cliente.getId())).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumero(2L)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            transacaoService.transfer(transacaoDTO, userLoginByToken);
        });

        assertEquals(Strings.TRANSACAO.NOT_FOUND_DESTINATION, thrown.getMessage());
        verify(clienteRepository, times(1)).findByLogin(userLoginByToken);
        verify(contaRepository, times(1)).findByClienteId(cliente.getId());
        verify(contaRepository, times(1)).findByNumero(2L);
        verify(contaRepository, never()).save(any());
        verify(transacaoRepository, never()).save(any());
    }
}