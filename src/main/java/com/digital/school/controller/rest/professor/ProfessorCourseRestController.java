package com.digital.school.controller.rest.professor;

import com.digital.school.model.Course;
import com.digital.school.model.Professor;
import com.digital.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professor/api/courses")
public class ProfessorCourseRestController {

    @Autowired
    private CourseService courseService;

    /**
     * Récupère la liste des cours sous forme de timeline pour affichage sur FullCalendar.
     * Les filtres (classeId, subjectId, startDate, endDate) sont optionnels.
     */
    @GetMapping("/timeline")
    public ResponseEntity<List<Map<String, Object>>> getTimelineCourses(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // On peut obtenir le professeur depuis le contexte de sécurité si nécessaire.
        // Ici, nous appelons la méthode qui retourne les cours filtrés sous forme de Map.
        List<Map<String, Object>> coursesData = courseService.findByProfessorAndFilters(null, classeId, subjectId, startDate, endDate);
        return ResponseEntity.ok(coursesData);
    }

    /**
     * Récupère les cours d'aujourd'hui pour le professeur connecté.
     */
    @GetMapping("/today")
    public ResponseEntity<List<Map<String, Object>>> getTodayCourses(@AuthenticationPrincipal Professor professor) {
        // Utilisation de LocalDate.now() dans le service pour obtenir les cours d'aujourd'hui.
        Object todayCourses = courseService.findTodaySchedule(professor);
        // On suppose ici que le service retourne une List<Map<String, Object>>
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> courses = (List<Map<String, Object>>) todayCourses;
        return ResponseEntity.ok(courses);
    }

    /**
     * Récupère les détails d'un cours par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau cours.
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course,
                                               @AuthenticationPrincipal Professor professor) {
        // L'entité Course doit être liée au professeur connecté.
        course.setProfessor(professor);
        Course created = courseService.save(course, professor);
        return ResponseEntity.ok(created);
    }

    /**
     * Met à jour un cours existant.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id,
                                               @RequestBody Course course,
                                               @AuthenticationPrincipal Professor professor) {
        // On suppose que la méthode updateCourse gère les vérifications d'autorisation
        Course updated = courseService.updateCourse(id, course, professor);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un cours par son ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id,
                                                            @AuthenticationPrincipal Professor professor) {
        courseService.deleteCourseById(id, professor);
        return ResponseEntity.ok(Map.of("message", "Cours supprimé avec succès"));
    }

    /**
     * Met à jour la salle d'un cours.
     */
    @PutMapping("/{id}/room")
    public ResponseEntity<Course> updateCourseRoom(@PathVariable Long id,
                                                   @RequestBody Map<String, String> payload,
                                                   @AuthenticationPrincipal Professor professor) {
        String newRoom = payload.get("room");
        Course updated = courseService.updateCourseRoom(id, newRoom, professor);
        return ResponseEntity.ok(updated);
    }
}
