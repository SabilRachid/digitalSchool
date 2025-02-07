package com.digital.school.controller.rest.student;

```java
package com.digital.school.controller.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.model.User;
import com.digital.school.service.StudentAbsenceService;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/student/absences")
public class StudentAbsenceController {

    @Autowired
    private StudentAbsenceService absenceService;

    @GetMapping
    public String showAbsences(@AuthenticationPrincipal User student, Model model) {
        model.addAttribute("absences", absenceService.findStudentAbsences(student));
        model.addAttribute("stats", absenceService.getAbsenceStatistics(student));
        return "student/absences";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<?> getAbsenceData(
            @AuthenticationPrincipal User student,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(absenceService.findAbsencesByDateRange(student, start, end));
    }

    @PostMapping("/{id}/justify")
    @ResponseBody
    public ResponseEntity<?> submitJustification(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam String reason,
            @AuthenticationPrincipal User student) {
        try {
            absenceService.submitJustification(id, file, reason, student);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(absenceService.getAbsenceStatistics(student));
    }
}
```