package com.digital.school.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Subject;
import com.digital.school.service.SubjectService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/subjects")
public class AdminSubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String showSubjects(Model model) {
        return "admin/subjects";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getSubjectsData() {
        return subjectService.findAllAsMap();
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> getSubjectsList() {
        return subjectService.findAllBasicInfo();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Subject> getSubject(@PathVariable Long id) {
        return subjectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createSubject(@RequestBody Subject subject) {
        try {
            Subject savedSubject = subjectService.save(subject);
            return ResponseEntity.ok(savedSubject);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        try {
            if (!subjectService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            subject.setId(id);
            Subject updatedSubject = subjectService.save(subject);
            return ResponseEntity.ok(updatedSubject);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}