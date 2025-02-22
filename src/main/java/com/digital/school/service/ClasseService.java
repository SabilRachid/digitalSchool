package com.digital.school.service;

import com.digital.school.model.Classe;
import com.digital.school.model.Professor;

import java.util.List;
import java.util.Map;

public interface ClasseService {
    List<Map<String, Object>> findAllAsMap();
    List<Map<String, Object>> findAllBasicInfo();
    Classe findById(Long id);
    Classe save(Classe classe);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByNameAndSchoolYear(String name, String schoolYear);
    List<Classe> findByLevelId(Long levelId);
    long countStudents(Long classeId);
    List<Classe>  getAll();

    List<Classe> findByProfessor(Professor professor);
}