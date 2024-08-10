package br.acc.bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.acc.bank.dto.ContaRequestDTO;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Agencia;
import br.acc.bank.model.ContaPoupanca;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.ContaPoupancaRepository;
import br.acc.bank.util.Strings;

@Service
public class ContaPoupancaService {

    @Autowired
    private ContaPoupancaRepository contaPoupancaRepository;

    @Autowired
    private AgenciaRepository agenciaRepository;

    public List<ContaPoupanca> getAll() {
        try {
            return contaPoupancaRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_FIND_ALL_LIST, e);
        }
    }

    public Optional<ContaPoupanca> getById(Long id) {
        try {
            Optional<ContaPoupanca> contaPoupanca = contaPoupancaRepository.findById(id);
            if (!contaPoupanca.isPresent())
                throw new NotFoundException(Strings.CONTA.NOT_FOUND);

            return contaPoupanca;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_FIND_BY_ID, e);
        }
    }

    public ContaPoupanca create(ContaRequestDTO contaPoupanca) {
        try {
            Optional<Agencia> agencia = agenciaRepository.findByNumero(contaPoupanca.getNumeroDaAgencia());
            if (!agencia.isPresent())
                throw new NotFoundException(Strings.AGENCIA.NOT_FOUND);

            Long numeroContaGerado = gerarNumero();

            ContaPoupanca conta = new ContaPoupanca(null, numeroContaGerado, agencia.get());
            return contaPoupancaRepository.save(conta);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_CREATE, e);
        }
    }

    public void delete(Long id) {
        try {
            // Verificar se já existe uma agencia pelo id
            Optional<ContaPoupanca> conta = contaPoupancaRepository.findById(id);
            if (!conta.isPresent())
                throw new NotFoundException(Strings.CONTA.NOT_FOUND);
            // Veificar se o saldo ta zerado
            BigDecimal saldo = conta.get().getSaldo();
            if (saldo.compareTo(BigDecimal.ZERO) != 0)
                throw new NotFoundException(Strings.CONTA.ERROR_NOT_ZEROED);

            contaPoupancaRepository.deleteById(id);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_DELETE, e);
        }
    }

    private Long gerarNumero() {
        Long numero;
        do {
            numero = gerarNumeroAleatorio();
        } while (contaPoupancaRepository.existsByNumero(numero));
        return numero;
    }

    private Long gerarNumeroAleatorio() {
        // Gerar um número aleatório de 7 dígitos
        // entre 5_000_000 e 9_999_999
        return 5_000_000L + (long) (Math.random() * 4_999_999);
    }

}
