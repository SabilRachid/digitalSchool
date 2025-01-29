package com.digital.school.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.digital.school.model.Classe;
import com.digital.school.service.ClasseService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/classes")
public class AdminClasseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminClasseController.class);

    @Autowired
    private ClasseService classeService;

    @GetMapping
    public String showClasses(HttpServletRequest request, Model model) {
        LOGGER.debug("showClasses called " + getClass().getName());
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/classes";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getClassesData() {
        LOGGER.debug("getClassesData " + getClass().getName());
         try {
             return classeService.findAllAsMap();
         }catch (Exception e) {
             LOGGER.error("Erreur lors du chargement des classes : " + e.getMessage());
             return null;
         }
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> getClassesList() {
        LOGGER.debug("getClassesList " + getClass().getName());
        return classeService.findAllBasicInfo();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Classe> getClass(@PathVariable Long id) {
        return classeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createClass(@RequestBody Classe classe) {
        try {
            Classe savedClasse = classeService.save(classe);
            return ResponseEntity.ok(savedClasse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la création: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody Classe classe) {
        try {
            if (!classeService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            classe.setId(id);
            Classe updatedClasse = classeService.save(classe);
            return ResponseEntity.ok(updatedClasse);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        try {
            classeService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}