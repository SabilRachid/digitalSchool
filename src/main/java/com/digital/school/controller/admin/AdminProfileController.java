package com.digital.school.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.*;
import com.digital.school.service.*;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/profiles")
public class AdminProfileController {

    @Autowired
    private StudentProfileService studentProfileService;

    @Autowired
    private ParentProfileService parentProfileService;

    @Autowired
    private ParentStudentService parentStudentService;

    @GetMapping
    public String showProfiles(Model model) {
        return "admin/profiles";
    }

    // Student Profiles
    @GetMapping("/students/data")
    @ResponseBody
    public List<Map<String, Object>> getStudentProfilesData() {
        return studentProfileService.findAllAsMap();
    }

    @GetMapping("/students/{id}")
    @ResponseBody
    public ResponseEntity<?> getStudentProfile(@PathVariable Long id) {
        return studentProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/students")
    @ResponseBody
    public ResponseEntity<?> createStudentProfile(@RequestBody StudentProfile profile) {
        try {
            StudentProfile savedProfile = studentProfileService.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/students/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStudentProfile(@PathVariable Long id, @RequestBody StudentProfile profile) {
        try {
            profile.setId(id);
            StudentProfile updatedProfile = studentProfileService.save(profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    // Parent Profiles
    @GetMapping("/parents/data")
    @ResponseBody
    public List<Map<String, Object>> getParentProfilesData() {
        return parentProfileService.findAllAsMap();
    }

    @GetMapping("/parents/{id}")
    @ResponseBody
    public ResponseEntity<?> getParentProfile(@PathVariable Long id) {
        return parentProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/parents")
    @ResponseBody
    public ResponseEntity<?> createParentProfile(@RequestBody ParentProfile profile) {
        try {
            ParentProfile savedProfile = parentProfileService.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/parents/{id}")
    @ResponseBody
    public ResponseEntity<?> updateParentProfile(@PathVariable Long id, @RequestBody ParentProfile profile) {
        try {
            profile.setId(id);
            ParentProfile updatedProfile = parentProfileService.save(profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    // Parent-Student Associations
    @GetMapping("/associations/data")
    @ResponseBody
    public List<Map<String, Object>> getAssociationsData() {
        return parentStudentService.findAllAsMap();
    }

    @GetMapping("/associations/{id}")
    @ResponseBody
    public ResponseEntity<?> getAssociation(@PathVariable Long id) {
        return parentStudentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/associations")
    @ResponseBody
    public ResponseEntity<?> createAssociation(@RequestBody ParentStudent association) {
        try {
            ParentStudent savedAssociation = parentStudentService.save(association);
            return ResponseEntity.ok(savedAssociation);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/associations/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAssociation(@PathVariable Long id, @RequestBody ParentStudent association) {
        try {
            association.setId(id);
            ParentStudent updatedAssociation = parentStudentService.save(association);
            return ResponseEntity.ok(updatedAssociation);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @PutMapping("/associations/{id}/validate")
    @ResponseBody
    public ResponseEntity<?> validateAssociation(@PathVariable Long id) {
        try {
            ParentStudent validatedAssociation = parentStudentService.validate(id);
            return ResponseEntity.ok(validatedAssociation);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la validation: " + e.getMessage()));
        }
    }

    @DeleteMapping("/associations/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAssociation(@PathVariable Long id) {
        try {
            parentStudentService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}