package br.acc.bank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Agencia;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.ContaRepository;

class AgenciaServiceTest {

    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private AgenciaService agenciaService;

    private Agencia agencia;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        agencia = new Agencia();
        agencia.setId(1L);
        agencia.setNumero(12345L);
        agencia.setNome("Agencia Teste");
        agencia.setTelefone("123456789");
    }

    @Test
    @DisplayName("Deve listar todas as agências com sucesso")
    void testGetAll() {
        when(agenciaRepository.findAll()).thenReturn(Arrays.asList(agencia));

        List<Agencia> agencias = agenciaService.getAll();

        assertFalse(agencias.isEmpty());
        verify(agenciaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer erro de banco de dados em getAll()")
    void testRepositoryException_GetAll() {
        when(agenciaRepository.findAll()).thenThrow(new DataAccessException("DB Error") {
        });

        assertThrows(RepositoryException.class, () -> {
            agenciaService.getAll();
        });

        verify(agenciaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar uma agência pelo ID com sucesso")
    void testGetById_Success() {
        when(agenciaRepository.findById(1L)).thenReturn(Optional.of(agencia));

        Optional<Agencia> result = agenciaService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(agencia, result.get());
        verify(agenciaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer erro de banco de dados na busca por ID")
    void testRepositoryException_GetById() {
        when(agenciaRepository.findById(1L)).thenThrow(new DataAccessException("DB Error") {
        });

        assertThrows(RepositoryException.class, () -> {
            agenciaService.getById(1L);
        });

        verify(agenciaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar agência por ID inexistente")
    void testGetById_NotFound() {
        when(agenciaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            agenciaService.getById(1L);
        });

        verify(agenciaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar uma agência com sucesso")
    void testCreate_Success() {
        when(agenciaRepository.findByNumero(any())).thenReturn(Optional.empty());
        when(agenciaRepository.save(any(Agencia.class))).thenReturn(agencia);

        Agencia createdAgencia = agenciaService.create(agencia);

        assertNotNull(createdAgencia);
        verify(agenciaRepository, times(1)).findByNumero(any());
        verify(agenciaRepository, times(1)).save(agencia);
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer erro de banco de dados na criação de uma agência")
    void testRepositoryException_Create() {
        when(agenciaRepository.findByNumero(any())).thenReturn(Optional.empty());
        when(agenciaRepository.save(any(Agencia.class))).thenThrow(new DataAccessException("DB Error") {
        });

        assertThrows(RepositoryException.class, () -> {
            agenciaService.create(agencia);
        });

        verify(agenciaRepository, times(1)).findByNumero(any());
        verify(agenciaRepository, times(1)).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar criar agência com número já existente")
    void testCreate_Conflict() {
        when(agenciaRepository.findByNumero(any())).thenReturn(Optional.of(agencia));

        assertThrows(ConflictException.class, () -> {
            agenciaService.create(agencia);
        });

        verify(agenciaRepository, times(1)).findByNumero(any());
        verify(agenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar os dados de uma agência com sucesso")
    void testUpdate_Success() {
        Agencia updatedAgencia = new Agencia();
        updatedAgencia.setNumero(54321L);

        when(agenciaRepository.findById(1L)).thenReturn(Optional.of(agencia));
        when(agenciaRepository.findByNumero(any())).thenReturn(Optional.empty());
        when(agenciaRepository.save(any(Agencia.class))).thenReturn(updatedAgencia);

        Agencia result = agenciaService.update(1L, updatedAgencia);

        assertNotNull(result);
        assertEquals(54321L, result.getNumero());
        verify(agenciaRepository, times(1)).findById(1L);
        verify(agenciaRepository, times(1)).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer erro de banco de dados na atualização de uma agência")
    void testRepositoryException_Update() {
        when(agenciaRepository.findById(1L)).thenThrow(new DataAccessException("DB Error") {
        });

        assertThrows(RepositoryException.class, () -> {
            agenciaService.update(1L, agencia);
        });

        verify(agenciaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar uma agência inexistente")
    void testUpdate_NotFound() {
        when(agenciaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            agenciaService.update(1L, agencia);
        });

        verify(agenciaRepository, times(1)).findById(1L);
        verify(agenciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar atualizar uma agência para um número já existente")
    void testUpdate_Conflict() {
        Agencia updatedAgencia = new Agencia();
        updatedAgencia.setNumero(54321L);

        when(agenciaRepository.findById(1L)).thenReturn(Optional.of(agencia));
        when(agenciaRepository.findByNumero(any())).thenReturn(Optional.of(new Agencia()));

        assertThrows(ConflictException.class, () -> {
            agenciaService.update(1L, updatedAgencia);
        });

        verify(agenciaRepository, times(1)).findById(1L);
        verify(agenciaRepository, times(1)).findByNumero(any());
        verify(agenciaRepository, never()).save(any(Agencia.class));
    }

    @Test
    @DisplayName("Deve remover uma agência com sucesso quando não houver contas vinculadas")
    void testDelete_Success() {
        when(agenciaRepository.existsById(1L)).thenReturn(true);
        when(contaRepository.existsByAgenciaId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> {
            agenciaService.delete(1L);
        });

        verify(agenciaRepository, times(1)).existsById(1L);
        verify(agenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao ocorrer erro de banco de dados na remoção de uma agência")
    void testRepositoryException_Delete() {
        when(agenciaRepository.existsById(1L)).thenReturn(true);
        when(contaRepository.existsByAgenciaId(1L)).thenReturn(false);
        doThrow(new DataAccessException("DB Error") {
        }).when(agenciaRepository).deleteById(1L);

        assertThrows(RepositoryException.class, () -> {
            agenciaService.delete(1L);
        });

        verify(agenciaRepository, times(1)).existsById(1L);
        verify(contaRepository, times(1)).existsByAgenciaId(1L);
        verify(agenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar remover uma agência inexistente")
    void testDelete_NotFound() {
        when(agenciaRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            agenciaService.delete(1L);
        });

        verify(agenciaRepository, times(1)).existsById(1L);
        verify(agenciaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar remover uma agência com contas vinculadas")
    void testDelete_Conflict() {
        when(agenciaRepository.existsById(1L)).thenReturn(true);
        when(contaRepository.existsByAgenciaId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            agenciaService.delete(1L);
        });

        verify(agenciaRepository, times(1)).existsById(1L);
        verify(contaRepository, times(1)).existsByAgenciaId(1L);
        verify(agenciaRepository, never()).deleteById(any());
    }
}
