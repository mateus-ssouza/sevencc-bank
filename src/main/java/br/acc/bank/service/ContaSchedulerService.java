package br.acc.bank.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.acc.bank.model.Conta;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.repository.ContaRepository;

@Service
public class ContaSchedulerService {
    // Constante para representar 0.5% de juros
    private static final BigDecimal JUROS_MENSAL = BigDecimal.valueOf(0.005);
    // Constante para representar tarifa de R$ 20,00
    private static final BigDecimal TARIFA_MENSAL = BigDecimal.valueOf(20);

    @Autowired
    private ContaRepository contaRepository;

    // Aplicar juros as contas poupança com saldo positivo
    public void aplicarJurosMensal() {
        List<Conta> contas = contaRepository.findAllByTipo(TipoConta.POUPANCA);

        contas.stream()
                .filter(conta -> conta.getSaldo().compareTo(BigDecimal.ZERO) > 0)
                .forEach(conta -> {
                    BigDecimal saldoAtual = conta.getSaldo();
                    BigDecimal juros = saldoAtual.multiply(JUROS_MENSAL);
                    conta.setSaldo(saldoAtual.add(juros));
                    contaRepository.save(conta);
                });
    }

    // Aplicar tarifas a todas contas correntes
    public void aplicarTarifaMensal() {
        List<Conta> contas = contaRepository.findAllByTipo(TipoConta.CORRENTE);

        contas.stream()
                .forEach(conta -> {
                    BigDecimal saldoAtual = conta.getSaldo();
                    conta.setSaldo(saldoAtual.subtract(TARIFA_MENSAL));
                    contaRepository.save(conta);
                });
    }
}
