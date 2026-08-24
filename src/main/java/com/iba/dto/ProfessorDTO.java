package com.iba.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {
    private Long id;
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
    private String area;
    private String especializacao;
    private LocalDate dataContratacao;
    private String status;
}