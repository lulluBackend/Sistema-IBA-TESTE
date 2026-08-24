package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDTO {
    private String tipo;
    private Map<String, Object> dados;
    private List<Map<String, Object>> linhas;
    private Map<String, Double> totais;
}