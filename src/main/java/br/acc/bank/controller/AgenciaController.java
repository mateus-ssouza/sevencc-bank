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

import br.acc.bank.dto.AgenciaRequestDTO;
import br.acc.bank.dto.AgenciaResponseDTO;
import br.acc.bank.model.Agencia;
import br.acc.bank.service.AgenciaService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agencia")
public class AgenciaController {
    @Autowired
    private AgenciaService agenciaService;

    @GetMapping
    public ResponseEntity<List<AgenciaResponseDTO>> getAllAgencias() {
        var agencias = agenciaService.getAll();
        var agenciasDTO = agencias.stream()
                .map(agencia -> MapperConverter.convertToDto(agencia, AgenciaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(agenciasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgenciaResponseDTO> getAgenciaById(@PathVariable Long id) {
        return agenciaService.getById(id)
                .map(agencia -> ResponseEntity.ok(MapperConverter.convertToDto(agencia, AgenciaResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgenciaResponseDTO> createAgencia(@Valid @RequestBody AgenciaRequestDTO agenciaRequestDTO,
            BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var agencia = MapperConverter.convertToEntity(agenciaRequestDTO, Agencia.class);
        var savedAgencia = agenciaService.create(agencia);
        var agenciaResponseDTO = MapperConverter.convertToDto(savedAgencia, AgenciaResponseDTO.class);
        return new ResponseEntity<>(agenciaResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgenciaResponseDTO> updateAgencia(@PathVariable Long id,
            @Valid @RequestBody AgenciaRequestDTO agenciaRequestDTO, BindingResult validateFields) {
        
        ValidationUtils.validateBindingResult(validateFields);
        var updatedAgencia = MapperConverter.convertToEntity(agenciaRequestDTO, Agencia.class);
        return ResponseEntity.ok(
                MapperConverter.convertToDto(agenciaService.update(id, updatedAgencia), AgenciaResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgencia(@PathVariable Long id) {
        agenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
