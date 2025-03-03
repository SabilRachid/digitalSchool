package com.digital.school.controller.rest.professor;

import com.digital.school.dto.AttendanceDTO;
import com.digital.school.dto.AttendanceRequest;
import com.digital.school.dto.UserDTO;
import com.digital.school.model.Attendance;
import com.digital.school.model.Course;
import com.digital.school.model.Professor;
import com.digital.school.service.AttendanceService;
import com.digital.school.service.CourseService;
import com.digital.school.service.StudentService;
import com.digital.school.service.ClasseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/professor/api/attendances")
public class ProfessorAttendanceRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorAttendanceRestController.class);

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClasseService classeService;

    @GetMapping("/classes/list")
    @ResponseBody
    public List<Map<String, Object>> getClassesList() {
        LOGGER.debug("getClassesList " + getClass().getName());
        return classeService.findAllBasicInfo();
    }

    // Récupération des cours du professeur pour une classe donnée et une date donnée (utile pour la gestion du modal)
    @GetMapping("/courses")
    @ResponseBody
    public List<Map<String, Object>> getCoursesForProfessor(
            @AuthenticationPrincipal Professor professor,
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        LOGGER.debug("getCoursesForProfessor - professorId: {}, classId: {}, date: {}", professor.getId(), classId, date);
        List<Course> courses = courseService.findCoursesForProfessorByClassAndDate(professor.getId(), classId, date);
        return courses.stream()
                .map(course -> Map.<String, Object>of("id", course.getId(), "name", course.getName()))
                .collect(Collectors.toList());
    }

    // Retourne les fiches d'attendance groupées pour les cours du jour et passés.
    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getGroupedAttendanceData(
            @AuthenticationPrincipal Professor professor,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        LOGGER.debug("GroupedAttendanceData - teacherId: {}, classId: {}, startDate: {}, endDate: {}",
                professor.getId(), classId, startDate, endDate);
        return attendanceService.getGroupedAttendanceData(professor, classId, startDate, endDate);
    }

    @GetMapping("/students/{classId}")
    @ResponseBody
    public List<Map<String, Object>> getStudentsByClass(@PathVariable Long classId) {
        LOGGER.debug("getStudentsByClass - classId: {}", classId);
        // Récupération d'une liste de UserDTO depuis le service
        List<UserDTO> students = studentService.getStudentsDtoByClasseId(classId);
        // Transformation en liste de Map
        return students.stream()
                .map(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", dto.getId());
                    map.put("firstName", dto.getFirstName());
                    map.put("lastName", dto.getLastName());
                    return map;
                })
                .collect(Collectors.toList());

    }


    /**
     * Endpoint pour récupérer les enregistrements individuels de StudentAttendance pour une fiche d'attendance.
     * L'endpoint doit renvoyer une liste d'objets contenant au moins studentId et status.
     */
    @GetMapping("/{attendanceId}/student-attendance")
    public List<Map<String, Object>> getStudentAttendances(@PathVariable Long attendanceId) {
        LOGGER.debug("getStudentAttendances - attendanceId: {}", attendanceId);
        // Le service renvoie ici une liste de Map (par exemple : [{ "studentId": 39, "status": "ABSENT" }, ...])
        return attendanceService.getStudentAttendances(attendanceId);
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Attendance> getAttendance(@PathVariable Long id,
                                                    @AuthenticationPrincipal Professor professor) {
        return attendanceService.findByIdAndTeacher(id, professor.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST endpoint pour saisir ou mettre à jour les enregistrements individuels (StudentAttendance)
     * pour une fiche d'attendance déjà existante (ou à créer) correspondant à un cours et une date.
     * Les informations (courseId, date, et la map attendances) proviennent du modal déclenché depuis la DataTable.
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createAttendance(@AuthenticationPrincipal Professor professor,
                                              @RequestBody AttendanceRequest request) {
        LOGGER.debug("==>createAttendance request : {}", request);
        try {
            LOGGER.debug("courseId Request = {}", request.getCourseId());
            AttendanceDTO savedAttendance = attendanceService.saveAttendance(request);
            return ResponseEntity.ok(savedAttendance);
        } catch (Exception e) {
            LOGGER.error("Erreur dans createAttendance", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAttendance(@PathVariable Long id,
                                              @AuthenticationPrincipal Professor professor,
                                              @RequestBody Attendance attendance) {
        try {
            if (!attendanceService.isTeacherAllowedToModify(professor.getId(), attendance.getCourse().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Non autorisé."));
            }

            if (!attendanceService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            attendance.setId(id);
            Attendance updatedAttendance = attendanceService.save(attendance);
            return ResponseEntity.ok(updatedAttendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id,
                                              @AuthenticationPrincipal Professor professor) {
        try {
            Attendance attendance = attendanceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fiche d'attendance introuvable"));

            if (!attendanceService.isTeacherAllowedToModify(professor.getId(), attendance.getCourse().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Non autorisé."));
            }

            attendanceService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}
