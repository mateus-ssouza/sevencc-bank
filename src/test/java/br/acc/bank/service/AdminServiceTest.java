package br.acc.bank.service;

import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Admin;
import br.acc.bank.model.Usuario;
import br.acc.bank.repository.AdminRepository;
import br.acc.bank.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdminRepository adminRepository;

    private Admin admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        admin = new Admin();
        admin.setId(1L);
        admin.setNome("Admin Teste");
        admin.setEmail("admin@teste.com");
        admin.setCpf("12345678900");
        admin.setLogin("admin_login");
        admin.setPassword("senha123");
    }

    @Test
    @DisplayName("Deve listar todos os admins com sucesso")
    void testGetAll_Success() {
        when(adminRepository.findAll()).thenReturn(List.of(admin));

        var admins = adminService.getAll();

        assertFalse(admins.isEmpty());
        verify(adminRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao tentar listar todos os admins")
    void testGetAll_RepositoryException() {
        when(adminRepository.findAll()).thenThrow(new RuntimeException("Erro inesperado"));

        assertThrows(RepositoryException.class, () -> adminService.getAll());

        verify(adminRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar admin pelo ID com sucesso")
    void testGetById_Success() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        Optional<Admin> result = adminService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(admin.getNome(), result.get().getNome());
        verify(adminRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar admin por ID inexistente")
    void testGetById_NotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminService.getById(1L));
        verify(adminRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao tentar buscar um admin por ID")
    void testGetById_RepositoryException() {
        when(adminRepository.findById(1L)).thenThrow(new RuntimeException("Erro inesperado"));

        assertThrows(RepositoryException.class, () -> adminService.getById(1L));

        verify(adminRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar um admin com sucesso")
    void testCreate_Success() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(any())).thenReturn(null);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Admin result = adminService.create(admin);

        assertNotNull(result);
        assertEquals(admin.getEmail(), result.getEmail());
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar criar admin com email duplicado")
    void testCreate_EmailConflict() {
        when(usuarioRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(new Usuario()));

        assertThrows(ConflictException.class, () -> adminService.create(admin));
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao tentar criar um admin")
    void testCreate_RepositoryException() {
        when(usuarioRepository.findByEmail(admin.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf(admin.getCpf())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(admin.getLogin())).thenReturn(null);
        when(adminRepository.save(admin)).thenThrow(new RuntimeException("Erro inesperado"));

        assertThrows(RepositoryException.class, () -> adminService.create(admin));

        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    @DisplayName("Deve atualizar um admin com sucesso")
    void testUpdate_Success() {
        Admin updatedAdmin = new Admin();
        updatedAdmin.setId(1L);
        updatedAdmin.setNome("Novo Nome");
        updatedAdmin.setCpf("98765432100");
        updatedAdmin.setTelefone("11999999999");
        updatedAdmin.setDataNascimento(LocalDate.of(1985, 5, 10));
        updatedAdmin.setEmail("novoemail@teste.com");
        updatedAdmin.setLogin("novologin");
        updatedAdmin.setPassword("novasenha123");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(any())).thenReturn(null);
        when(usuarioRepository.findIdByLogin(any())).thenReturn(Optional.of(1L));
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);

        Admin result = adminService.update(1L, updatedAdmin);

        assertNotNull(result);
        assertEquals("novoemail@teste.com", result.getEmail());
        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar admin inexistente")
    void testUpdate_NotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminService.update(1L, admin));
        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar atualizar admin com email duplicado")
    void testUpdate_ConflictEmail() {
        Admin conflictingAdmin = new Admin();
        conflictingAdmin.setId(2L);
        conflictingAdmin.setEmail("admin@teste.com");

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(conflictingAdmin));

        assertThrows(ConflictException.class, () -> adminService.update(1L, admin));

        verify(usuarioRepository, times(1)).findByEmail(admin.getEmail());
        verify(adminRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao tentar atualizar um admin")
    void testUpdate_RepositoryException() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.findByEmail(admin.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCpf(admin.getCpf())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(admin.getLogin())).thenReturn(null);
        when(adminRepository.save(admin)).thenThrow(new RuntimeException("Erro inesperado"));

        assertThrows(RepositoryException.class, () -> adminService.update(1L, admin));

        verify(adminRepository, times(1)).findById(1L);
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    @DisplayName("Deve remover admin com sucesso")
    void testDelete_Success() {
        when(adminRepository.existsById(1L)).thenReturn(true);

        adminService.delete(1L);

        verify(adminRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar remover admin inexistente")
    void testDelete_NotFound() {
        when(adminRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> adminService.delete(1L));
        verify(adminRepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar RepositoryException ao tentar deletar um admin")
    void testDelete_RepositoryException() {
        when(adminRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Erro inesperado")).when(adminRepository).deleteById(1L);

        assertThrows(RepositoryException.class, () -> adminService.delete(1L));

        verify(adminRepository, times(1)).existsById(1L);
        verify(adminRepository, times(1)).deleteById(1L);
    }

}
