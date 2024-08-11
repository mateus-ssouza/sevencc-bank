package br.acc.bank.dto.conta;

import br.acc.bank.model.enums.TipoConta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaRequestDTO {
    @NotNull(message = "O número da agência é obrigatório")
    @Positive(message = "O número da agência deve ser positivo")
    private Long numeroDaAgencia;

    @NotNull(message = "O tipo de transação é obrigatório")
    private TipoConta tipo;
}
