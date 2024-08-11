package br.acc.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.acc.bank.dto.conta.ContaRequestDTO;
import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.InvalidTypeException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Agencia;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Conta;
import br.acc.bank.model.ContaCorrente;
import br.acc.bank.model.ContaPoupanca;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.ContaRepository;
import br.acc.bank.util.Strings;
import jakarta.transaction.Transactional;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AgenciaRepository agenciaRepository;

    public List<Conta> getAll(TipoConta tipo) {
        try {
            if (tipo == null) {
                return contaRepository.findAll();
            } else {
                return contaRepository.findAllByTipo(tipo);
            }
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_FIND_ALL_LIST, e);
        }
    }

    public Optional<Conta> getById(Long id) {
        try {
            Optional<Conta> conta = contaRepository.findById(id);
            if (!conta.isPresent())
                throw new NotFoundException(Strings.CONTA.NOT_FOUND);

            return conta;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_FIND_BY_ID, e);
        }
    }

    @Transactional
    public Conta create(ContaRequestDTO conta, String userLoginByToken) {
        try {
            Optional<Agencia> agencia = agenciaRepository.findByNumero(conta.getNumeroDaAgencia());
            if (!agencia.isPresent())
                throw new NotFoundException(Strings.AGENCIA.NOT_FOUND);

            Optional<Cliente> cliente = clienteRepository.findByLogin(userLoginByToken);

            if (!cliente.isPresent())
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);

            boolean existeConta = contaRepository.existsByClienteId(cliente.get().getId());

            if (existeConta) 
                throw new ConflictException(Strings.CONTA.CONFLICT_ACCOUNT);

            Long numeroContaGerado = gerarNumeroConta();

            Conta savedConta;
            switch (conta.getTipo()) {
                case CORRENTE:
                    savedConta = new ContaCorrente(null, numeroContaGerado, agencia.get(), cliente.get());
                    break;
                case POUPANCA:
                    savedConta = new ContaPoupanca(null, numeroContaGerado, agencia.get(), cliente.get());
                    break;
                default:
                    throw new InvalidTypeException(Strings.CONTA.ERROR_TYPE_INVALID);
            }

            return contaRepository.save(savedConta);
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            throw e;
        } catch (InvalidTypeException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_CREATE, e);
        }
    }

    public void delete(Long id) {
        try {
            // Verificar se já existe uma conta pelo id
            Optional<Conta> conta = contaRepository.findById(id);
            if (!conta.isPresent())
                throw new NotFoundException(Strings.CONTA.NOT_FOUND);
            // Verificar se o saldo ta zerado
            BigDecimal saldo = conta.get().getSaldo();
            if (saldo.compareTo(BigDecimal.ZERO) != 0)
                throw new NotFoundException(Strings.CONTA.ERROR_NOT_ZEROED);

            contaRepository.deleteById(id);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_DELETE, e);
        }
    }

    private Long gerarNumeroConta() {
        Long numero;
        do {
            numero = digitosAleatorio();
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }

    private Long digitosAleatorio() {
        // Gerar um número aleatório de 6 dígitos entre 100.000 e 999.999
        return 100_000L + (long) (Math.random() * 900_000);
    }
}
