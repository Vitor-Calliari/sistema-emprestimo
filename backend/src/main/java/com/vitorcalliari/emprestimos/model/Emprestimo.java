package com.vitorcalliari.emprestimos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emprestimo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "moeda_codigo", nullable = false)
    private Moeda moeda;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "valor_obtido", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorObtido;

    @Column(name = "taxa_conversao", nullable = false, precision = 15, scale = 6)
    private BigDecimal taxaConversao;

    @Column(name = "valor_reais", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorReais;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "taxa_juros_mensal", nullable = false, precision = 6, scale = 4)
    private BigDecimal taxaJurosMensal;

    @Column(name = "valor_pagar_vencimento", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPagarVencimento;

    @Column(name = "criado_em", updatable = false, insertable = false)
    private LocalDateTime criadoEm;
}
