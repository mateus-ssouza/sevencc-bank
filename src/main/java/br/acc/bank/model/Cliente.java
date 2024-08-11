package br.acc.bank.model;

import java.time.LocalDate;

import br.acc.bank.model.enums.UsuarioRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "clientes")
public class Cliente extends Usuario {
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    public Cliente(String nome, String cpf, LocalDate dataNascimento, String telefone, String email,
            String login, String password, Endereco endereco) {
        super(nome, cpf, dataNascimento, telefone, email, login, password, UsuarioRole.USUARIO);
        this.endereco = endereco;
    }

}
