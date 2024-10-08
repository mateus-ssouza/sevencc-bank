package br.acc.bank.dto.cliente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.acc.bank.dto.endereco.EnderecoResponseDTO;
import br.acc.bank.model.enums.UsuarioRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String dataNascimento;
    private String telefone;
    private String email;
    private String dataCadastro;
    private String login;
    private UsuarioRole role;
    private EnderecoResponseDTO endereco;

    public void setDataNascimento(LocalDate dataNascimento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataNascimento = dataNascimento.format(formatter);
    }
    
    public void setDataCadastro(LocalDate dataCadastro) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataCadastro = dataCadastro.format(formatter);
    }
}
