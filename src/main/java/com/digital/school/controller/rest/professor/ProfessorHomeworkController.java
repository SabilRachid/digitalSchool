package com.digital.school.controller.rest.professor;

import com.digital.school.model.Homework;
import com.digital.school.model.Professor;
import com.digital.school.model.User;
import com.digital.school.service.HomeworkService;
import com.digital.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/professor/api/homeworks")
public class ProfessorHomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private CourseService courseService;



    @GetMapping("/data")
    public ResponseEntity<List<Map<String, Object>>> getHomeworks(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<Map<String, Object>> homeworks = homeworkService.findAllAsMap(classId, year, month);
        return ResponseEntity.ok(homeworks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Homework> getHomework(@PathVariable Long id,
                                                @AuthenticationPrincipal Professor professor) {
        Homework homework = homeworkService.findByIdAndProfessor(id, professor);
        return ResponseEntity.ok(homework);
    }

    @PostMapping
    public ResponseEntity<Homework> createHomework(@RequestBody Homework homework,
                                                   @AuthenticationPrincipal Professor professor) {
        Homework savedHomework = homeworkService.createHomework(homework, professor);
        return ResponseEntity.ok(savedHomework);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Homework> updateHomework(@PathVariable Long id,
                                                   @RequestBody Homework homework,
                                                   @AuthenticationPrincipal Professor professor) {
        Homework updatedHomework = homeworkService.updateHomework(id, homework, professor);
        return ResponseEntity.ok(updatedHomework);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHomework(@PathVariable Long id,
                                            @AuthenticationPrincipal Professor professor) {
        homeworkService.deleteHomework(id, professor);
        return ResponseEntity.ok().build();
    }




}
