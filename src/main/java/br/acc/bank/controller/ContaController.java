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
import br.acc.bank.model.enums.TipoConta;
import br.acc.bank.security.TokenService;
import br.acc.bank.service.ContaService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta")
public class ContaController {
    @Autowired
    private ContaService contaService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
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
    public ResponseEntity<ContaResponseDTO> createConta(@Valid @RequestBody ContaRequestDTO contaRequestDTO,
            BindingResult validateFields, @RequestHeader(value = "Authorization") String authorizationHeader) {

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
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        try {
            contaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }
}
