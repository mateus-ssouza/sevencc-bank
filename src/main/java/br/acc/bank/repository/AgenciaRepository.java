package br.acc.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.acc.bank.model.Agencia;

@Repository
public interface AgenciaRepository extends JpaRepository <Agencia, Long>{
    Optional<Agencia> findByNumero(Long numero);
}
