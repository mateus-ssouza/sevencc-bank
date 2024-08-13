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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.agencia.AgenciaRequestDTO;
import br.acc.bank.dto.agencia.AgenciaResponseDTO;
import br.acc.bank.exception.ErrorResponse;
import br.acc.bank.model.Agencia;
import br.acc.bank.service.AgenciaService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agencia")
@Tag(name = "agencia", description = "rotas para gerenciamento de agencias")
public class AgenciaController {
    @Autowired
    private AgenciaService agenciaService;

    @GetMapping
    @Operation(summary = "Lista todas as agências", description = "Obtém a lista de todas as agências cadastradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agências retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenciaResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao obter lista de agências", value = "{\"status\": 500, \"message\": \"Erro ao obter a lista de agências\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AgenciaResponseDTO>> getAllAgencias() {
        try {
            var agencias = agenciaService.getAll();
            var agenciasDTO = agencias.stream()
                    .map(agencia -> MapperConverter.convertToDto(agencia, AgenciaResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(agenciasDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém uma agência por ID", description = "Obtém os detalhes de uma agência específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agência encontrada e retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenciaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Agência não encontrada", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Agência não encontrada", value = "{\"status\": 404, \"message\": \"Agência não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao buscar agência", value = "{\"status\": 500, \"message\": \"Erro ao tentar obter uma agência por seu ID.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AgenciaResponseDTO> getAgenciaById(@PathVariable Long id) {
        try {
            return agenciaService.getById(id)
                    .map(agencia -> ResponseEntity.ok(MapperConverter.convertToDto(agencia, AgenciaResponseDTO.class)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Cria uma nova agência", description = "Cria uma nova agência com base nas informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agência criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenciaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para criação da agência.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito, agência já existe.", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Agência já existente", value = "{\"status\": 409, \"message\": \"Número da agência já utilizado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao criar agência", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar uma agência.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AgenciaResponseDTO> createAgencia(@Valid @RequestBody AgenciaRequestDTO agenciaRequestDTO,
            BindingResult validateFields) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            var agencia = MapperConverter.convertToEntity(agenciaRequestDTO, Agencia.class);
            var savedAgencia = agenciaService.create(agencia);
            var agenciaResponseDTO = MapperConverter.convertToDto(savedAgencia, AgenciaResponseDTO.class);
            return new ResponseEntity<>(agenciaResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma agência existente", description = "Atualiza as informações de uma agência específica com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agência atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenciaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para atualização da agência.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agência não encontrada para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Agência não encontrada", value = "{\"status\": 404, \"message\": \"Agência não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao atualizar agência", value = "{\"status\": 500, \"message\": \"Erro ao tentar atualizar uma agência.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AgenciaResponseDTO> updateAgencia(@PathVariable Long id,
            @Valid @RequestBody AgenciaRequestDTO agenciaRequestDTO, BindingResult validateFields) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            var updatedAgencia = MapperConverter.convertToEntity(agenciaRequestDTO, Agencia.class);
            return ResponseEntity.ok(
                    MapperConverter.convertToDto(agenciaService.update(id, updatedAgencia), AgenciaResponseDTO.class));
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma agência", description = "Remove uma agência específica com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agência removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agência não encontrada para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Agência não encontrada", value = "{\"status\": 404, \"message\": \"Agência não encontrada.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito, agência possui vinculo com conta(s).", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Agência vinculada com conta(s)", value = "{\"status\": 409, \"message\": \"A agência está vinculada com uma ou mais contas.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao remover agência", value = "{\"status\": 500, \"message\": \"Erro ao tentar deletar uma agência.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteAgencia(@PathVariable Long id) {
        try {
            agenciaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }
}
