package br.acc.bank.util;

public abstract class Strings {
    public static final class AGENCIA {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista de agências";
        public static final String NOT_FOUND = "Agência não encontrada.";
        public static final String CONFLICT = "Número da agência já utilizado.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter uma agência por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar uma agência.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar uma agência.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar uma agência.";
    }

    public static final class CLIENTE {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista de clientes";
        public static final String NOT_FOUND = "Cliente não encontrado.";
        public static final String CONFLICT = "Cpf ou e-mail do cliente já utilizado.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter um cliente por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar um cliente.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar um cliente.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar um cliente.";
    }

    public static final class CONTA {
        public static final String ERROR_FIND_ALL_LIST = "Erro ao obter a lista das contas";
        public static final String NOT_FOUND = "Conta não encontrada.";
        public static final String CONFLICT = "Número da conta já utilizado.";
        public static final String ERROR_FIND_BY_ID = "Erro ao tentar obter uma conta por seu ID.";
        public static final String ERROR_CREATE = "Erro ao tentar criar uma conta.";
        public static final String ERROR_UPDATE = "Erro ao tentar atualizar uma conta.";
        public static final String ERROR_DELETE = "Erro ao tentar deletar uma conta.";
        public static final String ERROR_NOT_ZEROED = "Conta deve está com saldo zerado.";
        public static final String ERROR_TYPE_INVALID = "Tipo de conta inválido, tipos válidos: [CORRENTE, POUPANCA].";
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
}