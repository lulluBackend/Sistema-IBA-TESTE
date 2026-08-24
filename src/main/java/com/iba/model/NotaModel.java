package com.iba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "nota", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"matricula_id", "materia_id", "ciclo"})
})
@Data
@NoArgsConstructor
public class NotaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private MatriculaModel matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private MateriaModel materia;

    @Column(nullable = false)
    private Integer ciclo;

    @Column(nullable = false)
    private Double valor; // 0 a 100
    
    private LocalDate dataLancamento;
    
    @PrePersist
    protected void onCreate() {
        dataLancamento = LocalDate.now();
    }
}