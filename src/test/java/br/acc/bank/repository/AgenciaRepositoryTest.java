package br.acc.bank.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.acc.bank.model.Agencia;
import br.acc.bank.model.Endereco;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class AgenciaRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AgenciaRepository agenciaRepository;

    @Test
    @DisplayName("Deve obter uma agência buscada por seu número com sucesso")
    void testFindByNumeroSucess() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("12345678");
        endereco.setPais("Brasil");

        Agencia agencia = new Agencia();
        agencia.setNome("Agência Central");
        agencia.setNumero(12345L);
        agencia.setTelefone("11987654321");
        agencia.setEndereco(endereco);

        this.createAgencia(agencia);

        Optional<Agencia> result = this.agenciaRepository.findByNumero(12345L);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Não deve obter uma agência buscada por seu número com sucesso")
    void testNotFoundAgenciaSucess() {

        Optional<Agencia> result = this.agenciaRepository.findByNumero(12345L);

        assertFalse(result.isPresent());
    }

    private Agencia createAgencia(Agencia agencia) {
        this.entityManager.persist(agencia);
        return agencia;
    }
}
