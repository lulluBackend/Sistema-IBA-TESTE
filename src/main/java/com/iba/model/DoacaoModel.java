package com.iba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeDoador;

    private String email;
    
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDoacao tipo;

    private Double valor; // Para doações em dinheiro

    @Column(length = 500)
    private String descricaoItem; // Para doações de itens

    @Column(nullable = false)
    private LocalDate dataDoacao;

    @Enumerated(EnumType.STRING)
    private StatusDoacao status = StatusDoacao.CONFIRMADA;

    @Column(length = 500)
    private String observacoes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dataDoacao == null) {
            dataDoacao = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TipoDoacao {
        DINHEIRO("Dinheiro"),
        LIVROS("Livros"),
        MOVEIS("Móveis"),
        EQUIPAMENTOS("Equipamentos"),
        OUTROS("Outros");

        private final String descricao;

        TipoDoacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusDoacao {
        CONFIRMADA("✅ Confirmada"),
        PENDENTE("⏳ Pendente"),
        CANCELADA("❌ Cancelada");

        private final String descricao;

        StatusDoacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}