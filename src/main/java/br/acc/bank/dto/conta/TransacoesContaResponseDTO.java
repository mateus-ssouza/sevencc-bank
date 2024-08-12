package br.acc.bank.dto.conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import br.acc.bank.model.enums.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransacoesContaResponseDTO {
    private BigDecimal valor;
    private TipoTransacao tipo;
    private String dataTransacao;
    private Optional<ContaDestinoResponseDTO> contaDestino;

    public void setDataTransacao(LocalDateTime dataTransacao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.dataTransacao = dataTransacao.format(formatter);
    }
}
