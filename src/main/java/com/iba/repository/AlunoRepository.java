package com.iba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.iba.model.AlunoModel;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<AlunoModel, Long> {
    
    Optional<AlunoModel> findByMatricula(String matricula);
    Optional<AlunoModel> findByCpf(String cpf);
    // REMOVER - aluno não tem mais curso diretamente
    // List<AlunoModel> findByCursoId(Long cursoId);
    List<AlunoModel> findByStatus(AlunoModel.StatusAluno status);
    boolean existsByMatricula(String matricula);
    boolean existsByCpf(String cpf);
    
    @Query("SELECT a FROM AlunoModel a WHERE a.nome LIKE %:nome%")
    List<AlunoModel> searchByNome(@Param("nome") String nome);
    
    @Query("SELECT COUNT(a) FROM AlunoModel a WHERE a.status = 'ATIVO'")
    Long countAtivos();
}