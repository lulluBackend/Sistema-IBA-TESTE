package com.iba.service;

import com.iba.dto.MatriculaDTO;
import com.iba.dto.NotaDTO;
import com.iba.model.AlunoModel;
import com.iba.model.CursoModel;
import com.iba.model.MatriculaModel;
import com.iba.model.NotaModel;
import com.iba.repository.AlunoRepository;
import com.iba.repository.CursoRepository;
import com.iba.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    private MatriculaDTO toDTO(MatriculaModel matricula) {
        MatriculaDTO dto = new MatriculaDTO();
        dto.setId(matricula.getId());
        dto.setAlunoId(matricula.getAluno().getId());
        dto.setAlunoNome(matricula.getAluno().getNome());
        dto.setAlunoMatricula(matricula.getAluno().getMatricula());
        dto.setCursoId(matricula.getCurso().getId());
        dto.setCursoNome(matricula.getCurso().getNome());
        dto.setAno(matricula.getAno());
        dto.setSemestre(matricula.getSemestre());
        dto.setDataMatricula(matricula.getDataMatricula());
        dto.setStatus(matricula.getStatus().toString());
        dto.setMediaFinal(matricula.getMediaFinal());
        
        if (matricula.getNotas() != null && !matricula.getNotas().isEmpty()) {
            dto.setNotas(matricula.getNotas().stream()
                .map(this::notaToDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private NotaDTO notaToDTO(NotaModel nota) {
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

    public List<MatriculaDTO> findAll() {
        return matriculaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MatriculaDTO> findByAlunoId(Long alunoId) {
        return matriculaRepository.findByAlunoId(alunoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MatriculaDTO findById(Long id) {
        MatriculaModel matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));
        return toDTO(matricula);
    }

    public List<MatriculaDTO> findByCursoAndPeriodo(Long cursoId, Integer ano, Integer semestre) {
        return matriculaRepository.findByCursoAndPeriodo(cursoId, ano, semestre).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MatriculaDTO> findAtivasByCurso(Long cursoId) {
        return matriculaRepository.findAtivasByCurso(cursoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatriculaDTO create(MatriculaDTO matriculaDTO) {
        AlunoModel aluno = alunoRepository.findById(matriculaDTO.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        CursoModel curso = cursoRepository.findById(matriculaDTO.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        
        // Verificar se já existe matrícula para este aluno no mesmo ano/semestre
        if (matriculaRepository.findByAlunoIdAndAnoAndSemestre(
                aluno.getId(), matriculaDTO.getAno(), matriculaDTO.getSemestre()).isPresent()) {
            throw new RuntimeException("Aluno já possui matrícula neste período");
        }
        
        MatriculaModel matricula = new MatriculaModel();
        matricula.setAluno(aluno);
        matricula.setCurso(curso);
        matricula.setAno(matriculaDTO.getAno());
        matricula.setSemestre(matriculaDTO.getSemestre());
        matricula.setDataMatricula(LocalDate.now());
        matricula.setCreatedAt(LocalDateTime.now());
        matricula.setStatus(MatriculaModel.StatusMatricula.CURSANDO);
        
        MatriculaModel saved = matriculaRepository.save(matricula);
        return toDTO(saved);
    }

    @Transactional
    public MatriculaDTO updateStatus(Long id, String status) {
        MatriculaModel matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));
        
        matricula.setStatus(MatriculaModel.StatusMatricula.valueOf(status));
        
        MatriculaModel saved = matriculaRepository.save(matricula);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        MatriculaModel matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));
        
        // Verificar se tem notas lançadas
        if (matricula.getNotas() != null && !matricula.getNotas().isEmpty()) {
            throw new RuntimeException("Não é possível excluir matrícula com notas já lançadas");
        }
        
        matriculaRepository.deleteById(id);
    }
}