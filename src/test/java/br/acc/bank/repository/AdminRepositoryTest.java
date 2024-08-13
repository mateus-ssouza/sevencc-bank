package br.acc.bank.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.acc.bank.model.Admin;
import br.acc.bank.model.enums.UsuarioRole;
import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class AdminRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AdminRepository adminRepository;

    @Test
    @DisplayName("Deve obter um admin buscado por email com sucesso")
    void testFindByEmailSuccess() {
        Admin admin = createAdminValid();

        this.createAdmin(admin);

        Optional<Admin> result = this.adminRepository.findByEmail("joao.silva@example.com");

        assertTrue(result.isPresent());
        assertEquals(admin.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("Não deve obter um admin buscado por email com sucesso")
    void testFindByEmailNotFound() {
        Optional<Admin> result = this.adminRepository.findByEmail("email.inexistente@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve obter um admin buscado por CPF com sucesso")
    void testFindByCpfSuccess() {
        Admin admin = createAdminValid();

        this.createAdmin(admin);

        Optional<Admin> result = this.adminRepository.findByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals(admin.getCpf(), result.get().getCpf());
    }

    @Test
    @DisplayName("Não deve obter um admin buscado por CPF com sucesso")
    void testFindByCpfNotFound() {
        Optional<Admin> result = this.adminRepository.findByCpf("00000000000");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve obter um admin buscado por login com sucesso")
    void testFindByLoginSuccess() {
        Admin admin = createAdminValid();

        this.createAdmin(admin);

        Optional<Admin> result = this.adminRepository.findByLogin("joao");

        assertTrue(result.isPresent());
        assertEquals(admin.getCpf(), result.get().getCpf());
    }

    @Test
    @DisplayName("Não deve obter um admin buscado por login com sucesso")
    void testFindByLoginNotFound() {
        Optional<Admin> result = this.adminRepository.findByLogin("test");

        assertFalse(result.isPresent());
    }

    private Admin createAdmin(Admin admin) {
        this.entityManager.persist(admin);
        return admin;
    }

    private Admin createAdminValid() {
        Admin admin = new Admin();
        admin.setNome("João Silva");
        admin.setCpf("12345678900");
        admin.setDataNascimento(LocalDate.of(1990, 1, 1));
        admin.setEmail("joao.silva@example.com");
        admin.setTelefone("11987654321");
        admin.setLogin("joao");
        admin.setPassword("joao123");
        admin.setRole(UsuarioRole.ADMIN);

        return admin;
    }
}
