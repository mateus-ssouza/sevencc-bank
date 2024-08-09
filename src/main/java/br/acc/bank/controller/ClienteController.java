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

import br.acc.bank.dto.ClienteRequestDTO;
import br.acc.bank.dto.ClienteResponseDTO;
import br.acc.bank.model.Cliente;
import br.acc.bank.service.ClienteService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> getAllClientes() {
        var clientes = clienteService.getAll();
        var clientesDTO = clientes.stream()
                .map(cliente -> MapperConverter.convertToDto(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getClienteById(@PathVariable Long id) {
        return clienteService.getById(id)
                .map(cliente -> ResponseEntity.ok(MapperConverter.convertToDto(cliente, ClienteResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> createCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO,
            BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var cliente = MapperConverter.convertToEntity(clienteRequestDTO, Cliente.class);
        var savedCliente = clienteService.create(cliente);
        var clienteResponseDTO = MapperConverter.convertToDto(savedCliente, ClienteResponseDTO.class);

        return new ResponseEntity<>(clienteResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> updateCliente(@PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteRequestDTO, BindingResult validateFields) {

        ValidationUtils.validateBindingResult(validateFields);
        var cliente = MapperConverter.convertToEntity(clienteRequestDTO, Cliente.class);
        var updatedCliente = clienteService.update(id, cliente);
        var clienteResponseDTO = MapperConverter.convertToDto(updatedCliente, ClienteResponseDTO.class);

        return ResponseEntity.ok(clienteResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
