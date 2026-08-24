package com.iba.repository;

import com.iba.model.MateriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<MateriaModel, Long> {
    
    // Remover métodos relacionados a "codigo"
    // Optional<MateriaModel> findByCodigo(String codigo);  // REMOVER
    // boolean existsByCodigo(String codigo);              // REMOVER
    
    Optional<MateriaModel> findByNome(String nome);
    boolean existsByNome(String nome);
    
    @Query("SELECT m FROM MateriaModel m LEFT JOIN FETCH m.cursos")
    List<MateriaModel> findAllWithCursos();
    
    @Query("SELECT COUNT(c) FROM CursoModel c JOIN c.materias m WHERE m.id = :materiaId")
    Integer countCursosByMateriaId(@Param("materiaId") Long materiaId);
}