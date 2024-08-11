package br.acc.bank.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.auth.AuthenticationDTO;
import br.acc.bank.dto.auth.LoginResponseDTO;
import br.acc.bank.dto.cliente.ClienteRequestDTO;
import br.acc.bank.dto.cliente.ClienteResponseDTO;
import br.acc.bank.exception.AuthException;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Usuario;
import br.acc.bank.security.TokenService;
import br.acc.bank.service.ClienteService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.Strings;
import br.acc.bank.util.ValidationUtils;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO login,
            BindingResult validateFields) {
        try {
            ValidationUtils.validateBindingResult(validateFields);

            var usernamePassword = new UsernamePasswordAuthenticationToken(login.getLogin(), login.getPassword());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((Usuario) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (AuthenticationException e) {
            throw new AuthException(Strings.AUTH.ERROR_CREDENTIALS);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ClienteResponseDTO> register(@RequestBody @Valid ClienteRequestDTO clienteRequestDTO,
            BindingResult validateFields) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            var cliente = MapperConverter.convertToEntity(clienteRequestDTO, Cliente.class);
            var savedCliente = clienteService.create(cliente);

            var clienteResponseDTO = MapperConverter.convertToDto(savedCliente, ClienteResponseDTO.class);

            return new ResponseEntity<>(clienteResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }
}