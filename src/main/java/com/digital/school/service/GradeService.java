package com.digital.school.service;


import com.digital.school.model.StudentGrade;
import java.util.List;
import java.util.Map;

public interface GradeService {

       int calculateStudentRank(Long studentId, Long classeId);

    /**
     * Récupère les évaluations groupées en fonction des filtres donnés.
     * Les filtres incluent : classeId, subjectId, evaluationType (HOMEWORK ou EXAM) et evaluationDate (format YYYY-MM-DD).
     * @param classeId l'identifiant de la classe (facultatif)
     * @param subjectId l'identifiant de la matière (facultatif)
     * @param evaluationType le type d'évaluation (facultatif)
     * @param evaluationDate la date de l'évaluation (facultatif)
     * @return une liste de Map contenant par exemple : {id, subjectName, evaluationType, classeName, eventDate, completed}
     */
    List<Map<String, Object>> findGroupedEvaluations(Long classeId, Long subjectId, String evaluationType, String evaluationDate);

    /**
     * Récupère la liste des notes pour une évaluation donnée.
     * @param evaluationId l'identifiant de l'évaluation
     * @return une liste de Map contenant {studentId, studentName, value, comments}
     */
    List<Map<String, Object>> findGradesForEvaluation(Long evaluationId);

    /**
     * Enregistre ou met à jour en masse les notes pour une évaluation donnée.
     * Chaque objet de la liste "updates" doit contenir au minimum : studentId, value et éventuellement comments.
     * @param evaluationId l'identifiant de l'évaluation
     * @param updates la liste des mises à jour des notes
     */
    void saveGrades(Long evaluationId, List<Map<String, Object>> updates);

    /**
     * Génère le rapport des notes pour une évaluation (optionnellement filtré par matière) au format PDF.
     * @param evaluationId l'identifiant de l'évaluation
     * @param subjectId facultatif, permet de filtrer par matière
     * @return un tableau d'octets contenant le PDF généré
     */
    byte[] generateGradeReport(Long evaluationId, Long subjectId);
}

