package br.acc.bank.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;

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
import br.acc.bank.model.Cliente;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.EnderecoRepository;
import br.acc.bank.util.Strings;

public class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    private Cliente cliente;
    private Optional<Cliente> clienteOptional;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("John Doe");
        cliente.setCpf("12345678901");
        cliente.setEmail("john.doe@example.com");
        clienteOptional = Optional.of(cliente);
    }

    @Test
    @DisplayName("Deve retornar a lista de todos os clientes")
    public void testGetAll() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> clientes = clienteService.getAll();
        assertFalse(clientes.isEmpty());
        assertEquals(1, clientes.size());
        assertEquals(cliente, clientes.get(0));
    }

    @Test
    @DisplayName("Deve retornar o cliente para um ID válido")
    public void testGetById() {
        when(clienteRepository.findById(1L)).thenReturn(clienteOptional);

        Optional<Cliente> clienteReturned = clienteService.getById(1L);
        assertTrue(clienteReturned.isPresent());
        assertEquals(cliente, clienteReturned.get());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException para um ID inválido")
    public void testGetByIdNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            clienteService.getById(1L);
        });
        assertEquals(Strings.CLIENTE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar e retornar um novo cliente")
    public void testCreate() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente createdCliente = clienteService.create(cliente);
        assertEquals(cliente, createdCliente);
    }

    @Test
    @DisplayName("Deve lançar ConflictException se email já estiver em uso durante a criação")
    public void testCreateEmailConflict() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(clienteOptional);
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ConflictException.class, () -> {
            clienteService.create(cliente);
        });
        assertEquals(Strings.CLIENTE.CONFLICT, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflictException se CPF já estiver em uso durante a criação")
    public void testCreateCpfConflict() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(clienteOptional);

        Exception exception = assertThrows(ConflictException.class, () -> {
            clienteService.create(cliente);
        });
        assertEquals(Strings.CLIENTE.CONFLICT, exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar e retornar um cliente existente")
    public void testUpdate() {
        when(clienteRepository.findById(1L)).thenReturn(clienteOptional);
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente updatedCliente = clienteService.update(1L, cliente);
        assertEquals(cliente, updatedCliente);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException para cliente não existente")
    public void testUpdateNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            clienteService.update(1L, cliente);
        });
        assertEquals(Strings.CLIENTE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflictException se email já estiver em uso durante a atualização")
    public void testUpdateEmailConflict() {
        Cliente conflictingCliente = new Cliente();
        conflictingCliente.setId(2L);
        conflictingCliente.setEmail("conflict@example.com");
        conflictingCliente.setCpf("10987654321");

        when(clienteRepository.findById(1L)).thenReturn(clienteOptional);
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.of(conflictingCliente));
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ConflictException.class, () -> {
            clienteService.update(1L, cliente);
        });
        assertEquals(Strings.CLIENTE.CONFLICT, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflictException se CPF já estiver em uso durante a atualização")
    public void testUpdateCpfConflict() {
        Cliente conflictingCliente = new Cliente();
        conflictingCliente.setId(2L);
        conflictingCliente.setEmail("conflict@example.com");
        conflictingCliente.setCpf("10987654321");

        when(clienteRepository.findById(1L)).thenReturn(clienteOptional);
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(conflictingCliente));

        Exception exception = assertThrows(ConflictException.class, () -> {
            clienteService.update(1L, cliente);
        });
        assertEquals(Strings.CLIENTE.CONFLICT, exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar um cliente se existir")
    public void testDelete() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.delete(1L);
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException para cliente não existente")
    public void testDeleteNotFound() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            clienteService.delete(1L);
        });
        assertEquals(Strings.CLIENTE.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException em caso de erro no repositório")
    public void testRepositoryExceptionInGetAll() {
        when(clienteRepository.findAll()).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RepositoryException.class, () -> {
            clienteService.getAll();
        });
        assertEquals(Strings.CLIENTE.ERROR_FIND_ALL_LIST, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException em caso de erro no repositório")
    public void testRepositoryExceptionInGetById() {
        when(clienteRepository.findById(1L)).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RepositoryException.class, () -> {
            clienteService.getById(1L);
        });
        assertEquals(Strings.CLIENTE.ERROR_FIND_BY_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException em caso de erro no repositório")
    public void testRepositoryExceptionInCreate() {
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RepositoryException.class, () -> {
            clienteService.create(cliente);
        });
        assertEquals(Strings.CLIENTE.ERROR_CREATE, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException em caso de erro no repositório")
    public void testRepositoryExceptionInUpdate() {
        when(clienteRepository.findById(1L)).thenReturn(clienteOptional);
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RepositoryException.class, () -> {
            clienteService.update(1L, cliente);
        });
        assertEquals(Strings.CLIENTE.ERROR_UPDATE, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RepositoryException em caso de erro no repositório")
    public void testRepositoryExceptionInDelete() {
        when(clienteRepository.existsById(1L)).thenThrow(new DataAccessException("Error") {
        });

        Exception exception = assertThrows(RepositoryException.class, () -> {
            clienteService.delete(1L);
        });
        assertEquals(Strings.CLIENTE.ERROR_DELETE, exception.getMessage());
    }
}
