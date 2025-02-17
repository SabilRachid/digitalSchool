package com.digital.school.controller.rest.admin;

import com.digital.school.model.Classe;
import com.digital.school.model.School;
import com.digital.school.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/settings")
public class SettingsController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping
    public ResponseEntity<School> getSchools() {
        School school = schoolService.getSchools();
        return ResponseEntity.ok(school);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateSetting(@PathVariable Long id, @RequestBody School school) {
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
}