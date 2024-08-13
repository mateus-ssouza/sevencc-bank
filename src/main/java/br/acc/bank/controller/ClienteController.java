package br.acc.bank.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.cliente.ClienteRequestDTO;
import br.acc.bank.dto.cliente.ClienteResponseDTO;
import br.acc.bank.exception.ErrorResponse;
import br.acc.bank.model.Cliente;
import br.acc.bank.security.TokenService;
import br.acc.bank.service.ClienteService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
@Tag(name = "cliente", description = "rotas para gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private TokenService tokenService;

    @GetMapping
    @Operation(summary = "Lista todos os clientes", description = "Obtém a lista de todos os clientes cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao obter lista de clientes", value = "{\"status\": 500, \"message\": \"Erro ao obter a lista de clientes\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes() {
        try {
            var clientes = clienteService.getAll();
            var clientesDTO = clientes.stream()
                    .map(cliente -> MapperConverter.convertToDto(cliente, ClienteResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(clientesDTO);
        } catch (Exception e) {
            throw e;
        }

    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém um cliente por ID", description = "Obtém os detalhes de um cliente específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado e retornado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao buscar cliente", value = "{\"status\": 500, \"message\": \"Erro ao tentar obter um cliente por seu ID.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponseDTO> getClienteById(
            @Parameter(description = "ID do cliente a ser buscado") @PathVariable Long id) {
        try {
            return clienteService.getById(id)
                    .map(cliente -> ResponseEntity.ok(MapperConverter.convertToDto(cliente, ClienteResponseDTO.class)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Cria um novo cliente", description = "Cria um novo cliente com base nas informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para criação do cliente.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao criar cliente", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar um cliente.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponseDTO> createCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO,
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

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente existente", description = "Atualiza as informações de um cliente específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para atualização do cliente.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao atualizar cliente", value = "{\"status\": 500, \"message\": \"Erro ao tentar atualizar um cliente.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponseDTO> updateCliente(@PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteRequestDTO, BindingResult validateFields) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            var cliente = MapperConverter.convertToEntity(clienteRequestDTO, Cliente.class);
            var updatedCliente = clienteService.update(id, cliente);
            var clienteResponseDTO = MapperConverter.convertToDto(updatedCliente, ClienteResponseDTO.class);

            return ResponseEntity.ok(clienteResponseDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um cliente", description = "Remove um cliente específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao remover cliente", value = "{\"status\": 500, \"message\": \"Erro ao tentar deletar um cliente.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/meu-perfil")
    @Operation(summary = "Visualiza os dados do perfil do cliente", description = "Obtém os detalhes do perfil associado ao login do cliente extraído do token JWT.")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Detalhes do perfil do cliente retornados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
                    }, schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                                    @ExampleObject(name = "Erro ao buscar dados do perfil", value = "{\"status\": 500, \"message\": \"Erro ao tentar obter detalhes do meu perfil.\", \"cause\": \"Detalhes do erro interno\"}")
                    }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponseDTO> meuPerfil(@Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);

            return clienteService.meuPerfil(userInfoToken)
                    .map(cliente -> ResponseEntity.ok(MapperConverter.convertToDto(cliente,
                            ClienteResponseDTO.class)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw e;
        }
    }
}
