package com.iba.service;

import com.iba.dto.CursoDTO;
import com.iba.model.CursoModel;
import com.iba.model.MateriaModel;
import com.iba.repository.CursoRepository;
import com.iba.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;

    private CursoDTO toDTO(CursoModel curso) {
        CursoDTO dto = new CursoDTO();
        dto.setId(curso.getId());
        dto.setNome(curso.getNome());
        
        if (curso.getMaterias() != null && !curso.getMaterias().isEmpty()) {
            // Preencher nomes das matérias para exibição
            dto.setMateriasNomes(curso.getMaterias().stream()
                    .map(MateriaModel::getNome)
                    .collect(Collectors.toList()));
            
            // ← ADICIONAR ESTA LINHA: Preencher os IDs das matérias
            dto.setMateriasIds(curso.getMaterias().stream()
                    .map(MateriaModel::getId)
                    .collect(Collectors.toList()));
        }
        
        dto.setQuantidadeAlunos(0); // Será implementado depois
        
        return dto;
    }

    public List<CursoDTO> findAll() {
        return cursoRepository.findAllWithMaterias().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CursoDTO findById(Long id) {
        CursoModel curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        return toDTO(curso);
    }

    @Transactional
    public CursoDTO create(CursoDTO cursoDTO) {
        if (cursoRepository.existsByNome(cursoDTO.getNome())) {
            throw new RuntimeException("Já existe um curso com este nome");
        }

        CursoModel curso = new CursoModel();
        curso.setNome(cursoDTO.getNome());

        if (cursoDTO.getMateriasIds() != null && !cursoDTO.getMateriasIds().isEmpty()) {
            List<MateriaModel> materias = materiaRepository.findAllById(cursoDTO.getMateriasIds());
            curso.setMaterias(materias);
        }

        CursoModel saved = cursoRepository.save(curso);
        return toDTO(saved);
    }

    @Transactional
    public CursoDTO update(Long id, CursoDTO cursoDTO) {
        CursoModel curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        curso.setNome(cursoDTO.getNome());

        if (cursoDTO.getMateriasIds() != null) {
            List<MateriaModel> materias = materiaRepository.findAllById(cursoDTO.getMateriasIds());
            curso.setMaterias(materias);
        }

        CursoModel saved = cursoRepository.save(curso);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        cursoRepository.deleteById(id);
    }
}