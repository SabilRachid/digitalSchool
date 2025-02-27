package com.digital.school.service;


import com.digital.school.model.Classe;
import com.digital.school.model.Professor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    List<Professor> findAll(); // 🔍 Récupérer tous les professeurs
    Optional<Professor> findById(Long id); // 🔍 Trouver un professeur par ID
    Professor save(Professor professor); // 💾 Sauvegarder un professeur
    void deleteById(Long id); // ❌ Supprimer un professeur
    boolean existsById(Long id); // ✅ Vérifier si un professeur existe
    List<Professor> findByClasse(Classe classe);

    List<Professor> findProfessorsByClasseId(Long classeId);
}
