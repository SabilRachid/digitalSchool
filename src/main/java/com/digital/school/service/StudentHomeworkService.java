package com.digital.school.service;

import com.digital.school.model.Homework;
import com.digital.school.model.Student;
import com.digital.school.model.EvaluationGrade;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des devoirs soumis par les étudiants.
 */
public interface StudentHomeworkService {

    /**
     * Récupère les devoirs assignés à l'étudiant pour les prochaines échéances.
     *
     * @param student l'étudiant concerné
     * @return la liste des soumissions (EvaluationGrade) associées aux devoirs à venir
     */
    List<Homework> findUpcomingHomeworks(Student student);

    /**
     * Récupère les devoirs à réaliser dans les jours à venir pour l'étudiant.
     *
     * @param student l'étudiant concerné
     * @return la liste des soumissions (EvaluationGrade) pour les devoirs à venir
     */
    List<Homework> findHomeworksForNextDays(Student student);

    /**
     * Permet à l'étudiant de soumettre son devoir.
     *
     * @param homeworkId Identifiant du devoir assigné (correspondant à l'ID de Homework)
     * @param file Fichier associé à la soumission (peut être null)
     * @param comment Commentaire éventuel lors de la soumission
     * @param student Étudiant qui soumet le devoir
     * @return La soumission enregistrée sous forme d'EvaluationGrade
     */
    EvaluationGrade submitHomework(Long homeworkId, MultipartFile file, String comment, Student student);

    /**
     * Récupère une soumission spécifique pour l'étudiant.
     *
     * @param id Identifiant de la soumission
     * @param student l'étudiant concerné
     * @return La soumission trouvée, si elle existe
     */
    Optional<EvaluationGrade> findByIdAndStudent(Long id, Student student);
}
