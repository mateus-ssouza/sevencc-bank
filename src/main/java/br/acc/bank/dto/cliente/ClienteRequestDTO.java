package br.acc.bank.dto.cliente;

import java.time.LocalDate;

import br.acc.bank.dto.endereco.EnderecoRequestDTO;
import br.acc.bank.util.PastDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

    @NotNull(message = "O nome é obrigatório")
    @Size(min = 3, max = 60, message = "O nome deve ter entre 3 e 60 caracteres")
    private String nome;

    @NotNull(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos")
    private String cpf;

    @NotNull(message = "O telefone é obrigatório")
    @Size(min = 10, max = 13, message = "O telefone deve ter entre 10 e 13 caracteres")
    private String telefone;

    @NotNull(message = "A data de nascimento é obrigatória")
    @PastDate(message = "A data de nascimento deve ser uma data no passado")
    private LocalDate dataNascimento;

    @NotNull(message = "O e-mail é obrigatório")
    @NotBlank(message = "O e-mail não deve estar em branco")
    @Email(message = "O e-mail deve ser válido")
    @Size(max = 60, message = "O e-mail deve ter no máximo 60 caracteres")
    private String email;

    @Valid
    @NotNull(message = "O endereço é obrigatório")
    private EnderecoRequestDTO endereco;
}
