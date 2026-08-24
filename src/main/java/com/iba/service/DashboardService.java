package com.iba.service;

import com.iba.dto.DashboardDTO;
import com.iba.model.AlunoModel;
import com.iba.model.CursoModel;
import com.iba.model.MatriculaModel;
import com.iba.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;
    private final ProfessorRepository professorRepository;
    private final MatriculaRepository matriculaRepository;
    private final NotaRepository notaRepository;

    public DashboardDTO getDashboardData() {
        DashboardDTO dashboard = new DashboardDTO();
        
        // Totais básicos
        dashboard.setTotalAlunos(alunoRepository.count());
        dashboard.setTotalAlunosAtivos(alunoRepository.countAtivos());
        dashboard.setTotalCursos(cursoRepository.count());
        dashboard.setTotalMaterias(materiaRepository.count());
        dashboard.setTotalProfessores(professorRepository.count());
        
        // Distribuição de alunos por curso (baseado nas matrículas ATIVAS/CURSANDO)
        Map<String, Double> distribuicao = new LinkedHashMap<>();
        List<CursoModel> cursos = cursoRepository.findAll();
        
        for (CursoModel curso : cursos) {
            // Contar alunos com matrícula ATIVA neste curso (status CURSANDO)
            // Não usar distinct - queremos contar cada matrícula ativa
            long count = matriculaRepository.findAtivasByCurso(curso.getId()).size();
            if (count > 0) {
                distribuicao.put(curso.getNome(), (double) count);
                System.out.println("Curso: " + curso.getNome() + " - Alunos ativos: " + count);
            } else {
                System.out.println("Curso: " + curso.getNome() + " - Nenhum aluno ativo");
            }
        }
        dashboard.setDistribuicaoAlunosPorCurso(distribuicao);
        
        // Desempenho por curso (média de notas dos alunos APROVADOS)
        List<Map<String, Object>> desempenho = new ArrayList<>();
        for (CursoModel curso : cursos) {
            Map<String, Object> map = new HashMap<>();
            map.put("curso", curso.getNome());
            
            // Buscar todas as matrículas APROVADAS deste curso
            List<MatriculaModel> matriculasAprovadas = matriculaRepository.findByCursoId(curso.getId()).stream()
                    .filter(m -> m.getStatus() == MatriculaModel.StatusMatricula.APROVADO)
                    .collect(Collectors.toList());
            
            // Calcular média das notas finais das matrículas aprovadas
            double somaMedias = 0.0;
            for (MatriculaModel m : matriculasAprovadas) {
                somaMedias += m.getMediaFinal();
            }
            double media = matriculasAprovadas.isEmpty() ? 0.0 : somaMedias / matriculasAprovadas.size();
            map.put("media", media);
            desempenho.add(map);
            
            System.out.println("Curso: " + curso.getNome() + " - Média: " + media);
        }
        dashboard.setDesempenhoPorCurso(desempenho);
        
        // Alunos recentes (últimos 5 por data de cadastro)
        List<AlunoModel> alunosRecentes = alunoRepository.findAll().stream()
                .sorted((a1, a2) -> {
                    if (a1.getDataCadastro() == null || a2.getDataCadastro() == null) return 0;
                    return a2.getDataCadastro().compareTo(a1.getDataCadastro());
                })
                .limit(5)
                .collect(Collectors.toList());
        
        List<Map<String, Object>> alunosRecentesMap = new ArrayList<>();
        for (AlunoModel aluno : alunosRecentes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", aluno.getId());
            map.put("nome", aluno.getNome());
            map.put("matricula", aluno.getMatricula());
            
            // Buscar curso da última matrícula ativa
            String nomeCurso = "Não matriculado";
            if (aluno.getMatriculas() != null && !aluno.getMatriculas().isEmpty()) {
                // Buscar matrícula ativa primeiro
                Optional<MatriculaModel> matriculaAtiva = aluno.getMatriculas().stream()
                        .filter(m -> m.getStatus() == MatriculaModel.StatusMatricula.CURSANDO)
                        .findFirst();
                if (matriculaAtiva.isPresent()) {
                    nomeCurso = matriculaAtiva.get().getCurso().getNome();
                } else {
                    // Se não tiver ativa, pega a última
                    MatriculaModel ultimaMatricula = aluno.getMatriculas().get(aluno.getMatriculas().size() - 1);
                    nomeCurso = ultimaMatricula.getCurso().getNome();
                }
            }
            map.put("curso", nomeCurso);
            map.put("dataCadastro", aluno.getDataCadastro());
            alunosRecentesMap.add(map);
        }
        dashboard.setAlunosRecentes(alunosRecentesMap);
        
        // Taxa de aprovação geral
        List<MatriculaModel> todasMatriculas = matriculaRepository.findAll();
        long totalMatriculas = todasMatriculas.size();
        long aprovados = todasMatriculas.stream()
                .filter(m -> m.getStatus() == MatriculaModel.StatusMatricula.APROVADO)
                .count();
        dashboard.setTaxaAprovacao(totalMatriculas > 0 ? (aprovados * 100.0 / totalMatriculas) : 0.0);
        
        return dashboard;
    }
}