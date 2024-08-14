package br.acc.bank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Usuario;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.ContaRepository;
import br.acc.bank.repository.UsuarioRepository;

class ClienteServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
        cliente.setEmail("cliente@teste.com");
        cliente.setCpf("12345678901");
        cliente.setTelefone("123456789");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        cliente.setLogin("cliente_login");
        cliente.setPassword("senha123");
    }

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void testGetAll() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));

        List<Cliente> clientes = clienteService.getAll();

        assertFalse(clientes.isEmpty());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar um cliente pelo ID com sucesso")
    void testGetById_Success() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar cliente por ID inexistente")
    void testGetById_NotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clienteService.getById(1L);
        });

        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void testCreate_Success() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(any())).thenReturn(null);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente createdCliente = clienteService.create(cliente);

        assertNotNull(createdCliente);
        verify(usuarioRepository, times(1)).findByEmail(any());
        verify(usuarioRepository, times(1)).findByCpf(any());
        verify(usuarioRepository, times(1)).findByLogin(any());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar criar cliente com email, CPF ou login já existentes")
    void testCreate_Conflict() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(new Usuario()));

        assertThrows(ConflictException.class, () -> {
            clienteService.create(cliente);
        });

        verify(usuarioRepository, times(1)).findByEmail(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar os dados de um cliente com sucesso")
    void testUpdate_Success() {
        // Cliente atualizado com todos os campos
        Cliente updatedCliente = new Cliente();
        updatedCliente.setId(1L); // Mesmo ID do cliente original
        updatedCliente.setNome("Novo Nome");
        updatedCliente.setEmail("novoemail@teste.com");
        updatedCliente.setCpf("98765432100");
        updatedCliente.setTelefone("987654321");
        updatedCliente.setDataNascimento(LocalDate.of(1992, 4, 4));
        updatedCliente.setLogin("novo_login");
        updatedCliente.setPassword("novaSenha123");

        // Mock do comportamento dos repositórios
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(any())).thenReturn(null);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(updatedCliente);

        // Chamada ao serviço
        Cliente result = clienteService.update(1L, updatedCliente);

        // Asserções
        assertNotNull(result);
        assertEquals("novoemail@teste.com", result.getEmail());
        assertEquals("Novo Nome", result.getNome());
        assertEquals("98765432100", result.getCpf());
        assertEquals("987654321", result.getTelefone());
        assertEquals(LocalDate.of(1992, 4, 4), result.getDataNascimento());
        assertEquals("novo_login", result.getLogin());

        // Verificar se o repositório foi chamado corretamente
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar um cliente inexistente")
    void testUpdate_NotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clienteService.update(1L, cliente);
        });

        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover um cliente com sucesso quando não houver contas vinculadas")
    void testDelete_Success() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        when(contaRepository.existsByClienteId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> {
            clienteService.delete(1L);
        });

        verify(clienteRepository, times(1)).existsById(1L);
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar remover um cliente inexistente")
    void testDelete_NotFound() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            clienteService.delete(1L);
        });

        verify(clienteRepository, times(1)).existsById(1L);
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar remover um cliente com contas vinculadas")
    void testDelete_Conflict() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        when(contaRepository.existsByClienteId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            clienteService.delete(1L);
        });

        verify(clienteRepository, times(1)).existsById(1L);
        verify(contaRepository, times(1)).existsByClienteId(1L);
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar os dados do perfil do cliente pelo login do token")
    void testMeuPerfil_Success() {
        when(clienteRepository.findByLogin(any())).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.meuPerfil("cliente_login");

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());
        verify(clienteRepository, times(1)).findByLogin(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar buscar o perfil de um cliente inexistente pelo login")
    void testMeuPerfil_NotFound() {
        when(clienteRepository.findByLogin(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clienteService.meuPerfil("cliente_login");
        });

        verify(clienteRepository, times(1)).findByLogin(any());
    }

    @Test
@DisplayName("Deve lançar RepositoryException ao tentar listar todos os clientes")
void testGetAll_RepositoryException() {
    when(clienteRepository.findAll()).thenThrow(new RuntimeException("Erro inesperado"));

    assertThrows(RepositoryException.class, () -> clienteService.getAll());

    verify(clienteRepository, times(1)).findAll();
}

@Test
@DisplayName("Deve lançar RepositoryException ao tentar buscar cliente por ID")
void testGetById_RepositoryException() {
    when(clienteRepository.findById(1L)).thenThrow(new RuntimeException("Erro inesperado"));

    assertThrows(RepositoryException.class, () -> clienteService.getById(1L));

    verify(clienteRepository, times(1)).findById(1L);
}

@Test
@DisplayName("Deve lançar RepositoryException ao tentar criar um cliente")
void testCreate_RepositoryException() {
    when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
    when(usuarioRepository.findByLogin(any())).thenReturn(null);
    when(clienteRepository.save(any(Cliente.class))).thenThrow(new RuntimeException("Erro inesperado"));

    assertThrows(RepositoryException.class, () -> clienteService.create(cliente));

    verify(clienteRepository, times(1)).save(any(Cliente.class));
}

@Test
@DisplayName("Deve lançar RepositoryException ao tentar atualizar um cliente")
void testUpdate_RepositoryException() {
    Cliente updatedCliente = new Cliente();
    updatedCliente.setId(1L);
    updatedCliente.setNome("Novo Nome");
    updatedCliente.setEmail("novoemail@teste.com");
    updatedCliente.setCpf("98765432100");
    updatedCliente.setTelefone("987654321");
    updatedCliente.setDataNascimento(LocalDate.of(1992, 4, 4));
    updatedCliente.setLogin("novo_login");
    updatedCliente.setPassword("novaSenha123");

    when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
    when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
    when(usuarioRepository.findByLogin(any())).thenReturn(null);
    when(clienteRepository.save(any(Cliente.class))).thenThrow(new RuntimeException("Erro inesperado"));

    assertThrows(RepositoryException.class, () -> clienteService.update(1L, updatedCliente));

    verify(clienteRepository, times(1)).findById(1L);
    verify(clienteRepository, times(1)).save(any(Cliente.class));
}

@Test
@DisplayName("Deve lançar RepositoryException ao tentar remover um cliente")
void testDelete_RepositoryException() {
    when(clienteRepository.existsById(1L)).thenReturn(true);
    when(contaRepository.existsByClienteId(1L)).thenReturn(false);
    doThrow(new RuntimeException("Erro inesperado")).when(clienteRepository).deleteById(1L);

    assertThrows(RepositoryException.class, () -> clienteService.delete(1L));

    verify(clienteRepository, times(1)).existsById(1L);
    verify(clienteRepository, times(1)).deleteById(1L);
}

@Test
@DisplayName("Deve lançar RepositoryException ao tentar buscar o perfil de um cliente pelo login")
void testMeuPerfil_RepositoryException() {
    when(clienteRepository.findByLogin(any())).thenThrow(new RuntimeException("Erro inesperado"));

    assertThrows(RepositoryException.class, () -> clienteService.meuPerfil("cliente_login"));

    verify(clienteRepository, times(1)).findByLogin(any());
}

}
