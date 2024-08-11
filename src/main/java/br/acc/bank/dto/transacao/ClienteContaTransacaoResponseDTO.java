package br.acc.bank.dto.transacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteContaTransacaoResponseDTO {
    private String nome;
    private String cpf;
}
