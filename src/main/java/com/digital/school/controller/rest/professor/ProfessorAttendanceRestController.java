package com.digital.school.controller.rest.professor;


import com.digital.school.model.*;
import com.digital.school.service.AttendanceService;
import com.digital.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professor/api/attendances")
public class ProfessorAttendanceRestController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getAttendancesData(
            @AuthenticationPrincipal User professor,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        return attendanceService.findAllAsMapForProfessor(professor.getId(), classId, startDate, endDate);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Attendance> getAttendance(@PathVariable Long id, @AuthenticationPrincipal Professor professor) {

        return attendanceService.findByIdAndTeacher(id, professor.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createAttendance(@AuthenticationPrincipal Professor professor, @RequestBody Attendance attendance) {
        try {

            if (!attendanceService.isTeacherAllowedToModify(professor.getId(), attendance.getCourse().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Non autorisé."));
            }
            Attendance savedAttendance = attendanceService.save(attendance);
            return ResponseEntity.ok(savedAttendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAttendance(@PathVariable Long id,
                                              @AuthenticationPrincipal Professor professor,
                                              @RequestBody Attendance attendance) {
        try {
            if (!attendanceService.isTeacherAllowedToModify(professor.getId(), attendance.getCourse().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Non autorisé."));
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
                    .orElseThrow(() -> new RuntimeException("Feuille de présence introuvable"));

            if (!attendanceService.isTeacherAllowedToModify(professor.getId(), attendance.getCourse().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Non autorisé."));
            }

            attendanceService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}


