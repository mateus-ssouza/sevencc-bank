package br.acc.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.acc.bank.model.Conta;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.repository.ContaRepository;

class ContaSchedulerServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaSchedulerService contaSchedulerService;

    private Conta contaPoupanca;
    private Conta contaCorrente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Conta poupança com saldo positivo
        contaPoupanca = new Conta();
        contaPoupanca.setTipo(TipoConta.POUPANCA);
        contaPoupanca.setSaldo(BigDecimal.valueOf(1000));

        // Conta corrente
        contaCorrente = new Conta();
        contaCorrente.setTipo(TipoConta.CORRENTE);
        contaCorrente.setSaldo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Deve aplicar juros mensalmente às contas poupança com saldo positivo")
    void testAplicarJurosMensal() {
        List<Conta> contasPoupanca = Arrays.asList(contaPoupanca);
        when(contaRepository.findAllByTipo(TipoConta.POUPANCA)).thenReturn(contasPoupanca);

        BigDecimal saldoEsperado = contaPoupanca.getSaldo()
                .add(contaPoupanca.getSaldo().multiply(BigDecimal.valueOf(0.005)));

        contaSchedulerService.aplicarJurosMensal();

        verify(contaRepository, times(1)).save(contaPoupanca);
        assertEquals(saldoEsperado, contaPoupanca.getSaldo());
    }

    @Test
    @DisplayName("Não deve aplicar juros a contas poupança com saldo zero ou negativo")
    void testAplicarJurosMensal_SaldoZeroOuNegativo() {
        Conta contaPoupancaZero = new Conta();
        contaPoupancaZero.setTipo(TipoConta.POUPANCA);
        contaPoupancaZero.setSaldo(BigDecimal.ZERO);

        List<Conta> contasPoupanca = Arrays.asList(contaPoupancaZero);
        when(contaRepository.findAllByTipo(TipoConta.POUPANCA)).thenReturn(contasPoupanca);

        contaSchedulerService.aplicarJurosMensal();

        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve aplicar tarifa mensalmente às contas correntes")
    void testAplicarTarifaMensal() {
        List<Conta> contasCorrente = Arrays.asList(contaCorrente);
        when(contaRepository.findAllByTipo(TipoConta.CORRENTE)).thenReturn(contasCorrente);

        BigDecimal saldoEsperado = contaCorrente.getSaldo().subtract(BigDecimal.valueOf(20));
        
        contaSchedulerService.aplicarTarifaMensal();

        verify(contaRepository, times(1)).save(contaCorrente);
        assertEquals(saldoEsperado, contaCorrente.getSaldo());
    }
}
