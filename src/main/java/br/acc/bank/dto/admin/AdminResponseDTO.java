package br.acc.bank.dto.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.acc.bank.model.enums.UsuarioRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponseDTO {
private Long id;
    private String nome;
    private String cpf;
    private String dataNascimento;
    private String telefone;
    private String email;
    private String dataCadastro;
    private String login;
    private UsuarioRole role;

    public void setDataNascimento(LocalDate dataNascimento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataNascimento = dataNascimento.format(formatter);
    }
    
    public void setDataCadastro(LocalDate dataCadastro) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataCadastro = dataCadastro.format(formatter);
    }
}
