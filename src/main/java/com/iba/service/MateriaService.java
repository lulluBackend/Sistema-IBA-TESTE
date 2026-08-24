package com.iba.service;

import com.iba.dto.MateriaDTO;
import com.iba.model.MateriaModel;
import com.iba.repository.MateriaRepository;
import com.iba.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final NotaRepository notaRepository;

    private MateriaDTO toDTO(MateriaModel materia) {
        MateriaDTO dto = new MateriaDTO();
        dto.setId(materia.getId());  // ID é o código
        dto.setNome(materia.getNome());
        dto.setQuantidadeCursos(materiaRepository.countCursosByMateriaId(materia.getId()));
        dto.setMediaGeralNotas(notaRepository.calcularMediaPorMateria(materia.getId()));
        return dto;
    }

    public List<MateriaDTO> findAll() {
        return materiaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MateriaDTO findById(Long id) {
        MateriaModel materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));
        return toDTO(materia);
    }

    @Transactional
    public MateriaDTO create(MateriaDTO materiaDTO) {
        // Verificar se já existe matéria com mesmo nome
        // Opcional: você pode adicionar validação de nome duplicado se quiser
        
        MateriaModel materia = new MateriaModel();
        materia.setNome(materiaDTO.getNome());

        MateriaModel saved = materiaRepository.save(materia);
        return toDTO(saved);
    }

    @Transactional
    public MateriaDTO update(Long id, MateriaDTO materiaDTO) {
        MateriaModel materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        materia.setNome(materiaDTO.getNome());

        MateriaModel saved = materiaRepository.save(materia);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new RuntimeException("Matéria não encontrada");
        }
        materiaRepository.deleteById(id);
    }
}