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
import br.acc.bank.security.TokenService;
import br.acc.bank.service.TransacaoService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {
    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponseDTO> depositar(@Valid @RequestBody TransacaoRequestDTO transacaoRequestDTO,
            BindingResult validateFields, @RequestHeader(value = "Authorization") String authorizationHeader) {

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
    public ResponseEntity<TransacaoResponseDTO> sacar(@Valid @RequestBody TransacaoRequestDTO transacaoRequestDTO,
            BindingResult validateFields, @RequestHeader(value = "Authorization") String authorizationHeader) {

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
    public ResponseEntity<TransacaoResponseDTO> transferir(@Valid @RequestBody TransferenciaRequestDTO transferenciaRequestDTO,
            BindingResult validateFields, @RequestHeader(value = "Authorization") String authorizationHeader) {

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