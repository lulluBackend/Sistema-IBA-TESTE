package com.iba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "professor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
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
    
    private String area;
    
    private String especializacao;
    
    private LocalDate dataContratacao;
    
    @Enumerated(EnumType.STRING)
    private StatusProfessor status = StatusProfessor.ATIVO;
    
    @Transient
    private Integer idade;

    public Integer getIdade() {
        if (dataNascimento == null) return null;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
    
    @PrePersist
    protected void onCreate() {
        if (dataContratacao == null) {
            dataContratacao = LocalDate.now();
        }
    }
    
    public enum StatusProfessor {
        ATIVO, AFASTADO, DESLIGADO
    }
}