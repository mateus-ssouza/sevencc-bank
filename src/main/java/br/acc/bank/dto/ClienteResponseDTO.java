package br.acc.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private EnderecoResponseDTO endereco;
    private String dataCadastro;

    public void setDataCadastro(LocalDate dataCadastro) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataCadastro = dataCadastro.format(formatter);
    }
}
