package com.iba.repository;

import com.iba.model.NotaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<NotaModel, Long> {
    
    Optional<NotaModel> findByMatriculaIdAndMateriaIdAndCiclo(Long matriculaId, Long materiaId, Integer ciclo);
    
    List<NotaModel> findByMatriculaId(Long matriculaId);  // ← Verificar se este método existe
    
    List<NotaModel> findByMateriaId(Long materiaId);
    
    @Query("SELECT AVG(n.valor) FROM NotaModel n WHERE n.materia.id = :materiaId")
    Double calcularMediaPorMateria(@Param("materiaId") Long materiaId);
    
    @Query("SELECT n.materia.id, AVG(n.valor) FROM NotaModel n GROUP BY n.materia.id")
    List<Object[]> calcularMediaPorMateria();
    
    boolean existsByMatriculaIdAndMateriaIdAndCiclo(Long matriculaId, Long materiaId, Integer ciclo);
}