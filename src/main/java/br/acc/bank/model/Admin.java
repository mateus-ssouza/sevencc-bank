package br.acc.bank.model;

import java.time.LocalDate;

import br.acc.bank.model.enums.UsuarioRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "admins")
public class Admin extends Usuario {
    
    public Admin(String nome, String cpf, LocalDate dataNascimento, String telefone, String email,
            String login, String password, Endereco endereco) {
        super(nome, cpf, dataNascimento, telefone, email, login, password, UsuarioRole.ADMIN);
    }

}
