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
import br.acc.bank.model.Conta;
import br.acc.bank.model.Transacao;
import br.acc.bank.model.enums.TipoTransacao;
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

    @Transactional
    public Transacao deposit(TransacaoRequestDTO transacao, Long id) {
        try {
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidNumericValueException(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE);
            }

            Optional<Conta> contaOrigem = contaRepository.findById(id);
            if (!contaOrigem.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_ORIGIN);

            Conta conta = contaOrigem.get();
            BigDecimal novoSaldo = conta.getSaldo().add(transacao.getValor());
            conta.setSaldo(novoSaldo);

            contaRepository.save(conta);

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

    @Transactional
    public Transacao withdraw(TransacaoRequestDTO transacao, Long id) {
        try {
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidNumericValueException(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE);
            }

            Optional<Conta> contaOrigem = contaRepository.findById(id);
            if (!contaOrigem.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_ORIGIN);

            Conta conta = contaOrigem.get();

            if (conta.getSaldo().compareTo(transacao.getValor()) < 0) {
                throw new InsufficientBalanceException(Strings.TRANSACAO.INSUFFICIENT_BALANCE);
            }

            BigDecimal novoSaldo = conta.getSaldo().subtract(transacao.getValor());
            conta.setSaldo(novoSaldo);

            contaRepository.save(conta);

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

    @Transactional
    public Transacao transfer(TransferenciaRequestDTO transacao, Long id) {
        try {
            if (transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidNumericValueException(Strings.TRANSACAO.INVALID_TRANSACTION_VALUE);
            }

            Optional<Conta> contaOrigem = contaRepository.findById(id);
            if (!contaOrigem.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_ORIGIN);

            Optional<Conta> contaDestino = contaRepository.findByNumero(transacao.getNumeroContaDestino());
            if (!contaDestino.isPresent())
                throw new NotFoundException(Strings.TRANSACAO.NOT_FOUND_DESTINATION);

            boolean contaOrigemIgualContaDestino = contaOrigem.get().getNumero() == contaDestino.get().getNumero();

            if (contaOrigemIgualContaDestino)
                throw new ConflictException(Strings.TRANSACAO.CONFLICT);

            Conta contaOrigemTransacao = contaOrigem.get();

            if (contaOrigemTransacao.getSaldo().compareTo(transacao.getValor()) < 0) {
                throw new InsufficientBalanceException(Strings.TRANSACAO.INSUFFICIENT_BALANCE);
            }
            
            Conta contaDestinoTransacao = contaDestino.get();

            BigDecimal novoSaldoContaOrigem = contaOrigemTransacao.getSaldo().subtract(transacao.getValor());
            contaOrigemTransacao.setSaldo(novoSaldoContaOrigem);

            BigDecimal novoSaldoContaDestino = contaDestinoTransacao.getSaldo().add(transacao.getValor());
            contaDestinoTransacao.setSaldo(novoSaldoContaDestino);

            contaRepository.save(contaOrigemTransacao);
            contaRepository.save(contaDestinoTransacao);

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