package br.acc.bank.dto.conta;

import java.math.BigDecimal;
import br.acc.bank.model.enums.TipoConta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaResponseDTO {
    private Long id;
    private Long numero;
    private BigDecimal saldo;
    private TipoConta tipo;
    private AgenciaContaResponseDTO agencia;
}
