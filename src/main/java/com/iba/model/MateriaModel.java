package com.iba.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Remover o campo codigo - não precisamos mais dele!
    // O ID já serve como identificador único

    // Relacionamento ManyToMany com Curso (lado inverso)
    @ManyToMany(mappedBy = "materias", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CursoModel> cursos = new ArrayList<>();

    // Relacionamento OneToMany com Nota
    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NotaModel> notas = new ArrayList<>();

    public MateriaModel(String nome) {
        this.nome = nome;
    }
}