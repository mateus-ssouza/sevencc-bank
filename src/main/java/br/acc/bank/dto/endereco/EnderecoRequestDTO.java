package br.acc.bank.dto.endereco;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoRequestDTO {

    @NotNull(message = "O CEP é obrigatório")
    @Size(min = 8, max = 8, message = "O CEP deve ter exatamente 8 caracteres")
    private String cep;

    @NotNull(message = "A rua é obrigatória")
    @Size(min = 3, max = 60, message = "A rua deve ter entre 3 e 60 caracteres")
    private String rua;

    @NotNull(message = "O número é obrigatório")
    @Size(min = 1, max = 10, message = "O número deve ter entre 1 e 10 caracteres")
    private String numero;

    @NotNull(message = "O bairro é obrigatório")
    @Size(min = 3, max = 50, message = "O bairro deve ter entre 3 e 50 caracteres")
    private String bairro;

    @NotNull(message = "A cidade é obrigatória")
    @Size(min = 3, max = 50, message = "A cidade deve ter entre 3 e 50 caracteres")
    private String cidade;

    @NotNull(message = "O estado é obrigatório")
    @Size(min = 2, max = 60, message = "O estado deve ter entre 2 e 60 caracteres")
    private String estado;

    @NotNull(message = "O país é obrigatório")
    @Size(min = 2, max = 60, message = "O país deve ter entre 3 e 60 caracteres")
    private String pais;
}
