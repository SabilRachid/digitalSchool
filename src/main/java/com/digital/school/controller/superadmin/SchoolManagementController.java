package com.digital.school.controller.superadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.School;
import com.digital.school.service.SchoolService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/superadmin/schools")
public class SchoolManagementController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping
    public String showSchools(Model model) {
        return "superadmin/schools";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getSchoolsData() {
        return schoolService.findAllAsMap();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<School> getSchool(@PathVariable Long id) {
        return schoolService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createSchool(@RequestBody School school) {
        try {
            School savedSchool = schoolService.save(school);
            return ResponseEntity.ok(savedSchool);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSchool(@PathVariable Long id, @RequestBody School school) {
        try {
            if (!schoolService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            school.setId(id);
            School updatedSchool = schoolService.save(school);
            return ResponseEntity.ok(updatedSchool);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSchool(@PathVariable Long id) {
        try {
            schoolService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}