package com.digital.school.service;

import com.digital.school.dto.CourseDTO;
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



    /** Sauvegarde ou met à jour un cours.*/
    CourseDTO createCourse(CourseDTO courseDTO, User user);
    /** Met à jour le statut d’un cours à UPCOMING. */
    void publishCourse(Long courseId, User user);

    /** Finalise la saisie de présence d’un cours (par exemple, en passant le statut à COMPLETED). */
    void finalizeCourse(Long courseId);

    /** Enregistre la présence des élèves pour un cours.*/
    void recordAttendance(Map<String, Object> attendanceData);

    /** Récupère tous les cours sous forme paginée.*/
    Page<Course> findAll(Pageable pageable);

    /** Récupère tous les cours.*/
    List<Course> findAll();

    /** Récupère un cours par son identifiant.*/
    Optional<Course> findById(Long id);


    /** Supprime un cours par son identifiant.*/
    void deleteById(Long id);

    /** Vérifie si un cours existe pour un identifiant donné.*/
    boolean existsById(Long id);

    /** Récupère la liste des cours programmés pour aujourd'hui pour un étudiant.*/
    List<Course> findTodaySchedule(Student student);

    /** Récupère la liste des cours pour une classe donnée.*/
    List<Course> findByClasse(Classe classe);

    /** Récupère les cours du professeur sous forme de Map (données enrichies pour affichage) */
    List<Map<String, Object>> findByProfessor(Professor professor);

    /** Récupère l'ensemble des cours sous forme de Map.*/
    List<Map<String, Object>> findAllAsMap();

    /** Récupère les informations basiques de tous les cours sous forme de Map.*/
    List<Map<String, Object>> findAllBasicInfo();

    /** Récupère les cours pour un étudiant sous forme d'objet métier.*/
    Object findByStudent(Student student);

    /** Récupère les ressources associées à un cours sous forme de Map. */
    List<Map<String, Object>> getCourseResourcesAsMap(Long id);

    /** Génère un lien de réunion (meeting link) pour un cours */
    Map<String, String> generateMeetingLink(Long id);

    /** Récupère le planning des cours d'aujourd'hui pour un professeur.*/
    Object findTodaySchedule(Professor professor);

    /** Récupère les cours d'aujourd'hui (tous professeurs confondus ou filtré selon un critère).*/
    Object findTodayCourses();

    Course updateCourse(Long id, Course course, Professor professor);

    void deleteCourseById(Long id, Professor professor);

    Course updateCourseRoom(Long id, String newRoom, Professor professor);

    /** Récupère les cours d'aujourd'hui pour un étudiant donné. */
    Object findTodayCoursesForStudent(Long studentId);

    List<Map<String, Object>> findByProfessorAndFilters(Professor professor, Long classeId, Long subjectId, String startDate, String endDate);


    List<Course> findCoursesForProfessorByClassAndDate(Long id, Long classId, LocalDate date);


    List<Course> findCoursesByProfessor(Long professorId, String month, Long classe, Long subject);


    Course save(Course course, Administrator administrator);
}
