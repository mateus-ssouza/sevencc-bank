package br.acc.bank.dto.conta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaDestinoResponseDTO {
     private ClienteDestinoResponseDTO cliente;
}
