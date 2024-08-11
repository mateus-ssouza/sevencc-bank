package br.acc.bank.dto.auth;

import br.acc.bank.model.enums.UsuarioRole;

public record RegisterDTO(String login, String password, UsuarioRole role) {
}
