package br.acc.bank.util;

public abstract class Strings {

    public static final class AUTH {
        public static final String ERROR_CREDENTIALS = "login ou password inválidos.";
    }
    public static final class AGENCIA {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista de agências";
        public static final String NOT_FOUND = "Agência não encontrada.";
        public static final String CONFLICT = "Número da agência já utilizado.";
        public static final String DELETE_CONFLICT = "A agência está vinculada com uma ou mais contas.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter uma agência por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar uma agência.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar uma agência.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar uma agência.";
    }

    public static final class USER {
        public static final String CONFLICT = "Cpf, e-mail ou login do usuário já utilizado.";
    }

    public static final class CLIENTE {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista de clientes";
        public static final String NOT_FOUND = "Cliente não encontrado.";
        public static final String CONFLICT = "Cpf, e-mail ou login do cliente já utilizado.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter um cliente por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar um cliente.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar um cliente.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar um cliente.";
        public static final String DELETE_CONFLICT = "O cliente está vinculado a uma conta.";
        public static final String ERROR_MY_PROFILE = "Erro ao tentar obter detalhes do meu perfil.";
    }

    public static final class ADMIN {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista de admins";
        public static final String NOT_FOUND = "Admin não encontrado.";
        public static final String CONFLICT = "Cpf, e-mail ou login do admin já utilizado.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter um admin por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar um admin.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar um admin.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar um admin.";
    }

    public static final class CONTA {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista das contas";
        public static final String NOT_FOUND = "Conta não encontrada.";
        public static final String CONFLICT_ACCOUNT = "Já existe uma conta criada para esse usuário.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter uma conta por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar uma conta.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar uma conta.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar uma conta.";
        public static final String ERROR_NOT_ZEROED = "Conta deve está com saldo zerado.";
        public static final String ERROR_TYPE_INVALID = "Tipo de conta inválido, tipos válidos: [CORRENTE, POUPANCA].";
        public static final String ERROR_EXTRACT = "Erro ao tentar gerar o extrato da conta.";
        public static final String ERROR_DETAILS = "Erro ao tentar obter detalhes de uma conta.";
    }

    public static final class TRANSACAO {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista das transações";
        public static final String NOT_FOUND = "Transação não encontrada.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter uma transação por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar uma transação.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar uma transação.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar uma transação.";
        public static final String INVALID_TRANSACTION_VALUE = "O valor da transação deve ser positivo.";
        public static final String INSUFFICIENT_BALANCE = "Saldo insuficiente para a transação.";
        public static final String NOT_FOUND_ORIGIN = "Conta origem não encontrada.";
        public static final String NOT_FOUND_DESTINATION = "Conta destino não encontrada.";
        public static final String CONFLICT = "Conta origem e destino não podem ser a mesma.";
    }

    public static final class ERROR {
        public static final String GENERATE_TOKEN = "Erro ao gerar token.";
        public static final String INVALID_TOKEN = "Token inválido.";
        public static final String INVALID_TOKEN_FORMAT = "Token com formato inválido.";
    }
}