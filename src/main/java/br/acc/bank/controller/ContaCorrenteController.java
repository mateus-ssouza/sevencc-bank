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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.acc.bank.dto.ContaResponseDTO;
import br.acc.bank.dto.ContaRequestDTO;
import br.acc.bank.service.ContaCorrenteService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conta-corrente")
public class ContaCorrenteController {
    @Autowired
    private ContaCorrenteService contaCorrenteService;

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> getAllContas() {
        var contas = contaCorrenteService.getAll();
        var contasDTO = contas.stream()
                .map(conta -> MapperConverter.convertToDto(conta, 
                        ContaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(contasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> getContaById(@PathVariable Long id) {
        return contaCorrenteService.getById(id)
                .map(conta -> ResponseEntity.ok(MapperConverter.convertToDto(conta, 
                        ContaResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContaResponseDTO> createCliente(@Valid @RequestBody ContaRequestDTO contaRequestDTO,
            BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var savedConta = contaCorrenteService.create(contaRequestDTO);
        var contaResponseDTO = MapperConverter.convertToDto(savedConta, ContaResponseDTO.class);

        return new ResponseEntity<>(contaResponseDTO, HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgencia(@PathVariable Long id) {
            contaCorrenteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
