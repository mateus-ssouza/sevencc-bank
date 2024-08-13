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
import br.acc.bank.exception.ErrorResponse;
import br.acc.bank.model.Cliente;
import br.acc.bank.model.Usuario;
import br.acc.bank.security.TokenService;
import br.acc.bank.service.ClienteService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.Strings;
import br.acc.bank.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("auth")
@Tag(name = "auth", description = "rotas autenticação e registro")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "Realiza o login de um usuário", description = "Autentica o usuário e retorna um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Credenciais inválidas", value = "{\"status\": 401, \"message\": \"login ou password inválidos.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao gerar token", value = "{\"status\": 500, \"message\": \"Erro ao gerar token.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "Realiza o registro de um novo cliente", description = "Cria um novo cliente e retorna os dados do cliente criado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente registrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Requisição inválida", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conflito de usuário", value = "{\"status\": 409, \"message\": \"Cpf, e-mail ou login do usuário já utilizado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao criar cliente", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar um cliente.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
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