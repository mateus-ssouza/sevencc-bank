package br.acc.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.transacao.TransacaoRequestDTO;
import br.acc.bank.dto.transacao.TransacaoResponseDTO;
import br.acc.bank.dto.transacao.TransferenciaRequestDTO;
import br.acc.bank.service.TransacaoService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta/{id}")
public class TransacaoController {
    @Autowired
    private TransacaoService transacaoService;

    @PostMapping("/depositar")
    public ResponseEntity<TransacaoResponseDTO> deposit(@PathVariable Long id, @Valid @RequestBody TransacaoRequestDTO transacaoRequestDTO,
            BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var transacao = transacaoService.deposit(transacaoRequestDTO, id);
        var transacaoResponseDTO = MapperConverter.convertToDto(transacao, TransacaoResponseDTO.class);

        return new ResponseEntity<>(transacaoResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/sacar")
    public ResponseEntity<TransacaoResponseDTO> withdraw(@PathVariable Long id,
            @Valid @RequestBody TransacaoRequestDTO transacaoRequestDTO,
            BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var transacao = transacaoService.withdraw(transacaoRequestDTO, id);
        var transacaoResponseDTO = MapperConverter.convertToDto(transacao, TransacaoResponseDTO.class);

        return new ResponseEntity<>(transacaoResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/transferir")
    public ResponseEntity<TransacaoResponseDTO> transfer(@PathVariable Long id,
            @Valid @RequestBody TransferenciaRequestDTO transferenciaRequestDTO,
            BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var transacao = transacaoService.transfer(transferenciaRequestDTO, id);
        var transacaoResponseDTO = MapperConverter.convertToDto(transacao, TransacaoResponseDTO.class);

        return new ResponseEntity<>(transacaoResponseDTO, HttpStatus.CREATED);
    }
}