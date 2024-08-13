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

import br.acc.bank.dto.admin.AdminRequestDTO;
import br.acc.bank.dto.admin.AdminResponseDTO;
import br.acc.bank.exception.ErrorResponse;
import br.acc.bank.model.Admin;
import br.acc.bank.service.AdminService;
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
@RequestMapping("/admin")
@Tag(name = "admin", description = "rotas para gerenciamento de administradores")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    @Operation(summary = "Lista todos os administradores", description = "Obtém a lista de todos os administradores cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de administradores retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao obter lista de administradores", value = "{\"status\": 500, \"message\": \"Erro ao obter a lista de administradores\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        try {
            var admins = adminService.getAll();
            var adminsDTO = admins.stream()
                    .map(admin -> MapperConverter.convertToDto(admin, AdminResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(adminsDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém um administrador por ID", description = "Obtém os detalhes de um administrador específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador encontrado e retornado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Administrador não encontrado", value = "{\"status\": 404, \"message\": \"Administrador não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao buscar administrador", value = "{\"status\": 500, \"message\": \"Erro ao tentar obter um administrador por seu ID.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        try {
            return adminService.getById(id)
                    .map(admin -> ResponseEntity.ok(MapperConverter.convertToDto(admin, AdminResponseDTO.class)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Cria um novo administrador", description = "Cria um novo administrador com base nas informações fornecidas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para criação do administrador.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao criar administrador", value = "{\"status\": 500, \"message\": \"Erro ao tentar criar um administrador.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminRequestDTO adminRequestDTO,
            BindingResult validateFields) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            var admin = MapperConverter.convertToEntity(adminRequestDTO, Admin.class);
            var savedAdmin = adminService.create(admin);
            var adminResponseDTO = MapperConverter.convertToDto(savedAdmin, AdminResponseDTO.class);

            return new ResponseEntity<>(adminResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
           throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um administrador existente", description = "Atualiza as informações de um administrador específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, geralmente devido a dados de entrada incorretos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Dados inválidos", value = "{\"status\": 400, \"message\": \"Dados inválidos fornecidos para atualização do administrador.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Administrador não encontrado", value = "{\"status\": 404, \"message\": \"Administrador não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao atualizar administrador", value = "{\"status\": 500, \"message\": \"Erro ao tentar atualizar um administrador.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long id,
            @Valid @RequestBody AdminRequestDTO adminRequestDTO, BindingResult validateFields) {

        try {
            ValidationUtils.validateBindingResult(validateFields);
            var admin = MapperConverter.convertToEntity(adminRequestDTO, Admin.class);
            var updatedAdmin = adminService.update(id, admin);
            var adminResponseDTO = MapperConverter.convertToDto(updatedAdmin, AdminResponseDTO.class);

            return ResponseEntity.ok(adminResponseDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um administrador", description = "Remove um administrador específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Administrador removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado para o ID fornecido", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Administrador não encontrado", value = "{\"status\": 404, \"message\": \"Administrador não encontrado.\", \"cause\": null}")
            }, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Erro ao remover administrador", value = "{\"status\": 500, \"message\": \"Erro ao tentar deletar um administrador.\", \"cause\": \"Detalhes do erro interno\"}")
            }, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }
}
