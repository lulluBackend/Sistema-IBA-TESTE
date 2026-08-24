package com.iba.repository;

import com.iba.model.DoacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoacaoRepository extends JpaRepository<DoacaoModel, Long> {
    
    List<DoacaoModel> findByStatus(DoacaoModel.StatusDoacao status);
    
    List<DoacaoModel> findByTipo(DoacaoModel.TipoDoacao tipo);
    
    List<DoacaoModel> findByDataDoacaoBetween(LocalDate inicio, LocalDate fim);
    
    @Query("SELECT SUM(d.valor) FROM DoacaoModel d WHERE d.tipo = 'DINHEIRO' AND d.status = 'CONFIRMADA'")
    Double sumValorDoacoesDinheiro();
    
    @Query("SELECT COUNT(d) FROM DoacaoModel d WHERE d.status = 'CONFIRMADA'")
    Long countDoacoesConfirmadas();
    
    @Query("SELECT d.tipo, COUNT(d) FROM DoacaoModel d WHERE d.status = 'CONFIRMADA' GROUP BY d.tipo")
    List<Object[]> countByTipo();
}