package com.iba.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aluno")
@Data
@NoArgsConstructor
public class AlunoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
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
    
    private String naturalidade;
    
    private String nomePai;
    
    private String nomeMae;
    
    private LocalDate dataCadastro;
    
    @Enumerated(EnumType.STRING)
    private StatusAluno status = StatusAluno.ATIVO;

    // REMOVER o campo curso - agora é definido via Matricula
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "curso_id", nullable = false)
    // private CursoModel curso;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MatriculaModel> matriculas = new ArrayList<>();

    @Transient
    private Integer idade;

    public Integer getIdade() {
        if (dataNascimento == null) return null;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
    
    @PrePersist
    protected void onCreate() {
        if (dataCadastro == null) {
            dataCadastro = LocalDate.now();
        }
        if (matricula == null) {
            matricula = "IBA" + System.currentTimeMillis();
        }
    }
    
    public enum StatusAluno {
        ATIVO, TRANCADO, FORMADO, DESLIGADO
    }
}