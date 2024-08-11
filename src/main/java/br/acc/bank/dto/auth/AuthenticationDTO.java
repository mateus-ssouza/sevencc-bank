package br.acc.bank.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDTO {
    @NotNull(message = "O login é obrigatório")
    @Size(min = 3, max = 60, message = "O login deve ter entre 3 e 60 caracteres")
    private String login;

    @NotNull(message = "O password é obrigatório")
    @Size(min = 6, max = 25, message = "O password deve ter entre 6 e 25 caracteres")
    private String password;
}
