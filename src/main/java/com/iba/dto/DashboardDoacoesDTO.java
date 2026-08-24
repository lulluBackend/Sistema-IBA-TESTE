package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDoacoesDTO {
    private Long totalDoacoes;
    private Long totalConfirmadas;
    private Double totalDinheiro;
    private Map<String, Long> doacoesPorTipo = new LinkedHashMap<>();
    private List<DoacaoDTO> ultimasDoacoes;
}