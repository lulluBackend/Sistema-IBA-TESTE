package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoacaoDTO {
    private Long id;
    private String nomeDoador;
    private String email;
    private String telefone;
    private String tipo;
    private String tipoDescricao;
    private Double valor;
    private String descricaoItem;
    private LocalDate dataDoacao;
    private String status;
    private String statusDescricao;
    private String observacoes;
    private String dataFormatada;
    private String valorFormatado;
}