package br.acc.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.acc.bank.model.Conta;
import br.acc.bank.model.enums.TipoConta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByNumero(Long numero);
    boolean existsByNumero(Long numero);
    List<Conta> findAllByTipo(TipoConta tipo);
    boolean existsByClienteId(Long clienteId);
    Optional<Conta> findByClienteId(Long clienteId);
    boolean existsByAgenciaId(Long agenciaId);
}