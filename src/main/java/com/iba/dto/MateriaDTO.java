package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDTO {
    private Long id;
    private String nome;
    private Integer quantidadeCursos;
    private Double mediaGeralNotas; // Isso é a média da matéria em TODOS os alunos - OK
}