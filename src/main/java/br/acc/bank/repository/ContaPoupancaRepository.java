package br.acc.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.acc.bank.model.ContaPoupanca;

@Repository
public interface ContaPoupancaRepository extends JpaRepository<ContaPoupanca, Long> {
    Optional<ContaPoupanca> findByNumero(Long numero);
    boolean existsByNumero(Long numero);
}