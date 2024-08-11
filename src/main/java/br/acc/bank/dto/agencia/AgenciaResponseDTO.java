package br.acc.bank.dto.agencia;

import br.acc.bank.dto.endereco.EnderecoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenciaResponseDTO {
    private Long id;
    private String nome;
    private Long numero;
    private String telefone;
    private EnderecoResponseDTO endereco;
}
