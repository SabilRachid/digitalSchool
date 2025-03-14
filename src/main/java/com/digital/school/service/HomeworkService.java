package com.digital.school.service;

import com.digital.school.dto.HomeworkDTO;
import com.digital.school.model.Homework;
import com.digital.school.model.Parent;
import com.digital.school.model.Professor;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestion des devoirs créés par les professeurs.
 */
public interface HomeworkService {

    Homework createHomework(HomeworkDTO homeworkDTO, Long professorId);
    void publishHomework(Long homeworkId);
    void endHomework(Long homeworkId);
    void enterGrade(Long submissionId, Double gradeValue, String comment, Long professorId);
    List<Homework> findHomeworksByProfessor(Long professorId, String month, Long classe, Long subject);
    byte[] generateHomeworkReport(Long homeworkId);
    List<Map<String, Object>> findAllAsMap(Long classId, Integer year, Integer month);
    Homework findByIdAndProfessor(Long id, Professor professor);
    Optional<Homework> findById(Long id);
    HomeworkDTO convertToDTO(Homework homework);

    List<Homework> findPendingGradingByProfessor(Professor professor);


    /** Récupère les devoirs de tous les enfants du parent, sous forme de liste de Map.*/
    List<Map<String, Object>> getChildrenHomework(Parent parent);

    /** Retourne les détails complets des devoirs d'un enfant, identifié par son ID.*/
    Map<String, Object> getDetailedChildHomework(Long childId);

    /** Retourne les statistiques associées aux devoirs d'un enfant (moyennes, taux d'achèvement, etc.).*/
    Map<String, Object> getChildHomeworkStats(Long childId);

    /** Envoie un rappel pour un devoir dont la soumission est en attente.*/
    void sendHomeworkReminder(Long homeworkId);

    List<Map<String, Object>> getHomeworkGrades(Long id);
}
