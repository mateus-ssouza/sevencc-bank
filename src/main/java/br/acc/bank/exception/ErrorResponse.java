package br.acc.bank.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Schema(description = "Detalhes do erro")
public class ErrorResponse {
    @Schema(description = "Código de status HTTP")
    private int status;
    @Schema(description = "Mensagem de erro")
    private String message;
    @Schema(description = "Causa de erro")
    private String cause;
}
