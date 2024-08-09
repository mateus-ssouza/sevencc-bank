package br.acc.bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Agencia;
import br.acc.bank.model.Endereco;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.EnderecoRepository;
import br.acc.bank.util.Strings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AgenciaServiceTest {

    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private AgenciaService agenciaService;

    public AgenciaServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve retornar todas as agências com sucesso")
    void testGetAllSuccess() {
        // Configurando o mock do repositório para retornar uma lista de agências
        Agencia agencia1 = new Agencia();
        agencia1.setNome("Agência 1");
        agencia1.setNumero(12345L);

        Agencia agencia2 = new Agencia();
        agencia2.setNome("Agência 2");
        agencia2.setNumero(67890L);

        when(agenciaRepository.findAll()).thenReturn(Arrays.asList(agencia1, agencia2));

        // Chamando o método a ser testado
        List<Agencia> result = agenciaService.getAll();

        // Verificando se o resultado é o esperado
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Agência 1", result.get(0).getNome());
        assertEquals("Agência 2", result.get(1).getNome());
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia se não houver agências")
    void testGetAllEmptyList() {
        // Configurando o mock do repositório para retornar uma lista vazia
        when(agenciaRepository.findAll()).thenReturn(Collections.emptyList());

        // Chamando o método a ser testado
        List<Agencia> result = agenciaService.getAll();

        // Verificando se o resultado é uma lista vazia
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao falhar em obter todas as agências")
    void testGetAllFailure() {
        // Configurando o mock do repositório para lançar uma exceção
        when(agenciaRepository.findAll()).thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        // Chamando o método a ser testado e verificando se a exceção correta é lançada
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            agenciaService.getAll();
        });

        // Verificando a mensagem da exceção
        assertEquals(Strings.AGENCIA.ERROR_FIND_ALL_LIST, exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma agência existente com sucesso")
    void testGetByIdSuccess() {
        // Configurando o mock para retornar uma agência existente
        Long agenciaId = 1L;
        Agencia agencia = new Agencia();
        agencia.setId(agenciaId);
        agencia.setNome("Agência Central");

        when(agenciaRepository.findById(agenciaId)).thenReturn(Optional.of(agencia));

        // Chamando o método a ser testado
        Optional<Agencia> result = agenciaService.getById(agenciaId);

        // Verificando se a agência foi encontrada com sucesso
        assertTrue(result.isPresent());
        assertEquals(agencia, result.get());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException se a agência não for encontrada")
    void testGetByIdNotFound() {
        // Configurando o mock para retornar um Optional vazio, simulando que a agência
        // não foi encontrada
        Long agenciaId = 1L;
        when(agenciaRepository.findById(agenciaId)).thenReturn(Optional.empty());

        // Chamando o método a ser testado e verificando se a exceção correta é lançada
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            agenciaService.getById(agenciaId);
        });

        // Verificando a mensagem da exceção
        assertEquals(Strings.AGENCIA.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer um erro inesperado ao buscar a agência")
    void testGetByIdFailure() {
        // Configurando o mock para lançar uma exceção genérica, simulando um erro
        // inesperado
        Long agenciaId = 1L;
        when(agenciaRepository.findById(agenciaId)).thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        // Chamando o método a ser testado e verificando se a exceção correta é lançada
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            agenciaService.getById(agenciaId);
        });

        // Verificando a mensagem da exceção
        assertEquals(Strings.AGENCIA.ERROR_FIND_BY_ID, exception.getMessage());
    }

     @Test
    @DisplayName("Deve criar uma nova agência com sucesso")
    void testCreateSuccess() {
        // Configurando o mock para não encontrar nenhuma agência existente com o mesmo número
        Agencia novaAgencia = new Agencia();
        novaAgencia.setNome("Agência Nova");
        novaAgencia.setNumero(12345L);

        when(agenciaRepository.findByNumero(novaAgencia.getNumero())).thenReturn(Optional.empty());
        when(agenciaRepository.save(novaAgencia)).thenReturn(novaAgencia);

        // Chamando o método a ser testado
        Agencia result = agenciaService.create(novaAgencia);

        // Verificando se a agência foi criada com sucesso
        assertNotNull(result, "A agência criada não deve ser nula");
        assertEquals(novaAgencia.getNome(), result.getNome());
        assertEquals(novaAgencia.getNumero(), result.getNumero());

        // Verificando se o método save foi chamado para o endereço e a agência
        verify(enderecoRepository, times(1)).save(novaAgencia.getEndereco());
        verify(agenciaRepository, times(1)).save(novaAgencia);
    }

    @Test
    @DisplayName("Deve lançar ConflictException se o número da agência já existir")
    void testCreateConflict() {
        // Configurando o mock para simular que uma agência com o mesmo número já existe
        Agencia agenciaExistente = new Agencia();
        agenciaExistente.setNumero(12345L);

        when(agenciaRepository.findByNumero(agenciaExistente.getNumero())).thenReturn(Optional.of(agenciaExistente));

        // Chamando o método a ser testado e verificando se a exceção correta é lançada
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            agenciaService.create(agenciaExistente);
        });

        // Verificando a mensagem da exceção
        assertEquals(Strings.AGENCIA.CONFLICT, exception.getMessage());

        // Verificando se o método save não foi chamado
        verify(agenciaRepository, never()).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer um erro inesperado ao criar a agência")
    void testCreateFailure() {
        // Configurando o mock para lançar uma exceção genérica ao tentar salvar a agência
        Agencia novaAgencia = new Agencia();
        novaAgencia.setNumero(12345L);

        when(agenciaRepository.findByNumero(novaAgencia.getNumero())).thenReturn(Optional.empty());
        when(agenciaRepository.save(novaAgencia)).thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        // Chamando o método a ser testado e verificando se a exceção correta é lançada
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            agenciaService.create(novaAgencia);
        });

        // Verificando a mensagem da exceção
        assertEquals(Strings.AGENCIA.ERROR_CREATE, exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar uma agência com sucesso")
    void testUpdateSuccess() {
        Long agenciaId = 1L;

        // Simula a agência existente
        Agencia agenciaExistente = new Agencia();
        agenciaExistente.setId(agenciaId);
        agenciaExistente.setNumero(12345L);

        // Nova agência com os dados atualizados
        Agencia agenciaAtualizada = new Agencia();
        agenciaAtualizada.setNome("Agência Atualizada");
        agenciaAtualizada.setNumero(54321L);
        agenciaAtualizada.setTelefone("11987654321");
        agenciaAtualizada.setEndereco(new Endereco());

        when(agenciaRepository.findById(agenciaId)).thenReturn(Optional.of(agenciaExistente));
        when(agenciaRepository.findByNumero(54321L)).thenReturn(Optional.empty());
        when(agenciaRepository.save(any(Agencia.class))).thenReturn(agenciaAtualizada);

        // Chamando o método a ser testado
        Agencia result = agenciaService.update(agenciaId, agenciaAtualizada);

        // Verificando se a atualização foi feita corretamente
        assertNotNull(result);
        assertEquals(agenciaAtualizada.getNome(), result.getNome());
        assertEquals(agenciaAtualizada.getNumero(), result.getNumero());

        // Verificando se os métodos foram chamados corretamente
        verify(agenciaRepository).findById(agenciaId);
        verify(agenciaRepository).findByNumero(54321L);
        verify(enderecoRepository).save(agenciaAtualizada.getEndereco());
        verify(agenciaRepository).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException se a agência não for encontrada")
    void testUpdateNotFound() {
        Long agenciaId = 1L;

        when(agenciaRepository.findById(agenciaId)).thenReturn(Optional.empty());

        Agencia agenciaAtualizada = new Agencia();
        agenciaAtualizada.setNome("Agência Atualizada");

        // Verificando se a exceção NotFoundException é lançada
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            agenciaService.update(agenciaId, agenciaAtualizada);
        });

        assertEquals(Strings.AGENCIA.NOT_FOUND, exception.getMessage());

        // Verificando se os métodos não foram chamados
        verify(agenciaRepository).findById(agenciaId);
        verify(agenciaRepository, never()).findByNumero(anyLong());
        verify(agenciaRepository, never()).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException se o número da agência já existir e pertencer a outra agência")
    void testUpdateConflict() {
        Long agenciaId = 1L;
        Long numeroExistente = 54321L;

        // Simula a agência existente
        Agencia agenciaExistente = new Agencia();
        agenciaExistente.setId(agenciaId);
        agenciaExistente.setNumero(12345L);

        // Simula outra agência com o número em conflito
        Agencia agenciaComMesmoNumero = new Agencia();
        agenciaComMesmoNumero.setId(2L);
        agenciaComMesmoNumero.setNumero(numeroExistente);

        when(agenciaRepository.findById(agenciaId)).thenReturn(Optional.of(agenciaExistente));
        when(agenciaRepository.findByNumero(numeroExistente)).thenReturn(Optional.of(agenciaComMesmoNumero));

        Agencia agenciaAtualizada = new Agencia();
        agenciaAtualizada.setNumero(numeroExistente);

        // Verificando se a exceção ConflictException é lançada
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            agenciaService.update(agenciaId, agenciaAtualizada);
        });

        assertEquals(Strings.AGENCIA.CONFLICT, exception.getMessage());

        // Verificando se o método save não foi chamado
        verify(agenciaRepository).findById(agenciaId);
        verify(agenciaRepository).findByNumero(numeroExistente);
        verify(agenciaRepository, never()).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer um erro inesperado ao atualizar a agência")
    void testUpdateFailure() {
        Long agenciaId = 1L;

        // Simula a agência existente
        Agencia agenciaExistente = new Agencia();
        agenciaExistente.setId(agenciaId);
        agenciaExistente.setNumero(12345L);

        Agencia agenciaAtualizada = new Agencia();
        agenciaAtualizada.setNumero(54321L);

        when(agenciaRepository.findById(agenciaId)).thenReturn(Optional.of(agenciaExistente));
        when(agenciaRepository.findByNumero(54321L)).thenReturn(Optional.empty());
        when(agenciaRepository.save(any(Agencia.class))).thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        // Verificando se a exceção RepositoryException é lançada
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            agenciaService.update(agenciaId, agenciaAtualizada);
        });

        assertEquals(Strings.AGENCIA.ERROR_UPDATE, exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar uma agência com sucesso")
    void testDeleteSuccess() {
        Long agenciaId = 1L;

        // Simula a existência da agência
        when(agenciaRepository.existsById(agenciaId)).thenReturn(true);

        // Chamando o método a ser testado
        agenciaService.delete(agenciaId);

        // Verificando se o método deleteById foi chamado
        verify(agenciaRepository).deleteById(agenciaId);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException se a agência não for encontrada")
    void testDeleteNotFound() {
        Long agenciaId = 1L;

        // Simula a inexistência da agência
        when(agenciaRepository.existsById(agenciaId)).thenReturn(false);

        // Verificando se a exceção NotFoundException é lançada
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            agenciaService.delete(agenciaId);
        });

        assertEquals(Strings.AGENCIA.NOT_FOUND, exception.getMessage());

        // Verificando se o método deleteById não foi chamado
        verify(agenciaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer um erro inesperado ao deletar a agência")
    void testDeleteFailure() {
        Long agenciaId = 1L;

        // Simula a existência da agência
        when(agenciaRepository.existsById(agenciaId)).thenReturn(true);

        // Simula uma falha ao deletar a agência
        doThrow(new RuntimeException("Erro ao acessar o banco de dados")).when(agenciaRepository).deleteById(agenciaId);

        // Verificando se a exceção RepositoryException é lançada
        RepositoryException exception = assertThrows(RepositoryException.class, () -> {
            agenciaService.delete(agenciaId);
        });

        assertEquals(Strings.AGENCIA.ERROR_DELETE, exception.getMessage());
    }
}
