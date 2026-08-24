package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDTO {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private String alunoMatricula;
    private Long cursoId;
    private String cursoNome;
    private Integer ano;
    private Integer semestre;
    private LocalDate dataMatricula;
    private String status;
    private Double mediaFinal;
    private List<NotaDTO> notas;
}