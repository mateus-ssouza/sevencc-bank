package br.acc.bank.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.acc.bank.dto.transacao.TransacaoRequestDTO;
import br.acc.bank.dto.transacao.TransferenciaRequestDTO;
import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.InsufficientBalanceException;
import br.acc.bank.exception.InvalidNumericValueException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Conta;
import br.acc.bank.model.Transacao;
import br.acc.bank.model.enums.TipoTransacao;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.ContaRepository;
import br.acc.bank.repository.TransacaoRepository;
import br.acc.bank.util.Strings;
import jakarta.transaction.Transactional;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Realizar depósito em uma conta
    @Transactional
    public Transacao deposit(TransacaoRequestDTO transacao, String userLoginByToken) {
        try {
            // Verificar se o valor passado na transação é nulo ou positivo
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidNumericValueException(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE);
            }
            // Buscando cliente pelo login passado no token
            Optional<Cliente> cliente = clienteRepository.findByLogin(userLoginByToken);
            // Verifica se o cliente existe
            if (!cliente.isPresent())
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);

            // Buscando conta pelo id do cliente
            Optional<Conta> contaOrigem = contaRepository.findByClienteId(cliente.get().getId());
            // Verificar se conta existe
            if (!contaOrigem.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_ORIGIN);

            // Pegando os dados da conta
            Conta conta = contaOrigem.get();
            // Realizando deposito junto ao saldo da conta
            BigDecimal novoSaldo = conta.getSaldo().add(transacao.getValor());
            conta.setSaldo(novoSaldo);

            contaRepository.save(conta);
            // Criando transação como do tipo DEPOSITO
            Transacao savedTransacao = new Transacao(transacao.getValor(),
                    TipoTransacao.DEPOSITO, conta);

            return transacaoRepository.save(savedTransacao);
        } catch (InvalidNumericValueException e) {
            throw e;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.TRANSACAO.ERROR_CREATE, e);
        }
    }

    // Realizar saque em uma conta
    @Transactional
    public Transacao withdraw(TransacaoRequestDTO transacao, String userLoginByToken) {
        try {
            // Verificar se o valor passado na transação é nulo ou positivo
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidNumericValueException(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE);
            }
            // Buscando cliente pelo login passado no token
            Optional<Cliente> cliente = clienteRepository.findByLogin(userLoginByToken);
            // Verifica se o cliente existe
            if (!cliente.isPresent())
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);

            // Buscando conta pelo id do cliente
            Optional<Conta> contaOrigem = contaRepository.findByClienteId(cliente.get().getId());
            // Verificar se conta existe
            if (!contaOrigem.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_ORIGIN);
            
            // Pegando os dados da conta
            Conta conta = contaOrigem.get();
            // Verificando se a conta possui saldo suficiente para o saque
            if (conta.getSaldo().compareTo(transacao.getValor()) < 0) {
                throw new InsufficientBalanceException(Strings.TRANSACAO.INSUFFICIENT_BALANCE);
            }
            // Realizando saque junto ao saldo da conta
            BigDecimal novoSaldo = conta.getSaldo().subtract(transacao.getValor());
            conta.setSaldo(novoSaldo);

            contaRepository.save(conta);
            // Criando transação como do tipo SAQUE
            Transacao savedTransacao = new Transacao(transacao.getValor(),
                    TipoTransacao.SAQUE, conta);

            return transacaoRepository.save(savedTransacao);
        } catch (InvalidNumericValueException e) {
            throw e;
        } catch (NotFoundException e) {
            throw e;
        } catch (InsufficientBalanceException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.TRANSACAO.ERROR_CREATE, e);
        }
    }

    // Realizar transferencia entre contas
    @Transactional
    public Transacao transfer(TransferenciaRequestDTO transacao, String userLoginByToken) {
        try {
            // Verificar se o valor passado na transação é nulo ou positivo
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidNumericValueException(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE);
            }
            // Buscando cliente pelo login passado no token
            Optional<Cliente> cliente = clienteRepository.findByLogin(userLoginByToken);
            // Verifica se o cliente existe
            if (!cliente.isPresent())
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);
            
            // Buscando conta origem pelo id do cliente
            Optional<Conta> contaOrigem = contaRepository.findByClienteId(cliente.get().getId());
            // Verificar se conta origem existe
            if (!contaOrigem.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_ORIGIN);

            // Buscando conta destino pelo número da conta
            Optional<Conta> contaDestino = contaRepository.findByNumero(transacao.getNumeroContaDestino());
            // Verificar se conta destino existe
            if (!contaDestino.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_DESTINATION);

            // Verificar se conta origem e conta destino são as mesmas
            boolean contaOrigemIgualContaDestino = contaOrigem.get().getNumero() == contaDestino.get().getNumero();
            if (contaOrigemIgualContaDestino)
                throw new ConflictException(Strings.TRANSACAO.CONFLICT);

            Conta contaOrigemTransacao = contaOrigem.get();
            // Verificando se a conta de origem possui saldo para a transferencia
            if (contaOrigemTransacao.getSaldo().compareTo(transacao.getValor()) < 0) {
                throw new InsufficientBalanceException(Strings.TRANSACAO.INSUFFICIENT_BALANCE);
            }
            
            Conta contaDestinoTransacao = contaDestino.get();
            // Realizando o decréscimo no saldo da conta de origem
            BigDecimal novoSaldoContaOrigem = contaOrigemTransacao.getSaldo().subtract(transacao.getValor());
            contaOrigemTransacao.setSaldo(novoSaldoContaOrigem);

            // Realizando o acréscimo no saldo da conta de destino
            BigDecimal novoSaldoContaDestino = contaDestinoTransacao.getSaldo().add(transacao.getValor());
            contaDestinoTransacao.setSaldo(novoSaldoContaDestino);

            // Atualizando as contas com seus novos saldos
            contaRepository.save(contaOrigemTransacao);
            contaRepository.save(contaDestinoTransacao);

            // Criando transação como do tipo TRANSFERENCIA
            Transacao savedTransacao = new Transacao(transacao.getValor(),
                    TipoTransacao.TRANSFERENCIA, contaOrigemTransacao, contaDestinoTransacao);

            return transacaoRepository.save(savedTransacao);
        } catch (InvalidNumericValueException e) {
            throw e;
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            throw e;
        } catch (InsufficientBalanceException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.TRANSACAO.ERROR_CREATE, e);
        }
    }
}