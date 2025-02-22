package com.digital.school.service;

import com.digital.school.model.Student;
import com.digital.school.model.StudentHomework;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des devoirs soumis par les étudiants.
 */
public interface StudentHomeworkService {

    /**
     * Récupère les devoirs assignés à l'étudiant pour les prochaines échéances.
     */
    List<StudentHomework> findUpcomingHomeworks(Student student);

    /**
     * Récupère les devoirs à réaliser dans les jours à venir pour l'étudiant.
     */
    List<StudentHomework> findHomeworksForNextDays(Student student);

    /**
     * Permet à l'étudiant de soumettre son devoir.
     *
     * @param homeworkId Identifiant du devoir assigné
     * @param file Fichier associé à la soumission (peut être null)
     * @param comment Commentaire éventuel lors de la soumission
     * @param student Étudiant qui soumet le devoir
     * @return La soumission enregistrée (StudentHomework)
     */
    StudentHomework submitHomework(Long homeworkId, MultipartFile file, String comment, Student student);

    /**
     * Récupère une soumission spécifique pour l'étudiant.
     */
    Optional<StudentHomework> findByIdAndStudent(Long id, Student student);
}
