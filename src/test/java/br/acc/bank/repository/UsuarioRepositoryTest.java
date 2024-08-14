package br.acc.bank.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.acc.bank.model.Usuario;
import br.acc.bank.model.enums.UsuarioRole;
import org.springframework.security.core.userdetails.UserDetails;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve encontrar um usuário pelo login com sucesso")
    void testFindByLoginSuccess() {
        Usuario usuario = createUsuarioValid();
        usuarioRepository.save(usuario);

        UserDetails result = usuarioRepository.findByLogin("joao");

        assertNotNull(result);
        assertEquals(usuario.getLogin(), result.getUsername());
    }

    @Test
    @DisplayName("Não deve encontrar um usuário pelo login se o login não existir")
    void testFindByLoginNotFound() {
        UserDetails result = usuarioRepository.findByLogin("login.inexistente");

        assertNull(result);
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo email com sucesso")
    void testFindByEmailSuccess() {
        Usuario usuario = createUsuarioValid();
        usuarioRepository.save(usuario);

        Optional<Usuario> result = usuarioRepository.findByEmail("joao.silva@example.com");

        assertTrue(result.isPresent());
        assertEquals(usuario.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("Não deve encontrar um usuário pelo email se o email não existir")
    void testFindByEmailNotFound() {
        Optional<Usuario> result = usuarioRepository.findByEmail("email.inexistente@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo CPF com sucesso")
    void testFindByCpfSuccess() {
        Usuario usuario = createUsuarioValid();
        usuarioRepository.save(usuario);

        Optional<Usuario> result = usuarioRepository.findByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals(usuario.getCpf(), result.get().getCpf());
    }

    @Test
    @DisplayName("Não deve encontrar um usuário pelo CPF se o CPF não existir")
    void testFindByCpfNotFound() {
        Optional<Usuario> result = usuarioRepository.findByCpf("00000000000");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar o ID do usuário pelo login com sucesso")
    void testFindIdByLoginSuccess() {
        Usuario usuario = createUsuarioValid();
        usuarioRepository.save(usuario);

        Optional<Long> result = usuarioRepository.findIdByLogin("joao");

        assertTrue(result.isPresent());
        assertEquals(usuario.getId(), result.get());
    }

    @Test
    @DisplayName("Não deve encontrar o ID do usuário pelo login se o login não existir")
    void testFindIdByLoginNotFound() {
        Optional<Long> result = usuarioRepository.findIdByLogin("login.inexistente");

        assertFalse(result.isPresent());
    }

    private Usuario createUsuarioValid() {
        Usuario usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setCpf("12345678900");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setEmail("joao.silva@example.com");
        usuario.setTelefone("11987654321");
        usuario.setLogin("joao");
        usuario.setPassword("joao123");
        usuario.setRole(UsuarioRole.ADMIN);

        return usuario;
    }
}