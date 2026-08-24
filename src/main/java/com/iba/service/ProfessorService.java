package com.iba.service;

import com.iba.dto.ProfessorDTO;
import com.iba.model.ProfessorModel;
import com.iba.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    private ProfessorDTO toDTO(ProfessorModel professor) {
        ProfessorDTO dto = new ProfessorDTO();
        dto.setId(professor.getId());
        dto.setNome(professor.getNome());
        dto.setCpf(professor.getCpf());
        dto.setRg(professor.getRg());
        dto.setEmail(professor.getEmail());
        dto.setTelefone(professor.getTelefone());
        dto.setEndereco(professor.getEndereco());
        dto.setCidade(professor.getCidade());
        dto.setEstado(professor.getEstado());
        dto.setCep(professor.getCep());
        dto.setBairro(professor.getBairro());
        dto.setNumero(professor.getNumero());
        dto.setComplemento(professor.getComplemento());
        dto.setDataNascimento(professor.getDataNascimento());
        dto.setIdade(professor.getIdade());
        dto.setArea(professor.getArea());
        dto.setEspecializacao(professor.getEspecializacao());
        dto.setDataContratacao(professor.getDataContratacao());
        dto.setStatus(professor.getStatus().toString());
        return dto;
    }

    public List<ProfessorDTO> findAll() {
        return professorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProfessorDTO findById(Long id) {
        ProfessorModel professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        return toDTO(professor);
    }

    @Transactional
    public ProfessorDTO create(ProfessorDTO professorDTO) {
        if (professorRepository.existsByCpf(professorDTO.getCpf())) {
            throw new RuntimeException("Já existe um professor com este CPF");
        }

        ProfessorModel professor = new ProfessorModel();
        professor.setNome(professorDTO.getNome());
        professor.setCpf(professorDTO.getCpf());
        professor.setRg(professorDTO.getRg());
        professor.setEmail(professorDTO.getEmail());
        professor.setTelefone(professorDTO.getTelefone());
        professor.setEndereco(professorDTO.getEndereco());
        professor.setCidade(professorDTO.getCidade());
        professor.setEstado(professorDTO.getEstado());
        professor.setCep(professorDTO.getCep());
        professor.setBairro(professorDTO.getBairro());
        professor.setNumero(professorDTO.getNumero());
        professor.setComplemento(professorDTO.getComplemento());
        professor.setDataNascimento(professorDTO.getDataNascimento());
        professor.setArea(professorDTO.getArea());
        professor.setEspecializacao(professorDTO.getEspecializacao());
        professor.setDataContratacao(professorDTO.getDataContratacao() != null ? professorDTO.getDataContratacao() : LocalDate.now());
        professor.setStatus(ProfessorModel.StatusProfessor.ATIVO);

        ProfessorModel saved = professorRepository.save(professor);
        return toDTO(saved);
    }

    @Transactional
    public ProfessorDTO update(Long id, ProfessorDTO professorDTO) {
        ProfessorModel professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        if (professorDTO.getNome() != null) professor.setNome(professorDTO.getNome());
        if (professorDTO.getCpf() != null) professor.setCpf(professorDTO.getCpf());
        if (professorDTO.getRg() != null) professor.setRg(professorDTO.getRg());
        if (professorDTO.getEmail() != null) professor.setEmail(professorDTO.getEmail());
        if (professorDTO.getTelefone() != null) professor.setTelefone(professorDTO.getTelefone());
        if (professorDTO.getEndereco() != null) professor.setEndereco(professorDTO.getEndereco());
        if (professorDTO.getCidade() != null) professor.setCidade(professorDTO.getCidade());
        if (professorDTO.getEstado() != null) professor.setEstado(professorDTO.getEstado());
        if (professorDTO.getCep() != null) professor.setCep(professorDTO.getCep());
        if (professorDTO.getBairro() != null) professor.setBairro(professorDTO.getBairro());
        if (professorDTO.getNumero() != null) professor.setNumero(professorDTO.getNumero());
        if (professorDTO.getComplemento() != null) professor.setComplemento(professorDTO.getComplemento());
        if (professorDTO.getDataNascimento() != null) professor.setDataNascimento(professorDTO.getDataNascimento());
        if (professorDTO.getArea() != null) professor.setArea(professorDTO.getArea());
        if (professorDTO.getEspecializacao() != null) professor.setEspecializacao(professorDTO.getEspecializacao());
        if (professorDTO.getStatus() != null) professor.setStatus(ProfessorModel.StatusProfessor.valueOf(professorDTO.getStatus()));

        ProfessorModel saved = professorRepository.save(professor);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new RuntimeException("Professor não encontrado");
        }
        professorRepository.deleteById(id);
    }
}