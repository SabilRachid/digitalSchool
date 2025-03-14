package com.digital.school.controller.rest.professor;

import com.digital.school.controller.ProfessorController;
import com.digital.school.dto.CourseDTO;
import com.digital.school.dto.UserDTO;
import com.digital.school.model.Course;
import com.digital.school.model.Professor;
import com.digital.school.model.Student;
import com.digital.school.model.User;
import com.digital.school.service.AttendanceService;
import com.digital.school.service.CourseService;
import com.digital.school.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/professor/api/courses")
public class ProfessorCourseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorCourseController.class);

    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<?> getCourses(@AuthenticationPrincipal Professor professor,
                                        @RequestParam(required = false) String month,
                                        @RequestParam(required = false) Long classe,
                                        @RequestParam(required = false) Long subject) {
        try {
            List<Course> courses = courseService.findCoursesByProfessor(professor.getId(), month, classe, subject);
            List<CourseDTO> dtoList = courses.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors du chargement des cours : " + e.getMessage())
            );
        }
    }


    /** Récupère la liste des cours sous forme de timeline pour affichage sur FullCalendar.
     * Les filtres (classeId, subjectId, startDate, endDate) sont optionnels. */
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

    /** Récupère les détails d'un cours par son ID.*/
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        Course course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        CourseDTO dto = convertToDTO(course);
        return ResponseEntity.ok(dto);
    }



    /** Récupère les cours d'aujourd'hui pour le professeur connecté.*/
    @GetMapping("/today")
    public ResponseEntity<List<Map<String, Object>>> getTodayCourses(@AuthenticationPrincipal Professor professor) {
        // Utilisation de LocalDate.now() dans le service pour obtenir les cours d'aujourd'hui.
        Object todayCourses = courseService.findTodaySchedule(professor);
        // On suppose ici que le service retourne une List<Map<String, Object>>
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> courses = (List<Map<String, Object>>) todayCourses;
        return ResponseEntity.ok(courses);
    }

    /** Crée un nouveau cours.*/
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @RequestBody CourseDTO courseDTO,
            @AuthenticationPrincipal User user) {
        LOGGER.debug("POST createCourse courseDTO:"+courseDTO.toString()+" from UserId="+user.getId());
        // Si l'ID du professeur n'est pas précisé dans le DTO, on l'utilise depuis le contexte d'authentification
        if (courseDTO.getProfessorId() == null) {
            courseDTO.setProfessorId(user.getId());
        }
        CourseDTO createdCourse = courseService.createCourse(courseDTO, user);
        return ResponseEntity.ok(createdCourse);
    }

    /** Met à jour un cours existant.*/
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id,
                                               @RequestBody Course course,
                                               @AuthenticationPrincipal Professor professor) {
        // On suppose que la méthode updateCourse gère les vérifications d'autorisation
        Course updated = courseService.updateCourse(id, course, professor);
        return ResponseEntity.ok(updated);
    }

    /** Supprime un cours par son ID.*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id,
                                                            @AuthenticationPrincipal Professor professor) {
        courseService.deleteCourseById(id, professor);
        return ResponseEntity.ok(Map.of("message", "Cours supprimé avec succès"));
    }

    /** Met à jour la salle d'un cours.*/
    @PutMapping("/{id}/room")
    public ResponseEntity<Course> updateCourseRoom(@PathVariable Long id,
                                                   @RequestBody Map<String, String> payload,
                                                   @AuthenticationPrincipal Professor professor) {
        String newRoom = payload.get("room");
        Course updated = courseService.updateCourseRoom(id, newRoom, professor);
        return ResponseEntity.ok(updated);
    }

    /** Publication d'un cours (change le statut en UPCOMING). */
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable Long id,@AuthenticationPrincipal User user ) {
        try {
            courseService.publishCourse(id, user);
            return ResponseEntity.ok(Map.of("message", "Cours publié avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la publication du cours : " + e.getMessage())
            );
        }
    }

    /** Récupère les étudiants associés à un cours.
     * L'implémentation ici suppose que le cours contient une liste de participants de type User.*/
    @GetMapping("/{courseId}/students")
    public ResponseEntity<?> getCourseStudents(@PathVariable Long courseId,
                                               @AuthenticationPrincipal Professor professor) {
        try {
            // Vérifier que le cours appartient au professeur connecté
            Course course = courseService.findById(courseId)
                     .orElseThrow(() -> new RuntimeException("Course non trouvé avec l'ID " + courseId));;
            if (course == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cours non trouvé ou accès non autorisé"));
            }

            // Récupérer les étudiants de la classe associée au cours
            List<Student> students = studentService.getStudentsByClasseId(course.getClasse().getId());
            // Convertir chaque User en UserDTO
            List<UserDTO> studentDTOs = students.stream().map(user -> {
                UserDTO dto = new UserDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setEnabled(user.isEnabled());
                // Pour les étudiants, on renseigne le champ classeId
                if (user.getClasse() != null) {
                    dto.setClasseId(user.getClasse().getId());
                }
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(studentDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la récupération des étudiants : " + e.getMessage()));
        }
    }

    /** Finalisation de la saisie de présence pour un cours (par exemple, marquer la saisie comme terminée).*/
    @PostMapping("/{id}/finalize")
    public ResponseEntity<?> finalizeCourse(@PathVariable Long id) {
        try {
            courseService.finalizeCourse(id);
            return ResponseEntity.ok(Map.of("message", "Saisie de présence terminée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Erreur lors de la finalisation du cours : " + e.getMessage())
            );
        }
    }

    /**
     * Enregistrement de la présence pour un cours.
     * L'objet JSON attendu contient, par exemple, l'ID du cours et un mapping des identifiants des étudiants à leur présence (true/false).
     */
    @PostMapping("/attendance")
    public ResponseEntity<?> recordAttendance(@RequestBody Map<String, Object> attendanceData) {
        try {
            LOGGER.debug("Enregistrement de la présence pour le cours {}: {}",
                    attendanceData.get("courseId"), attendanceData);
            // Appel du service qui va traiter et sauvegarder les enregistrements de présence.
            attendanceService.recordAttendance(attendanceData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'enregistrement de la présence", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    public CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setStatus(course.getStatus());
        dto.setSubjectId(course.getSubject() != null ? course.getSubject().getId() : null);
        dto.setProfessorId(course.getProfessor() != null ? course.getProfessor().getId() : null);
        dto.setClasseId(course.getClasse() != null ? course.getClasse().getId() : null);
        dto.setStartTime(course.getStartTime());
        dto.setEndTime(course.getEndTime());
       // dto.setRoom(course.getRoom());
        dto.setDescription(course.getDescription());
        return dto;
    }


}
