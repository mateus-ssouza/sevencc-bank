package br.acc.bank.dto.transacao;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoRequestDTO {

    @NotNull(message = "O valor da transação é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor da transação deve ser positivo e maior que zero")
    private BigDecimal valor;

}