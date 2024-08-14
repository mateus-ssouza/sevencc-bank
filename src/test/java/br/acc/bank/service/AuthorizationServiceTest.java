package br.acc.bank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import br.acc.bank.repository.UsuarioRepository;

class AuthorizationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private UserDetails userDetails;
    private String username = "user@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = mock(UserDetails.class);
    }

    @Test
    @DisplayName("Deve buscar usuário pelo login com sucesso")
    void testLoadUserByUsername_Success() {
        when(usuarioRepository.findByLogin(username)).thenReturn(userDetails);

        UserDetails result = authorizationService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(userDetails, result);
        verify(usuarioRepository, times(1)).findByLogin(username);
    }

}
