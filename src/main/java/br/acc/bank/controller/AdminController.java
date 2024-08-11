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
import br.acc.bank.model.Admin;
import br.acc.bank.service.AdminService;
import br.acc.bank.util.MapperConverter;
import br.acc.bank.util.ValidationUtils;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
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
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }
}
