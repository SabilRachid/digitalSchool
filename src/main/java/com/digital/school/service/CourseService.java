package com.digital.school.service;

import com.digital.school.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestion des cours.
 */
public interface CourseService {

    /**
     * Récupère tous les cours sous forme paginée.
     *
     * @param pageable les paramètres de pagination
     * @return une page de cours
     */
    Page<Course> findAll(Pageable pageable);

    /**
     * Récupère tous les cours.
     *
     * @return la liste de tous les cours
     */
    List<Course> findAll();

    /**
     * Récupère un cours par son identifiant.
     *
     * @param id l'identifiant du cours
     * @return un Optional contenant le cours s'il existe
     */
    Optional<Course> findById(Long id);

    /**
     * Sauvegarde ou met à jour un cours.
     *
     * @param course le cours à sauvegarder
     * @return le cours sauvegardé
     */
    Course save(Course course, User user);

    /**
     * Supprime un cours par son identifiant.
     *
     * @param id l'identifiant du cours à supprimer
     */
    void deleteById(Long id);

    /**
     * Vérifie si un cours existe pour un identifiant donné.
     *
     * @param id l'identifiant à vérifier
     * @return true si le cours existe, false sinon
     */
    boolean existsById(Long id);

    /**
     * Récupère la liste des cours programmés pour aujourd'hui pour un étudiant.
     *
     * @param student l'étudiant concerné
     * @return la liste des cours de la journée pour l'étudiant
     */
    List<Course> findTodaySchedule(Student student);

    /**
     * Récupère la liste des cours pour une classe donnée.
     *
     * @param classe la classe concernée
     * @return la liste des cours de la classe
     */
    List<Course> findByClasse(Classe classe);

    /**
     * Récupère les cours du professeur sous forme de Map (données enrichies pour affichage).
     *
     * @param professor le professeur concerné
     * @return la liste des cours sous forme de Map
     */
    List<Map<String, Object>> findByProfessor(Professor professor);

    /**
     * Récupère l'ensemble des cours sous forme de Map.
     *
     * @return la liste des cours enrichis sous forme de Map
     */
    List<Map<String, Object>> findAllAsMap();

    /**
     * Récupère les informations basiques de tous les cours sous forme de Map.
     *
     * @return la liste des cours avec leurs informations basiques
     */
    List<Map<String, Object>> findAllBasicInfo();

    /**
     * Récupère les cours pour un étudiant sous forme d'objet métier.
     *
     * @param student l'étudiant concerné
     * @return les cours de l'étudiant (peut être une liste ou un autre type de structure)
     */
    Object findByStudent(Student student);

    /**
     * Récupère les ressources associées à un cours sous forme de Map.
     *
     * @param id l'identifiant du cours
     * @return la liste des ressources associées au cours
     */
    List<Map<String, Object>> getCourseResourcesAsMap(Long id);

    /**
     * Génère un lien de réunion (meeting link) pour un cours.
     *
     * @param id l'identifiant du cours
     * @return un Map contenant le lien généré
     */
    Map<String, String> generateMeetingLink(Long id);

    /**
     * Récupère le planning des cours d'aujourd'hui pour un professeur.
     *
     * @param professor le professeur concerné
     * @return les cours d'aujourd'hui pour le professeur
     */
    Object findTodaySchedule(Professor professor);

    /**
     * Récupère les cours d'aujourd'hui (tous professeurs confondus ou filtré selon un critère).
     *
     * @return les cours d'aujourd'hui
     */
    Object findTodayCourses();

    Course updateCourse(Long id, Course course, Professor professor);

    void deleteCourseById(Long id, Professor professor);

    Course updateCourseRoom(Long id, String newRoom, Professor professor);

    /**
     * Récupère les cours d'aujourd'hui pour un étudiant donné.
     *
     * @param studentId l'identifiant de l'étudiant
     * @return les cours d'aujourd'hui pour cet étudiant
     */
    Object findTodayCoursesForStudent(Long studentId);

    List<Map<String, Object>> findByProfessorAndFilters(Professor professor, Long classeId, Long subjectId, String startDate, String endDate);


    List<Course> findCoursesForProfessorByClassAndDate(Long id, Long classId, LocalDate date);
}
