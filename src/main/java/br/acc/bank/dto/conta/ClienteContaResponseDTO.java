package br.acc.bank.dto.conta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteContaResponseDTO {
    private String nome;
    private String cpf;
}
