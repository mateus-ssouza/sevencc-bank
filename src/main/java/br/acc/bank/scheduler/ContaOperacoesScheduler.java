package br.acc.bank.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.acc.bank.service.ContaSchedulerService;

@Component
public class ContaOperacoesScheduler {

    @Autowired
    private ContaSchedulerService schedulerService;

    // Aplica as opereações no primeiro dia de cada mês à meia-noite
    @Scheduled(cron = "0 0 0 1 * ?")
    public void verificarDataAplicarOperacoes() {
        // Aplica juros em todas as contas POUPANCA
        schedulerService.aplicarJurosMensal();
        // Aplica taxas em todas as contas CORRENTE
        schedulerService.aplicarTarifaMensal();
    }
}
