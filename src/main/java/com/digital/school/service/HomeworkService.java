package com.digital.school.service;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/* Service de gestion des devoirs */
public interface HomeworkService {

    List<Map<String, Object>> findAllAsMap();

    void createHomework(Professor professor, Homework homework);

    void deleteHomework(Long id);

    List<Homework> findHomeworksByProfessor(Professor professor);

    void updateHomework(Homework homework, Homework updatedHomework);

    /* Récupère la liste des devoirs en attente d'un étudiant. */
    List<Homework> findPendingHomework(Student student);

    /* Récupère la liste des devoirs soumis par un étudiant. */
    List<Homework> findSubmittedHomework(Student student);

    /* Récupère la liste des devoirs notés d'un étudiant. */
    List<Homework> findGradedHomework(Student student);

    /* Recherche un devoir par son identifiant. */
    Optional<Homework> findById(Long id);

    /* Permet à un étudiant de soumettre un devoir avec un fichier et un commentaire. */
    Homework submitHomework(Long id, Student student, MultipartFile file, String comment);

    /* Récupère le fichier soumis pour un devoir. */
    ResponseEntity<?> getSubmissionFile(Long id);

    /* Récupère la liste des devoirs en attente de correction par un professeur. */
    List<Homework> findPendingGradingByProfessor(Professor professor);

    /* Permet à un professeur de noter un devoir. */
    Homework gradeHomework(Long homeworkId, Professor professor, double grade, String feedback);

    /* Récupère la liste des devoirs donnés par un professeur. */
    List<Homework> findHomeworkByProfessor(Professor professor);

    /* Supprime un devoir donné par un professeur. */
    ResponseEntity<String> deleteHomework(Long homeworkId, Professor professor);



}