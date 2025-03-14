package com.digital.school.controller.rest.admin;

import com.digital.school.dto.CourseDTO;
import com.digital.school.model.*;
import com.digital.school.model.enumerated.CourseStatus;
import com.digital.school.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/api/courses")
public class AdminCourseRestController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdminCourseRestController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ClasseService classeService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getCoursesData() {
        return courseService.findAllAsMap();
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> getCoursesList() {
        return courseService.findAllBasicInfo();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createCourse(@RequestBody Map<String, Object> requestData, @AuthenticationPrincipal Administrator administrator) {
        LOGGER.debug("📌 Requête reçue : {}", requestData);

        try {
            // Vérification des ID
            Long subjectId = requestData.get("subject") != null ? ((Map<String, Object>) requestData.get("subject")).get("id") != null ? Long.parseLong(((Map<String, Object>) requestData.get("subject")).get("id").toString()) : null : null;
            Long professorId = requestData.get("professor") != null ? ((Map<String, Object>) requestData.get("professor")).get("id") != null ? Long.parseLong(((Map<String, Object>) requestData.get("professor")).get("id").toString()) : null : null;
            Long classId = requestData.get("class") != null ? ((Map<String, Object>) requestData.get("class")).get("id") != null ? Long.parseLong(((Map<String, Object>) requestData.get("class")).get("id").toString()) : null : null;

            if (subjectId == null || professorId == null || classId == null) {
                LOGGER.error("🚨 ID manquant pour subject, professor ou class !");
                return ResponseEntity.badRequest().body(Map.of("message", "Les ID de la matière, du professeur et de la classe sont obligatoires"));
            }

            // Récupération des entités associées
            Subject subject = subjectService.findById(subjectId).orElseThrow(() -> new RuntimeException("Matière non trouvée"));
            Professor professor = professorService.findById(professorId).orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
            //Room room = roomService.findById(professorId).orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
            Optional<Classe> classe = classeService.findById(classId);

            // Construction de l'objet Course
            Course course = new Course();
            course.setSubject(subject);
            course.setProfessor(professor);
            course.setClasse(classe.orElse(null));
            course.setStatus(CourseStatus.UPCOMING);

            // Vérification des valeurs null avant de les convertir en String
            course.setName(requestData.get("name") != null ? requestData.get("name").toString() : "Nom par défaut");
            course.setDescription(requestData.get("description") != null ? requestData.get("description").toString() : "");
           // course.setRoom(requestData.get("room") != null ? requestData.get("room").toString() : "");

            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setTitle(course.getTitle());
            courseDTO.setSubjectId(course.getSubject().getId());
            courseDTO.setProfessorId(course.getProfessor().getId());
            courseDTO.setClasseId(course.getClasse().getId());
            courseDTO.setStartTime(course.getStartTime());
            courseDTO.setEndTime(course.getEndTime());
            courseDTO.setDescription(course.getDescription());
            CourseDTO savedCourseDTO = courseService.createCourse(courseDTO, administrator);
            // Sauvegarde du cours
            return ResponseEntity.ok(savedCourseDTO);
        } catch (Exception e) {
            LOGGER.error("🚨 Erreur lors de la création du cours : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }



    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course, @AuthenticationPrincipal Administrator administrator) {
        try {
            if (!courseService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            course.setId(id);
            Course updatedCourse = courseService.save(course, administrator);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}