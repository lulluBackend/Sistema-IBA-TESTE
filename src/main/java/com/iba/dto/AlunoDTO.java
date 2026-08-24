package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private Long id;
    private String matricula;
    private String nome;
    private String cpf;
    private String rg;
    private String email;
    private String telefone;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String bairro;
    private String numero;
    private String complemento;
    private LocalDate dataNascimento;
    private Integer idade;
    private String naturalidade;
    private String nomePai;
    private String nomeMae;
    private LocalDate dataCadastro;
    private String status;
    // REMOVER média geral do aluno
    // private Double mediaGeral;
    private Integer totalMatriculas;
    private CursoDTO cursoAtual;
}