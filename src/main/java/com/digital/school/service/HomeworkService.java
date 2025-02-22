package com.digital.school.service;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestion des devoirs créés par les professeurs.
 */
public interface HomeworkService {

    List<Map<String, Object>> findAllAsMap(Long classId, Integer year, Integer month);

    Homework findByIdAndProfessor(Long id, Professor professor);

    Homework createHomework(Homework homework, Professor professor);

    Homework updateHomework(Long id, Homework homework, Professor professor);

    Optional<Homework> findById(Long id);

    ResponseEntity<String> deleteHomework(Long homeworkId, Professor professor);

    List<Homework> findHomeworksByProfessor(Professor professor);

    /**
     * Récupère la liste des devoirs en attente de correction pour le professeur.
     */
    List<Homework> findPendingGradingByProfessor(Professor professor);
}
