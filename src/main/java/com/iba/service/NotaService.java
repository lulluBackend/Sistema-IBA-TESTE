package com.iba.service;

import com.iba.dto.NotaDTO;
import com.iba.model.MatriculaModel;
import com.iba.model.MateriaModel;
import com.iba.model.NotaModel;
import com.iba.repository.MatriculaRepository;
import com.iba.repository.MateriaRepository;
import com.iba.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final MatriculaRepository matriculaRepository;
    private final MateriaRepository materiaRepository;

    private NotaDTO toDTO(NotaModel nota) {
        NotaDTO dto = new NotaDTO();
        dto.setId(nota.getId());
        dto.setValor(nota.getValor());
        dto.setCiclo(nota.getCiclo());
        dto.setMatriculaId(nota.getMatricula().getId());
        dto.setAlunoId(nota.getMatricula().getAluno().getId());
        dto.setAlunoNome(nota.getMatricula().getAluno().getNome());
        dto.setMateriaId(nota.getMateria().getId());
        dto.setMateriaNome(nota.getMateria().getNome());
        dto.setDataLancamento(nota.getDataLancamento());
        return dto;
    }

    public List<NotaDTO> findAll() {
        return notaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotaDTO> findByMatriculaId(Long matriculaId) {
        return notaRepository.findByMatriculaId(matriculaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotaDTO> findByAlunoId(Long alunoId) {
        List<MatriculaModel> matriculas = matriculaRepository.findByAlunoId(alunoId);
        return matriculas.stream()
                .flatMap(m -> m.getNotas().stream())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotaDTO> findByMateriaId(Long materiaId) {
        List<NotaModel> notas = notaRepository.findByMateriaId(materiaId);
        return notas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotaDTO save(NotaDTO notaDTO) {
        MatriculaModel matricula = matriculaRepository.findById(notaDTO.getMatriculaId())
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));
        
        MateriaModel materia = materiaRepository.findById(notaDTO.getMateriaId())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        // Validar nota entre 0 e 100
        if (notaDTO.getValor() < 0 || notaDTO.getValor() > 100) {
            throw new RuntimeException("Nota deve estar entre 0 e 100");
        }

        NotaModel nota = notaRepository.findByMatriculaIdAndMateriaIdAndCiclo(
                matricula.getId(), materia.getId(), notaDTO.getCiclo())
                .orElse(new NotaModel());

        nota.setValor(notaDTO.getValor());
        nota.setCiclo(notaDTO.getCiclo());
        nota.setMatricula(matricula);
        nota.setMateria(materia);
        nota.setDataLancamento(LocalDate.now());

        NotaModel saved = notaRepository.save(nota);
        
        // Verificar se todas as notas do período foram lançadas e atualizar status
        verificarConclusaoPeriodo(matricula);
        
        return toDTO(saved);
    }
    
    private void verificarConclusaoPeriodo(MatriculaModel matricula) {
        List<MateriaModel> materiasCurso = matricula.getCurso().getMaterias();
        List<NotaModel> notas = matricula.getNotas();
        
        boolean todasMateriasCompletas = true;
        for (MateriaModel materia : materiasCurso) {
            long ciclosLancados = notas.stream()
                    .filter(n -> n.getMateria().getId().equals(materia.getId()))
                    .count();
            if (ciclosLancados < 3) {
                todasMateriasCompletas = false;
                break;
            }
        }
        
        if (todasMateriasCompletas) {
            double mediaFinal = matricula.getMediaFinal();
            // Média para aprovação: 60 (alterado de 70 para 60)
            if (mediaFinal >= 60.0) {
                matricula.setStatus(MatriculaModel.StatusMatricula.APROVADO);
            } else {
                matricula.setStatus(MatriculaModel.StatusMatricula.REPROVADO);
            }
            matriculaRepository.save(matricula);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!notaRepository.existsById(id)) {
            throw new RuntimeException("Nota não encontrada");
        }
        notaRepository.deleteById(id);
    }
}