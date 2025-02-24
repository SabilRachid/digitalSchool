package com.digital.school.service;

import com.digital.school.model.Evaluation;
import com.digital.school.model.Professor;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EvaluationService {

    Optional<Evaluation> findById(Long id);

    List<Evaluation> findAllByProfessor(Professor professor);

    Evaluation createEvaluation(Evaluation evaluation, Professor professor);

    Evaluation updateEvaluation(Long id, Evaluation evaluation, Professor professor);

    void deleteEvaluation(Long id, Professor professor);

    /**
     * Retourne les évaluations groupées selon les filtres optionnels.
     * Les filtres incluent l'ID de la classe, l'ID de la matière, le type d'évaluation et la date.
     */
    List<Map<String, Object>> findGroupedEvaluations(Long classeId, Long subjectId, String evaluationType, String startDate, String endDate);



    /**
     * Récupère la liste des notes pour une évaluation donnée.
     * Chaque entrée du retour est une map contenant par exemple : "studentId", "studentName", "value" et "comments".
     */
    List<Map<String, Object>> findGradesForEvaluation(Long evaluationId);

    /**
     * Enregistre (ou met à jour) les notes pour une évaluation donnée.
     * Le paramètre updates est une liste de maps, chacune devant contenir au moins "studentId", "value" et éventuellement "comments".
     */
    void saveGrades(Long evaluationId, List<Map<String, Object>> updates);

    /**
     * Génère un rapport PDF des notes pour une évaluation.
     * Un paramètre subjectId optionnel peut être utilisé pour filtrer le rapport.
     */
    byte[] generateGradeReport(Long evaluationId, Long subjectId);

}
