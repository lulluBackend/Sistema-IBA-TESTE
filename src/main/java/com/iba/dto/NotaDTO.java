package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaDTO {
    private Long id;
    private Double valor; // ← já é Double
    private Integer ciclo;
    private Long matriculaId;
    private Long alunoId;
    private String alunoNome;
    private Long materiaId;
    private String materiaNome;
    private LocalDate dataLancamento;
}