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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.conta.ContaRequestDTO;
import br.acc.bank.dto.conta.ContaResponseDTO;
import br.acc.bank.dto.conta.TransacoesContaResponseDTO;
import br.acc.bank.exception.ErrorResponse;
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.security.TokenService;
import br.acc.bank.service.ContaService;
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
@RequestMapping("/conta")
@Tag(name = "conta", description = "rotas para gerenciamento de contas")
public class ContaController {
    @Autowired
    private ContaService contaService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    @Operation(summary = "Lista todas as contas", description = "Obtém a lista de todas as contas cadastradas. Pode filtrar pelo tipo de conta (CORRENTE ou POUPANCA).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContaResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao obter lista de contas", value = "{\"status\": 500, \"message\": \"Erro ao obter a lista de contas\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ContaResponseDTO>> getAllContas(
            @RequestParam(required = false) TipoConta tipo) {
        try {
            var contas = contaService.getAll(tipo);
            var contasDTO = contas.stream()
                    .map(conta -> MapperConverter.convertToDto(conta, ContaResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(contasDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém uma conta por ID", description = "Obtém os detalhes de uma conta específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada e retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conta não encontrada", value = "{\"status\": 404, \"message\": \"Conta não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao buscar conta", value = "{\"status\": 500, \"message\": \"Erro ao tentar obter uma conta pelo ID.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContaResponseDTO> getContaById(@PathVariable Long id) {
        try {
            return contaService.getById(id)
                    .map(conta -> ResponseEntity.ok(MapperConverter.convertToDto(conta,
                            ContaResponseDTO.class)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Cria uma nova conta", description = "Cria uma nova conta com base nas informações fornecidas e no login do usuário extraído do token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para criação da conta.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agência ou cliente não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Agência não encontrada", value = "{\"status\": 404, \"message\": \"Agência não encontrada.\", \"cause\": null}"),
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito, conta já existente para o cliente informado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conta já existente", value = "{\"status\": 409, \"message\": \"Já existe uma conta para o cliente informado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao criar conta", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar uma conta.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContaResponseDTO> createConta(@Valid @RequestBody ContaRequestDTO contaRequestDTO,
            BindingResult validateFields, @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);

            var savedConta = contaService.create(contaRequestDTO, userInfoToken);
            var contaResponseDTO = MapperConverter.convertToDto(savedConta, ContaResponseDTO.class);

            return new ResponseEntity<>(contaResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma conta", description = "Remove uma conta específica com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conta não encontrada", value = "{\"status\": 404, \"message\": \"Conta não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito, conta não está com saldo zerado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conta com saldo diferente de zero", value = "{\"status\": 409, \"message\": \"Conta deve está com saldo zerado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao remover conta", value = "{\"status\": 500, \"message\": \"Erro ao tentar deletar uma conta.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        try {
            contaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/extrato")
    @Operation(summary = "Gera o extrato da conta", description = "Gera o extrato com todas as transações da conta associada ao login do usuário extraído do token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extrato de transações retornado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacoesContaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta ou cliente não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conta não encontrada", value = "{\"status\": 404, \"message\": \"Conta não encontrada.\", \"cause\": null}"),
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao gerar extrato", value = "{\"status\": 500, \"message\": \"Erro ao tentar gerar o extrato da conta.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TransacoesContaResponseDTO>> getExtrato(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);

            var extratoTransacaoes = contaService.getExtrato(userInfoToken);

            var extrato = extratoTransacaoes.stream()
                    .map(transacao -> MapperConverter.convertToDto(
                            transacao,
                            TransacoesContaResponseDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(extrato);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/minha-conta")
    @Operation(summary = "Visualiza os dados da conta do usuário", description = "Obtém os detalhes da conta associada ao login do usuário extraído do token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados da conta retornados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta ou cliente não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conta não encontrada", value = "{\"status\": 404, \"message\": \"Conta não encontrada.\", \"cause\": null}"),
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao buscar dados da conta", value = "{\"status\": 500, \"message\": \"Erro ao tentar obter detalhes da conta.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContaResponseDTO> minhaConta(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);

            return contaService.detailsConta(userInfoToken)
                    .map(conta -> ResponseEntity.ok(MapperConverter.convertToDto(conta,
                            ContaResponseDTO.class)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw e;
        }
    }
}
