package com.digital.school.controller.rest.secretary;

import com.digital.school.model.Student;
import com.digital.school.model.enumerated.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Attendance;
import com.digital.school.service.AttendanceService;
import com.digital.school.service.ClasseService;
import com.digital.school.service.CourseService;
import com.digital.school.service.UserService;

import java.util.Map;

@Controller
@RequestMapping("/secretary/absences")
public class AbsenceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ClasseService classeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showAbsences(Model model) {
        model.addAttribute("stats", attendanceService.getAbsenceStatistics());
        model.addAttribute("classes", classeService.findAllBasicInfo());
        model.addAttribute("students", userService.findByRole(RoleName.ROLE_STUDENT));
        model.addAttribute("todayCourses", courseService.findTodayCourses());
        return "secretary/absences";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createAbsence(@RequestBody Attendance absence) {
        try {
            Attendance saved = attendanceService.save(absence);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/validate")
    @ResponseBody
    public ResponseEntity<?> validateJustification(@PathVariable Long id) {
        try {
            Attendance validated = attendanceService.validateJustification(id);
            return ResponseEntity.ok(validated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectJustification(@PathVariable Long id) {
        try {
            Attendance rejected = attendanceService.rejectJustification(id);
            return ResponseEntity.ok(rejected);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/justification")
    public ResponseEntity<?> downloadJustification(@PathVariable Long id) {
        try {
            return attendanceService.getJustificationFile(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/remind")
    @ResponseBody
    public ResponseEntity<?> sendReminder(@PathVariable Long id) {
        try {
            attendanceService.sendAbsenceReminder(id);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Rappel envoyé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/courses/{studentId}")
    @ResponseBody
    public ResponseEntity<?> getStudentCourses(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(courseService.findTodayCoursesForStudent(studentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
