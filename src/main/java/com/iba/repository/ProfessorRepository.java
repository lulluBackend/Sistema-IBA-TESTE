package com.iba.repository;

import com.iba.model.ProfessorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<ProfessorModel, Long> {
    Optional<ProfessorModel> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    Optional<ProfessorModel> findByEmail(String email);
}