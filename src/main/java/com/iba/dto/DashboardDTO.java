package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Long totalAlunos;
    private Long totalAlunosAtivos;
    private Long totalCursos;
    private Long totalMaterias;
    private Long totalProfessores;
    // REMOVER - média geral não faz sentido misturar disciplinas
    // private Double mediaGeral;
    private Double taxaAprovacao;
    private List<Map<String, Object>> alunosRecentes;
    private Map<String, Double> distribuicaoAlunosPorCurso;
    private List<Map<String, Object>> desempenhoPorCurso;
}