package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {
    private Long id;
    private String nome;
    private List<String> materiasNomes; // Lista de nomes das matérias (achatado)
    private Integer quantidadeAlunos;
    
    // Para criação/atualização (receber IDs)
    private List<Long> materiasIds; // ← JÁ TEM, mas precisa ser preenchido no toDTO
}