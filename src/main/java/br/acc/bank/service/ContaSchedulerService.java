package br.acc.bank.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.acc.bank.model.ContaCorrente;
import br.acc.bank.model.ContaPoupanca;
import br.acc.bank.repository.ContaCorrenteRepository;
import br.acc.bank.repository.ContaPoupancaRepository;

@Service
public class ContaSchedulerService {
    // 0.5% de juros
    private static final BigDecimal JUROS_MENSAL = BigDecimal.valueOf(0.005);
    // Tarifa de R$ 20,00
    private static final BigDecimal TARIFA_MENSAL = BigDecimal.valueOf(20); 

    @Autowired
    private ContaPoupancaRepository poupancaRepository;

    @Autowired
    private ContaCorrenteRepository correnteRepository;

    // Aplicar juros as contas poupança com saldo positivo
    public void aplicarJurosMensal() {
        List<ContaPoupanca> contas = poupancaRepository.findAll();

        contas.stream()
                .filter(conta -> conta.getSaldo().compareTo(BigDecimal.ZERO) > 0)
                .forEach(conta -> {
                    BigDecimal saldoAtual = conta.getSaldo();
                    BigDecimal juros = saldoAtual.multiply(JUROS_MENSAL);
                    conta.setSaldo(saldoAtual.add(juros));
                    poupancaRepository.save(conta);
                });
    }

    // Aplicar tarifas a todas contas correntes
    public void aplicarTarifaMensal() {
        List<ContaCorrente> contas = correnteRepository.findAll();

        contas.stream()
                .forEach(conta -> {
                    BigDecimal saldoAtual = conta.getSaldo();
                    conta.setSaldo(saldoAtual.subtract(TARIFA_MENSAL));
                    correnteRepository.save(conta);
                });
    }
}
