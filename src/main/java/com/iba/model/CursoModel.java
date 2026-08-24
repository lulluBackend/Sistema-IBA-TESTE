package com.iba.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    // Relacionamento ManyToMany com Materia
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "curso_materia",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    private List<MateriaModel> materias = new ArrayList<>();

    // REMOVER este relacionamento - Aluno não tem mais curso diretamente
    // O curso agora é definido via MatriculaModel
    // @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    // @JsonIgnore
    // private List<AlunoModel> alunos = new ArrayList<>();
}