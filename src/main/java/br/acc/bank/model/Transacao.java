package br.acc.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.acc.bank.model.enums.TipoTransacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transacoes")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private LocalDateTime dataTransacao;

    @ManyToOne
    @JoinColumn(name = "conta_origem_id", nullable = false)
    private Conta contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id", nullable = true)
    private Conta contaDestino;

    @PrePersist
    protected void onCreate() {
        if (dataTransacao == null) {
            dataTransacao = LocalDateTime.now();
        }
    }

    public Transacao(BigDecimal valor, TipoTransacao tipo, Conta contaOrigem) {
        this.valor = valor;
        this.tipo = tipo;
        this.contaOrigem = contaOrigem;
    }

    public Transacao(BigDecimal valor, TipoTransacao tipo, Conta contaOrigem, Conta contaDestino) {
        this.valor = valor;
        this.tipo = tipo;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
    }
}