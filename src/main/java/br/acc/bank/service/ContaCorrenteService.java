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
import br.acc.bank.model.ContaCorrente;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.ContaCorrenteRepository;
import br.acc.bank.util.Strings;

@Service
public class ContaCorrenteService {

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    @Autowired
    private AgenciaRepository agenciaRepository;

    public List<ContaCorrente> getAll() {
        try {
            return contaCorrenteRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_FIND_ALL_LIST, e);
        }
    }

    public Optional<ContaCorrente> getById(Long id) {
        try {
            Optional<ContaCorrente> contaCorrente = contaCorrenteRepository.findById(id);
            if (!contaCorrente.isPresent())
                throw new NotFoundException(Strings.CONTA.NOT_FOUND);

            return contaCorrente;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_FIND_BY_ID, e);
        }
    }

    public ContaCorrente create(ContaRequestDTO contaCorrente) {
        try {
            Optional<Agencia> agencia = agenciaRepository.findByNumero(contaCorrente.getNumeroDaAgencia());
            if (!agencia.isPresent())
                throw new NotFoundException(Strings.AGENCIA.NOT_FOUND);

            Long numeroContaGerado = gerarNumero();

            ContaCorrente conta = new ContaCorrente(null, numeroContaGerado, agencia.get());
            return contaCorrenteRepository.save(conta);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CONTA.ERROR_CREATE, e);
        }
    }

    public void delete(Long id) {
        try {
            // Verificar se já existe uma agencia pelo id
            Optional<ContaCorrente> conta = contaCorrenteRepository.findById(id);
            if (!conta.isPresent())
                throw new NotFoundException(Strings.CONTA.NOT_FOUND);
            // Veificar se o saldo ta zerado
            BigDecimal saldo = conta.get().getSaldo();
            if (saldo.compareTo(BigDecimal.ZERO) != 0)
                throw new NotFoundException(Strings.CONTA.ERROR_NOT_ZEROED);

            contaCorrenteRepository.deleteById(id);
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
        } while (contaCorrenteRepository.existsByNumero(numero));
        return numero;
    }

    private Long gerarNumeroAleatorio() {
        // Gerar um número aleatório de 7 dígitos
        // entre 1_000_000 e 4_999_999
        return 1_000_000L + (long) (Math.random() * 4_000_000);
    }

}
