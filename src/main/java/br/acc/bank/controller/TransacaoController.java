package br.acc.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.transacao.TransacaoRequestDTO;
import br.acc.bank.dto.transacao.TransacaoResponseDTO;
import br.acc.bank.dto.transacao.TransferenciaRequestDTO;
import br.acc.bank.exception.ErrorResponse;
import br.acc.bank.security.TokenService;
import br.acc.bank.service.TransacaoService;
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
@RequestMapping("/transacao")
@Tag(name = "transacao", description = "rotas para gerenciamento de transações")
public class TransacaoController {
    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/depositar")
    @Operation(summary = "Realizar depósito em uma conta", description = "Retorna os dados da transação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Depósito realizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Valor da transação inválido", value = "{\"status\": 400, \"message\": \"O valor da transação deve ser positivo.\", \"cause\": null}"),
                    @ExampleObject(name = "Saldo insuficiente", value = "{\"status\": 400, \"message\": \"Saldo insuficiente para a transação.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "Token inválido", value = "{\"status\": 401, \"message\": \"Token inválido.\", \"cause\": null}")
            })),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}"),
                    @ExampleObject(name = "Conta origem não encontrada", value = "{\"status\": 404, \"message\": \"Conta origem não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conflito de contas", value = "{\"status\": 409, \"message\": \"Conta origem e destino não podem ser a mesma.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro no repositório", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar uma transação.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransacaoResponseDTO> depositar(@Valid @RequestBody TransacaoRequestDTO transacaoRequestDTO,
            BindingResult validateFields,  @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {

        try {
            ValidationUtils.validateBindingResult(validateFields);

            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);

            var transacao = transacaoService.deposit(transacaoRequestDTO, userInfoToken);
            var transacaoResponseDTO = MapperConverter.convertToDto(transacao, TransacaoResponseDTO.class);

            return new ResponseEntity<>(transacaoResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/sacar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saque realizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Valor da transação inválido", value = "{\"status\": 400, \"message\": \"O valor da transação deve ser positivo.\", \"cause\": null}"),
                    @ExampleObject(name = "Saldo insuficiente", value = "{\"status\": 400, \"message\": \"Saldo insuficiente para a transação.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "Token inválido", value = "{\"status\": 401, \"message\": \"Token inválido.\", \"cause\": null}")
            })),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}"),
                    @ExampleObject(name = "Conta origem não encontrada", value = "{\"status\": 404, \"message\": \"Conta origem não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conflito de contas", value = "{\"status\": 409, \"message\": \"Conta origem e destino não podem ser a mesma.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro no repositório", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar uma transação.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "Realizar saque em uma conta", description = "Retorna os dados da transação.")
    public ResponseEntity<TransacaoResponseDTO> sacar(@Valid @RequestBody TransacaoRequestDTO transacaoRequestDTO,
            BindingResult validateFields,  @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {

        try {
            ValidationUtils.validateBindingResult(validateFields);

            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);
            
            var transacao = transacaoService.withdraw(transacaoRequestDTO, userInfoToken);
            var transacaoResponseDTO = MapperConverter.convertToDto(transacao, TransacaoResponseDTO.class);

            return new ResponseEntity<>(transacaoResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/transferir")
    @Operation(summary = "Realiza uma transferência entre contas", description = "Efetua a transferência de um valor entre a conta de origem e a conta de destino.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Valor da transação inválido", value = "{\"status\": 400, \"message\": \"O valor da transação deve ser positivo.\", \"cause\": null}"),
                    @ExampleObject(name = "Saldo insuficiente", value = "{\"status\": 400, \"message\": \"Saldo insuficiente para a transação.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "Token inválido", value = "{\"status\": 401, \"message\": \"Token inválido.\", \"cause\": null}")
            })),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Cliente não encontrado", value = "{\"status\": 404, \"message\": \"Cliente não encontrado.\", \"cause\": null}"),
                    @ExampleObject(name = "Conta origem não encontrada", value = "{\"status\": 404, \"message\": \"Conta origem não encontrada.\", \"cause\": null}"),
                    @ExampleObject(name = "Conta destino não encontrada", value = "{\"status\": 404, \"message\": \"Conta destino não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Conflito de contas", value = "{\"status\": 409, \"message\": \"Conta origem e destino não podem ser a mesma.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro no repositório", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar uma transação.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransacaoResponseDTO> transferir(@Valid @RequestBody TransferenciaRequestDTO transferenciaRequestDTO,
            BindingResult validateFields, @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader) {

        try {
            ValidationUtils.validateBindingResult(validateFields);

            // Extrai o token JWT do cabeçalho Authorization
            String token = tokenService.extractTokenFromHeader(authorizationHeader);

            // Obtém o login do usuário a partir do token JWT
            String userInfoToken = tokenService.getUserLoginFromToken(token);

            var transacao = transacaoService.transfer(transferenciaRequestDTO, userInfoToken);
            var transacaoResponseDTO = MapperConverter.convertToDto(transacao, TransacaoResponseDTO.class);

            return new ResponseEntity<>(transacaoResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }
}