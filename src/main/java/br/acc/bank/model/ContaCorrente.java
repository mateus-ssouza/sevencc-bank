package br.acc.bank.model;

import java.math.BigDecimal;

import br.acc.bank.model.enums.TipoConta;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "contas_correntes")
public class ContaCorrente extends Conta {

    public ContaCorrente(Long id, Long numero, Agencia agencia) {
        super(id, numero, BigDecimal.ZERO, TipoConta.CORRENTE, agencia);
    }

}
