package com.digital.school.service.impl;

import com.digital.school.model.Classe;
import com.digital.school.model.Professor;
import com.digital.school.repository.ProfessorRepository;
import com.digital.school.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    @Override
    public Optional<Professor> findById(Long id) {
        return professorRepository.findById(id);
    }

    @Override
    public Professor save(Professor professor) {
        return professorRepository.save(professor);
    }

    @Override
    public void deleteById(Long id) {
        professorRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return professorRepository.existsById(id);
    }

    @Override
    public List<Professor> findByClasse(Classe classe) {
        return professorRepository.findByClasse(classe);
    }


}
