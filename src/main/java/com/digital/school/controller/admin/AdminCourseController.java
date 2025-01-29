package com.digital.school.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Course;
import com.digital.school.service.CourseService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AdminCourseController.class);

    @Autowired
    private CourseService courseService;




    @GetMapping
    public String showCourses(HttpServletRequest request, Model model) {
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/courses";
    }

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
/*
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            // Vérification que les ID sont bien présents
            if (courseDTO.getSubject() == null || courseDTO.getProfessor() == null || courseDTO.getClasse() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Les ID de la matière, du professeur et de la classe sont obligatoires"));
            }

            // Récupération des entités associées
            Subject subject = subjectService.findById(courseDTO.getSubject()).orElseThrow(() -> new IllegalArgumentException("Matière introuvable"));
            User professor = userService.findById(courseDTO.getProfessor()).orElseThrow(() -> new IllegalArgumentException("Professeur introuvable"));
            Classe classe = classeService.findById(courseDTO.getClasse()).orElseThrow(() -> new IllegalArgumentException("Classe introuvable"));

            // Création du cours
            Course course = new Course();
            course.setSubject(subject);
            course.setProfessor(professor);
            course.setClasse(classe);
            course.setStartTime(courseDTO.getStartTime());
            course.setEndTime(courseDTO.getEndTime());
            course.setRoom(courseDTO.getRoom());
            course.setDescription(courseDTO.getDescription());

            Course savedCourse = courseService.save(course);
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }
*/

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        LOGGER.debug("📌 Requête reçue : {}", course);  // 🔍 Affiche le contenu de course

        try {

            LOGGER.debug("subjects id" + course.getSubject().getName());
            LOGGER.debug("professors id" + course.getProfessor().getFirstName());
            LOGGER.debug("classes id" + course.getClasse().getName());

            LOGGER.debug("subjects Name" + course.getSubject().getName());
            LOGGER.debug("professors firstName" + course.getProfessor().getFirstName());
            LOGGER.debug("classes name" + course.getClasse().getName());
            if (course.getSubject() == null || course.getSubject().getId() == null ||
                    course.getProfessor() == null || course.getProfessor().getId() == null ||
                    course.getClasse() == null || course.getClasse().getId() == null) {
                LOGGER.error("🚨 ID manquant pour subject, professor ou class !");
                return ResponseEntity.badRequest().body(Map.of("message", "Les ID de la matière, du professeur et de la classe sont obligatoires"));
            }

            Course savedCourse = courseService.save(course);
            return ResponseEntity.ok(savedCourse);
        } catch (Exception e) {
            LOGGER.error("🚨 Erreur lors de la création du cours : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }



    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            if (!courseService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            course.setId(id);
            Course updatedCourse = courseService.save(course);
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