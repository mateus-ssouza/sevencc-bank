package br.acc.bank.dto.agencia;

import br.acc.bank.dto.endereco.EnderecoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenciaRequestDTO {

    @NotNull(message = "O nome é obrigatório")
    @Size(min = 3, max = 45, message = "O nome deve ter entre 3 e 45 caracteres")
    private String nome;

    @NotNull(message = "O número é obrigatório")
    @Positive(message = "O número deve ser positivo")
    private Long numero;

    @NotNull(message = "O telefone é obrigatório")
    @Size(min = 10, max = 13, message = "O telefone deve ter entre 10 e 13 caracteres")
    private String telefone;

    @Valid
    @NotNull(message = "O endereço é obrigatório")
    private EnderecoRequestDTO endereco;
}
